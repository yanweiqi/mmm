package com.mex.bidder.engine.ranking.impl;

import com.google.api.client.util.Sets;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.constants.TaType;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.engine.ranking.AdRanking;
import com.mex.bidder.engine.redis.JedisService;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.engine.util.RtbHelper;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.BaseTa;
import com.mex.bidder.protocol.TaIdPackageData;
import com.mex.bidder.protocol.TaTargetingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * 最高价算法
 * <p>
 * User: donghai
 * Date: 2016/11/16
 */
public class RandomWithTaPriceAdRanking implements AdRanking {
    private static final Logger logger = LoggerFactory.getLogger(RandomWithTaPriceAdRanking.class);

    public static final String NAME = "TopFixPrice";


    @Inject
    private JedisService jedisService;


    @Inject
    private MexDataContext mexDataContext;

    private final Random random = new Random();


    @Override
    public <T extends Ad> AdAndPricePair process(List<T> candidates, BidRequest bidRequest, BidResponse bidResponse) {
        AdAndPricePair adAndPricePair;
        if (candidates == null || candidates.isEmpty()) {
            adAndPricePair = AdAndPricePair.EMPTY;
        } else {
            adAndPricePair = getAdAndPriceWithTaPriceFactor(candidates, bidRequest, bidResponse);
        }

        return adAndPricePair;
    }


    private <T extends Ad> AdAndPricePair getAdAndPriceWithTaPriceFactor(List<T> candidates, BidRequest bidRequest, BidResponse bidResponse) {
        String deviceId = RtbHelper.getDeviceId(bidRequest);

        // 随机选中广告
        Ad selectAd = candidates.get(random.nextInt(candidates.size()));

        // 记录最大出价因子
        BaseTa maxPriceRatioTa = TaTargetingData.TaGroupMapping.EMPTY;

        // 广告组关联的ta
        TaTargetingData taTargetingData = mexDataContext.getTaTargetingData();
        if (Objects.nonNull(taTargetingData)) {
            List<TaTargetingData.TaGroupMapping> taGroupMappings = taTargetingData.get(String.valueOf(selectAd.getAdGroupId()));

            // 本次请求合成redis ta key
            Set<String> redisHashFields = Sets.newHashSet();
            //List<TaTargetingData.TaGroupMapping> idPackageTaGroupList = Lists.newArrayList();  //  记录TA ID包定义
            Map<String, TaTargetingData.TaGroupMapping> keyToAdGroupMappingMap = Maps.newHashMap(); // 反向映射，下面要通过key找group使用

            taGroupMappings.forEach(taGroupMapping -> {
                TaType taType = TaType.lookup(taGroupMapping.getTaType());
                if (taType == TaType.HISTORY_AD) {
                    String key = "clk_" + taGroupMapping.getAdverId() + "_" + taGroupMapping.getProductId()
                            + "_" + taGroupMapping.getCampaignId() + "_" + taGroupMapping.getAdGroupId();
                    keyToAdGroupMappingMap.put(key, taGroupMapping);
                    redisHashFields.add(key);
                    // } else if (taType == TaType.DEVICE_ID) {
                    //     idPackageTaGroupList.add(taGroupMapping);
                } else {
                    logger.error("unknown taType={}", taGroupMapping.getTaType());
                }
            });

            if (redisHashFields.size() > 0) {
                // 查询ta key是否在redis中存在
                String redisHashKey = "ta_h_" + deviceId;

                List<String> taList = jedisService.hmget(redisHashKey, redisHashFields);
                logger.info("redis hashKey=" + redisHashKey + ", field=" + redisHashFields.toString() + ", taList=" + taList.toString());
                Map<String, Integer> taMap = MexUtil.zipToMap(redisHashFields, taList);

                // 计算出影响出价的历史广告ta组
                for (Map.Entry<String, Integer> entry : taMap.entrySet()) {
                    String key = entry.getKey();
                    Integer redisVal = entry.getValue();
                    if (redisVal != null && redisVal != 0) { // v != 0 代表key中人群中存在
                        TaTargetingData.TaGroupMapping taGroupMapping = keyToAdGroupMappingMap.get(key);
                        if (maxPriceRatioTa.getPriceRatio() < taGroupMapping.getPriceRatio()) {
                            maxPriceRatioTa = taGroupMapping;
                        }
                    }
                }
            }
        }

        // 计算出影响出价的ID包ta组
        TaIdPackageData taIdPackageData = mexDataContext.getTaIdPackageData();
        List<TaIdPackageData.TaIdMapping> taIdMappingList = taIdPackageData.get(selectAd.getAdGroupId());

        //request的设备id集
        Set<String> targetIds = getTargetId(bidRequest);
        for (TaIdPackageData.TaIdMapping taGroupMapping : taIdMappingList) {
            if (taIdPackageData.contains(taGroupMapping.getTaId(), targetIds)) {
                logger.info("idpackage targetIds={}", targetIds.toString());
                if (maxPriceRatioTa.getPriceRatio() < taGroupMapping.getPriceRatio()) {
                    maxPriceRatioTa = taGroupMapping;
                }
            }
        }

        // 再按ta系数放大价格
        int adGroupId = selectAd.getAdGroupId();
        BigDecimal finalPrice = multiply(selectAd.getAdxBidPrice(), maxPriceRatioTa.getPriceRatio());

        logger.info("adgroupid={}, adxBidPrice={}, finalPrice={}, taid={}, priceRatio={}"
                , adGroupId, selectAd.getAdxBidPrice(), finalPrice, maxPriceRatioTa.getTaId(), maxPriceRatioTa.getPriceRatio());

        // 记录使用的ta分组，在生成监测链接时使用
        bidResponse.setBaseTa(maxPriceRatioTa);

        return AdAndPricePair.create(selectAd, finalPrice);
    }

    private Set<String> getTargetId(BidRequest bidRequest) {
        OpenRtb.BidRequest.Device device = bidRequest.openRtb().getDevice();
        Set<String> deviceIds = Sets.newHashSet();
        //imei
        if (device.hasExtension(MexOpenRtbExt.imei)) {
            deviceIds.add(device.getExtension(MexOpenRtbExt.imei));
        }
        //imei md5
        if (device.hasDidmd5()) {
            deviceIds.add(device.getDidmd5());
        }
        //imei sha1
        if (device.hasDidsha1()) {
            deviceIds.add(device.getDidsha1());
        }

        //androidid
        if (device.hasExtension(MexOpenRtbExt.androidId)) {
            deviceIds.add(device.getExtension(MexOpenRtbExt.androidId));
        }
        //androidid md5
        if (device.hasDpidmd5()) {
            deviceIds.add(device.getDpidmd5());
        }
        //androidid sha1
        if (device.hasDpidsha1()) {
            deviceIds.add(device.getDpidsha1());
        }
        // mac
        if (device.hasExtension(MexOpenRtbExt.mac)) {
            deviceIds.add(device.getExtension(MexOpenRtbExt.mac));
        }
        //mac md5
        if (device.hasMacmd5()) {
            deviceIds.add(device.getMacmd5());
        }
        //mac sha1
        if (device.hasMacsha1()) {
            deviceIds.add(device.getMacsha1());
        }
        //idfa
        if (device.hasExtension(MexOpenRtbExt.idfa)) {
            deviceIds.add(device.getExtension(MexOpenRtbExt.idfa));
        }

        deviceIds.add(device.getIp());

        return deviceIds;
    }

    static BigDecimal multiply(BigDecimal adxBidPrice, double priceRatio) {
        return adxBidPrice.multiply(new BigDecimal(priceRatio));
    }

}
