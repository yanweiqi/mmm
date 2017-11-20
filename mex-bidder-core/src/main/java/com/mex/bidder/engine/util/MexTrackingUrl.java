package com.mex.bidder.engine.util;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.http.util.*;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.constants.MexCurrency;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.Banner;
import com.mex.bidder.protocol.Const;
import io.vertx.core.json.JsonObject;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 检测URL工具类
 * <p>
 * User: donghai
 * Date: 2016/11/21
 */
public abstract class MexTrackingUrl {

    private static final Logger logger = LoggerFactory.getLogger(MexTrackingUrl.class);


    //mex曝光监测地址
    protected final String IMP_TRACK_URL;
    //mex点击监测
    protected final String CLICK_TRACK_URL;
    //第三方曝光监测  TODO
//    protected final String THIRD_IMP_TRACK_URL;
    //mex赢价
    protected final String WINNOTICE_URL;

    // 通过查询串
    public final String QUERY_STRING = "requestid=${requestid}&adgroupid=${adgroupid}" +
            "&netid=${netid}&netname=${netname}" + "&devicetype=${devicetype}&os=${os}" +
            "&connectiontype=${connectiontype}" + "&material_id=${material_id}&idfa=${idfa}" +
            "&android_id=${android_id}&android_id_md5=${android_id_md5}&android_id_sha1=${android_id_sha1}" +
            "&imei=${imei}&imei_md5=${imei_md5}" + "&imei_sha1=${imei_sha1}&deviceID=${deviceID}" +
            "&mac=${mac}&mac_md5=${mac_md5}&mac_sha1=${mac_sha1}" + "&remote_addr=${remote_addr}" +
            "&cur_adv=${cur_adv}&cur_adx=${cur_adx}&adver_id=${adver_id}&campaign_id=${campaign_id}" +
            "&resptimestamp=${resptimestamp}" + "&height=${height}&width=${width}&make=${make}&model=${model}" +
            "&bundle=${bundle}&ip=${ip}&app_name=${app_name}&material_type=${material_type}";

    public MexTrackingUrl(JsonObject cnf) {

        if (cnf.containsKey("track-base-url")) {
            IMP_TRACK_URL = cnf.getString("track-base-url") + "/adImp?";
            CLICK_TRACK_URL = cnf.getString("track-base-url") + "/adClick?";
//            THIRD_IMP_TRACK_URL = cnf.getString("track-base-url")+ "/timp?url=";
        } else {
            throw new RuntimeException("main config is null");
        }
        if (cnf.containsKey("winnotice-url")) {
            WINNOTICE_URL = cnf.getString("winnotice-url") + "/winnotice?";
        } else {
            throw new RuntimeException("");
        }
    }


    protected final String buildQueryString(BidRequest request, BidResponse response) {

        // TODO 整理返回的json中的key和uri中的key是否重复
        OpenRtb.BidRequest mexBidRequest = request.openRtb();
        Banner banner = response.getAdAndPricePair().getAd();
        OpenRtb.BidRequest.Device device = mexBidRequest.getDevice();

        String commonUrl;

        commonUrl = QUERY_STRING.replace("${requestid}", encodeUri(request.openRtb().getId()))
                .replace("${adgroupid}", encodeUri(banner.getAdGroupId() + ""))
                .replace("${netid}", encodeUri(banner.getAdxId()))
                .replace("${netname}", encodeUri(banner.getAdxName()))
                .replace("${devicetype}", encodeUri(device.hasDevicetype() ? device.getDevicetype().name() : "NA"))
                .replace("${os}", encodeUri(device.hasOs() ? device.getOs() : "NA"))
                .replace("${connectiontype}", encodeUri(device.hasConnectiontype() ? device.getConnectiontype().name() : "NA"))
                .replace("${material_id}", encodeUri(banner.getCreativeId() + ""))
                //iOS idfa
                .replace("${idfa}", encodeUri(device.getExtension(MexOpenRtbExt.idfa)))
                .replace("${android_id}", encodeUri(device.getExtension(MexOpenRtbExt.androidId)))
                .replace("${android_id_md5}", encodeUri(device.getDpidmd5()))
                .replace("${android_id_sha1}", encodeUri(device.getDpidsha1()))
                // imei
                .replace("${imei}", encodeUri(device.getExtension(MexOpenRtbExt.imei)))
                .replace("${imei_md5}", encodeUri(device.getDidmd5()))
                .replace("${imei_sha1}", encodeUri(device.getDidsha1()))
                // mac
                .replace("${mac}", encodeUri(device.getExtension(MexOpenRtbExt.mac)))
                .replace("${mac_md5}", encodeUri(device.getMacmd5()))
                .replace("${mac_sha1}", encodeUri(device.getMacsha1()))
                .replace("${deviceID}", encodeUri(RtbHelper.getDeviceId(request)))

                .replace("${remote_addr}", encodeUri(device.getIp())) // ipv4
                .replace("${cur_adv}", encodeUri(MexCurrency.lookup(banner.getAdverCurrencyType()).toString()))
                .replace("${cur_adx}", encodeUri(MexCurrency.lookup(banner.getAdxCurrencyType()).toString()))
                .replace("${adver_id}", encodeUri(String.valueOf(banner.getAdverId())))
                .replace("${campaign_id}", encodeUri(String.valueOf(banner.getAdCampaignId())))
                .replace("${resptimestamp}", encodeUri(DateTime.now().toString("yyyyMMddHHmmssSSS")))
                .replace("${height}", encodeUri(banner.getHeight() + ""))
                .replace("${width}", encodeUri(banner.getWidth() + ""))
                .replace("${make}", encodeUri(mexBidRequest.getDevice().hasMake() ? mexBidRequest.getDevice().getMake() : "NA"))
                .replace("${model}", encodeUri(mexBidRequest.getDevice().hasModel() ? mexBidRequest.getDevice().getModel() : "NA"))
                .replace("${bundle}", encodeUri(mexBidRequest.getApp().getBundle()))
                .replace("${ip}", encodeUri(device.hasIp() ? device.getIp() : "NA"))
                .replace("${material_type}", encodeUri(parseMaterialType(mexBidRequest, "banner")))
                .replace("${app_name}", encodeUri(mexBidRequest.getApp().hasName() ? mexBidRequest.getApp().getName() : "NA"));

        return commonUrl;
    }

