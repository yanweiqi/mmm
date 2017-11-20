package com.mex.bidder.engine.filter.impl;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.api.client.util.Sets;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.AdListFilter;
import com.mex.bidder.engine.redis.JedisService;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.engine.util.RtbHelper;
import com.mex.bidder.protocol.Ad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 频次
 * User: donghai
 * Date: 2016/11/17
 */
public class FrequencyCappingFilter implements AdListFilter {
    private static final Logger logger = LoggerFactory.getLogger(FrequencyCappingFilter.class);

    @Inject
    private JedisService jedisService;

    private static final String FREQ = "freq_";
    private static final String IMP = "i_";
    private static final String CLK = "c_";

    @Override
    public <B extends Ad> List<B> filter(List<B> adList, BidRequest bidRequest, BidResponse bidResponse) {

        if (adList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> fields = Sets.newHashSet();
        String deviceId = RtbHelper.getDeviceId(bidRequest);
        // 曝光频次id和对象映射的Map
        Map<String, List<B>> keyToAdListMap = Maps.newHashMap();

        adList.forEach(ad -> {
            List<B> reqAdList;
            if (ad.isImpFreqCap()) {
                // 曝光频次限制
//                String impKey = ad.getAdCampaignId() + "__imp__" + ad.getImpFreqCapTimeUnit();
                String impKey = FREQ + IMP + ad.getAdCampaignId() + "_" + deviceId;
                fields.add(impKey);

                reqAdList = keyToAdListMap.putIfAbsent(impKey, Lists.newArrayList());
                if (reqAdList == null) {
                    reqAdList = keyToAdListMap.get(impKey);
                }


                reqAdList.add(ad);
            }
            if (ad.isClkFreqCap()) {
                // 点击频次限制
//                fields.add(ad.getAdCampaignId() + "__click__" + ad.getClkFreqCapTimeUnit());
                String clkKey = FREQ + CLK + ad.getAdCampaignId() + "_" + deviceId;
                fields.add(clkKey);


                reqAdList = keyToAdListMap.putIfAbsent(clkKey, Lists.newArrayList());
                if (reqAdList == null) {
                    reqAdList = keyToAdListMap.get(clkKey);
                }

                reqAdList.add(ad);
            }
        });

        // TODO
        if (fields.isEmpty()) {
            // 不限制频次
            return adList;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> freqList = jedisService.hget(fields);

        logger.info("redis-time " + stopwatch);

        Map<String, Integer> freqMap = MexUtil.zipToMap(fields, freqList);

        return doFilter(bidResponse, keyToAdListMap, freqMap);
    }

    /**
     * 根据频次过滤
     *
     * @param bidResponse
     * @param keyToAdListMap 频次的key和对应的对象映射
     * @param freqMap        频次的key和对应的次数映射
     * @param <B>
     * @return
     */
    private <B extends Ad> List<B> doFilter(BidResponse bidResponse, Map<String,
            List<B>> keyToAdListMap, Map<String, Integer> freqMap) {
        Set<B> result = Sets.newHashSet();

        boolean flag = true;
        // 33_i_key
        // 33_c_key
        // 355_c_key
        // 355_i_key
        Iterator<Map.Entry<String, List<B>>> it = keyToAdListMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<B>> entry = it.next();
            String key = entry.getKey();
            Ad ad = entry.getValue().get(0);

            if (key.contains("_i_"))
            {
                if (freqMap.get(key) >= ad.getImpFreqCapCnt()) {
                    flag = false;
                }
            }
            if (key.contains("_c_")){
                if (freqMap.get(key) >= ad.getClkFreqCapCnt()) {
                    flag = false;
                }
            }
        }

        // 展示和点击  同时满足频次要求
        Set<String> keySet = keyToAdListMap.keySet();
        if (flag) {
            keySet.forEach(v -> {
                result.addAll(keyToAdListMap.get(v));
            });
        }else {
            keySet.forEach(v -> {
                Ad ad = keyToAdListMap.get(v).get(0);
                bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_FreCtrl);
            });

        }
        return Lists.newArrayList(result);
    }


}