    private String encodeUri(String val) {
        try {
            if (Strings.isNullOrEmpty(val)) {
                // 为空就encode “NA”
                val = "NA";
            }
            val = URLEncoder.encode(val, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("encodeVal=" + val, e);
        }
        return val;
    }

    private static String parseMaterialType(OpenRtb.BidRequest bidRequest, String def) {
        OpenRtb.BidRequest.Imp imp = bidRequest.getImp(0);
        if (0 != imp.getBanner().getW()) {
            return "banner";
        }
        if (0 != imp.getVideo().getW()) {
            return "video";
        }
        return def;
    }

    protected static List<String> buildTracking(String mexTracker, String type, Ad ad, BidRequest request, String queryString) {
        // 适配正则
        queryString = "\\?" + queryString;
        String trackingType = ad.getTrackingType();

        Map<String, List<String>> paramMap = HttpUtil.splitQuery(queryString);

        List<String> urlList = Lists.newArrayList();
        List<String> trackingUrls;
        if ("imp".equals(type)) {
            if (ad.getImpTrackingUrls().size() > 0) {
                trackingUrls = ad.getImpTrackingUrls();

                trackingUrls.forEach(tracking -> {
                    // 宏替换
                    tracking = HttpUtil.redirectUrl(tracking, paramMap);
                    logger.info("imp tranking mexTracker=" + tracking);
                    urlList.add(tracking);
                });
            } else {
                logger.info("imptrackingUrls can't be null, groupId=" + ad.getAdGroupId());
            }
        }


        // 下发多地址
        if ("clk".equals(type)) {
            /*if (ad.getClickTrackingUrls().size() > 0) {
                trackingUrls = ad.getClickTrackingUrls();

                trackingUrls.forEach(tracking -> {
                    tracking = HttpUtil.redirectUrl(tracking, paramMap, type);
                    urlList.add(tracking);
                });
            } else {
                logger.info("clktrackingUrls can't be null, groupId=" + ad.getAdGroupId());
            }*/
            if (Const.AD_TARGETING_TYPE_302.equals(trackingType)) {
                // 下发mex点击监测
                // do nothing

            } else if (Const.AD_TARGETING_TYPE_C2S.equals(trackingType)) {
                if (ad.getClickTrackingUrls().size() > 0) {
                    trackingUrls = ad.getClickTrackingUrls();

                    trackingUrls.forEach(tracking -> {
                        tracking = HttpUtil.redirectUrl(tracking, paramMap);
                        urlList.add(tracking);
                    });
                } else {
                    logger.info("clktrackingUrls can't be null, groupId=" + ad.getAdGroupId());
                }
            }
        }

        // 将mexTracker加入list
        urlList.add(mexTracker);
        return urlList;
    }

    protected Map<String, Object> buildImpClkLpUrl(BidRequest request, BidResponse response, boolean secure, String channelMacroPrice) {
        AdAndPricePair adAndPricePair = response.getAdAndPricePair();
        if (adAndPricePair == AdAndPricePair.EMPTY) {
            return null;
        }
        Banner banner = adAndPricePair.getAd();

        String materialUrl;
        String win;
        String mexImpTracker;
        String mexClkTracker;
        String queryString = buildQueryString(request, response);

        if (secure) {
            materialUrl = Constants.HTTPS + banner.getMaterialUrl();
            win = Constants.HTTPS + WINNOTICE_URL + queryString;

            mexImpTracker = Constants.HTTPS + IMP_TRACK_URL + queryString
                    + channelMacroPrice;

            mexClkTracker = Constants.HTTPS + CLICK_TRACK_URL + queryString;
        } else {
            materialUrl = Constants.HTTP + banner.getMaterialUrl();
            win = Constants.HTTP + WINNOTICE_URL + queryString;

            mexImpTracker = Constants.HTTP + IMP_TRACK_URL + queryString
                    + channelMacroPrice;

            mexClkTracker = Constants.HTTP + CLICK_TRACK_URL + queryString;
        }

        //buildTracking(mexImpTracker, "imp", ad, request, queryString);
        List<String> impList = buildTracking(mexImpTracker, "imp", adAndPricePair.getAd(), request, queryString);
        List<String> clkList = buildTracking(mexClkTracker, "clk", adAndPricePair.getAd(), request, queryString);

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        builder.put(Constants.MAP_CLK, clkList);
        builder.put(Constants.MAP_IMP, impList);
        builder.put(Constants.MAP_WIN, win);
        builder.put(Constants.MAP_MAT, materialUrl);

        if (Const.AD_TARGETING_TYPE_302.equals(banner.getTrackingType())) {
            builder.put(Constants.MAP_LP, banner.getClickTrackingUrls().get(0));
        } else if (Const.AD_TARGETING_TYPE_C2S.equals(banner.getTrackingType())) {
            builder.put(Constants.MAP_LP, banner.getLandingPage());
        }
        logger.info("groupid="+banner.getAdGroupId()+", trackingType="+banner.getTrackingType());
        return builder.build();
    }

}
