package com.mex.bidder.engine.builder.response;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.engine.util.HttpUtil;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.Banner;
import com.mex.bidder.protocol.Const;
import com.mex.bidder.protocol.nativead.Native;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MEX 内部RTB响应对象模块类
 * <p>
 * user: donghai
 * date: 2017/4/21
 */
public abstract class MexResponseBuilder {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String HTTPS = NativeHelper.HTTPS;
    public static final String HTTP = NativeHelper.HTTP;

    //mex曝光监测地址
    private final String IMP_TRACK_URL_BASE;
    //mex点击监测
    private final String CLICK_TRACK_URL_BASE;
    //mex赢价
    private final String WINNOTICE_URL_BASE;

    public MexResponseBuilder(JsonObject cnf) {
        if (cnf.containsKey("track-base-url")) {
            IMP_TRACK_URL_BASE = cnf.getString("track-base-url") + "/adImp?";
            CLICK_TRACK_URL_BASE = cnf.getString("track-base-url") + "/adClick?";
        } else {
            throw new RuntimeException("main config is null");
        }
        if (cnf.containsKey("winnotice-url")) {
            WINNOTICE_URL_BASE = cnf.getString("winnotice-url") + "/winnotice?";
        } else {
            throw new RuntimeException("");
        }
    }

    protected abstract boolean needSecure(BidRequest request, BidResponse response);

    protected abstract String getPriceMacroTag();

    /**
     * 构建MEX 内部RTB响应对象
     *
     * @param request
     * @param response
     */
    public void buildMexRtbResponse(BidRequest request, BidResponse response) {
        BidResponse.ResponseMode responseMode = response.getResponseMode();

        if (responseMode == BidResponse.ResponseMode.NONE) {
            // 无下发，直接返回
            buildNbrInfo(request, response);
        } else {

            // 根据广告类型下发构建相应广告
            if (responseMode == BidResponse.ResponseMode.BANNER) {
                buildBanner(request, response);
            } else if (responseMode == BidResponse.ResponseMode.NATIVE) {
                buildNative(request, response);
            } else if (responseMode == BidResponse.ResponseMode.VIDEO) {
                // TODO
            }
        }
    }

    protected void buildNbrInfo(BidRequest request, BidResponse response) {
        Map<String, String> filterErrorMap = response.getFilterErrorMap();

        List<MexOpenRtbExt.MexNbrField> nbrFieldList = Lists.newArrayList();
        filterErrorMap.forEach((k, v) -> {
            nbrFieldList.add(MexOpenRtbExt.MexNbrField.newBuilder().setCode(k).setNbr(v).build());
        });

        response.openRtb()
                .setId(request.openRtb().getId())
                .setExtension(MexOpenRtbExt.resNetname, request.openRtb().getExtension(MexOpenRtbExt.reqNetname))
                .setBidid(MexUtil.uuid())
                .setExtension(MexOpenRtbExt.debugInfo, nbrFieldList);
    }


    /**
     * 构建原生广告
     *
     * @param request
     * @param response
     */
    private void buildNative(BidRequest request, BidResponse response) {
        AdAndPricePair adAndPricePair = response.getAdAndPricePair();
        Native nativeAd = adAndPricePair.getAd();
        String queryString = MexTrackerHelper.buildQueryString(request, response);
        boolean needSecure = needSecure(request, response);
        String priceMacroTag = getPriceMacroTag();
        MexUrl mexUrl = buildMexUrl(request, priceMacroTag, needSecure, queryString);
        ThirdPartyTrackerUrl thirdPartyTrackerUrl = buildThirdPartyTrackerUrl(
                queryString, nativeAd.getImpTrackingUrls(), nativeAd.getClickTrackingUrls(), request);
        TrackingUrlAndLandingPageUrl trackingUrlAndLandingPageUrl = normalizeUrlByLpType(nativeAd, mexUrl, thirdPartyTrackerUrl);

        // 构建原生asset元素
        List<OpenRtb.NativeResponse.Asset> pbAssets = NativeHelper.buildNativePbAssets(request, nativeAd, needSecure);

        // 构建原生响应
        SeatBid.Builder bid = SeatBid.newBuilder().addBid(
                Bid.newBuilder()
                        .setAdmNative(
                                OpenRtb.NativeResponse.newBuilder()
                                        .setLink(OpenRtb.NativeResponse.Link.newBuilder())
                                        .addAllAssets(pbAssets)
                        )
                        .setId(MexUtil.uuid())
                        .setImpid(request.openRtb().getImp(0).getId())
                        .setPrice(adAndPricePair.getPrice().doubleValue())
                        .setAdid(String.valueOf(nativeAd.getCreativeId()))
                        .addAdomain(Strings.nullToEmpty(nativeAd.getAdverWebsite()))
                        .setBundle(Strings.nullToEmpty(nativeAd.getBundle()))
                        .setCrid(String.valueOf(nativeAd.getCreativeId()))
                        .setCid(String.valueOf(nativeAd.getAdCampaignId()))
                        .setNurl(mexUrl.winUrl)
                        .setExtension(MexOpenRtbExt.landingpage, trackingUrlAndLandingPageUrl.landingPage)
                        .setExtension(MexOpenRtbExt.imptrackers, trackingUrlAndLandingPageUrl.impUrls)
                        .setExtension(MexOpenRtbExt.clktrackers, trackingUrlAndLandingPageUrl.clickUrls)
                        .setExtension(MexOpenRtbExt.adType, MexOpenRtbExt.AdType.NATIVE_AD)
                        .setExtension(MexOpenRtbExt.adgroupid, nativeAd.getAdGroupId())
                        .setExtension(MexOpenRtbExt.adverid, nativeAd.getAdverId())
                        .setExtension(MexOpenRtbExt.ip, request.openRtb().getDevice().getIp())
                        .setExtension(MexOpenRtbExt.productid,nativeAd.getProductId())
                        .setExtension(MexOpenRtbExt.deeplink,nativeAd.getDeeplink())
        );


        response.openRtb()
                .setId(request.openRtb().getId())
                .setExtension(MexOpenRtbExt.resNetname, request.openRtb().getExtension(MexOpenRtbExt.reqNetname))
                .setBidid(MexUtil.uuid()).addSeatbid(bid);
    }


    private void buildBanner(BidRequest request, BidResponse response) {
        AdAndPricePair adAndPricePair = response.getAdAndPricePair();
        Banner banner = adAndPricePair.getAd();
        String queryString = MexTrackerHelper.buildQueryString(request, response);
        boolean needSecure = needSecure(request, response);
        String priceMacroTag = getPriceMacroTag();
        String creativeUrl = (needSecure ? HTTPS : HTTP) + NativeHelper.formatUrl(banner.getMaterialUrl());
        MexUrl mexUrl = buildMexUrl(request, priceMacroTag, needSecure, queryString);
        ThirdPartyTrackerUrl thirdPartyTrackerUrl = buildThirdPartyTrackerUrl(queryString,
                banner.getImpTrackingUrls(), banner.getClickTrackingUrls(), request);

        TrackingUrlAndLandingPageUrl trackingUrlAndLandingPageUrl = normalizeUrlByLpType(banner, mexUrl, thirdPartyTrackerUrl);

        // 构建Banner
        response.openRtb()
                .setId(request.openRtb().getId())
                .setExtension(MexOpenRtbExt.resNetname, request.openRtb().getExtension(MexOpenRtbExt.reqNetname))
                .setBidid(MexUtil.uuid())
                .addSeatbid(SeatBid.newBuilder().addBid(
                        Bid.newBuilder()
                                .setId(MexUtil.uuid())
                                .setImpid(request.openRtb().getImp(0).getId())
                                .setPrice(adAndPricePair.getPrice().doubleValue())
                                .setAdid(String.valueOf(banner.getCreativeId()))
                                .setAdm(creativeUrl)
                                .setIurl(creativeUrl)
                                .addAdomain(Strings.nullToEmpty(banner.getAdverWebsite()))
                                .setBundle(Strings.nullToEmpty(banner.getBundle()))
                                .setCrid(String.valueOf(banner.getCreativeId()))
                                .setCid(String.valueOf(banner.getAdCampaignId()))
                                .setW(banner.getWidth())
                                .setH(banner.getHeight())
                                .setNurl(mexUrl.winUrl)
                                .setExtension(MexOpenRtbExt.imptrackers, trackingUrlAndLandingPageUrl.impUrls)
                                .setExtension(MexOpenRtbExt.clktrackers, trackingUrlAndLandingPageUrl.clickUrls)
                                .setExtension(MexOpenRtbExt.landingpage, trackingUrlAndLandingPageUrl.landingPage)
                                .setExtension(MexOpenRtbExt.adType, MexOpenRtbExt.AdType.BANNER_AD)
                                .setExtension(MexOpenRtbExt.adgroupid, banner.getAdGroupId())
                                .setExtension(MexOpenRtbExt.adverid, banner.getAdverId())
                                .setExtension(MexOpenRtbExt.ip, request.openRtb().getDevice().getIp())
                                .setExtension(MexOpenRtbExt.productid,banner.getProductId())
                                .setExtension(MexOpenRtbExt.deeplink,banner.getDeeplink())
                        )
                );
    }


    /**
     * 根据广告落地页跳转类型，判断相应的监测链接及LP页面：
     * 1.监测302跳转LP，投放点击监测链接
     * 2.直投c2s, 直接投放LP,异步上报Mex及第三方点击
     *
     * @param ad
     * @param mexUrl
     * @param thirdPartyTrackerUrl
     * @return
     */
    protected TrackingUrlAndLandingPageUrl normalizeUrlByLpType(Ad ad, MexUrl mexUrl,
                                                                ThirdPartyTrackerUrl thirdPartyTrackerUrl) {
        TrackingUrlAndLandingPageUrl trackingUrlAndLandingPageUrl = new TrackingUrlAndLandingPageUrl();
        if (Const.AD_TARGETING_TYPE_302.equals(ad.getTrackingType())) {
            // 302点击监测只下发mex url，落地页投放第三方监测的点击地址，第三方监测的点击地址跳转到落地页
            trackingUrlAndLandingPageUrl.landingPage = thirdPartyTrackerUrl.clickUrls.get(0);
            trackingUrlAndLandingPageUrl.clickUrls.add(mexUrl.clickUrl);

        } else if (Const.AD_TARGETING_TYPE_C2S.equals(ad.getTrackingType())) {
            // C2S直投落地页，异步上报Mex,第三方检测数据，所以下发两个链接
            trackingUrlAndLandingPageUrl.landingPage = ad.getLandingPage();
            trackingUrlAndLandingPageUrl.clickUrls.add(mexUrl.clickUrl);
            trackingUrlAndLandingPageUrl.clickUrls.addAll(thirdPartyTrackerUrl.clickUrls);
        }

        //展示检测都是异步发送，所以下发两个链接
        trackingUrlAndLandingPageUrl.impUrls.add(mexUrl.impUrl);
        trackingUrlAndLandingPageUrl.impUrls.addAll(thirdPartyTrackerUrl.impUrls);


        return trackingUrlAndLandingPageUrl;

    }

    /**
     * 第三监测宏替换
     *
     * @param impTrackingUrls
     * @param clickTrackingUrls
     * @param request
     * @return
     */
    protected ThirdPartyTrackerUrl buildThirdPartyTrackerUrl(String queryString,
                                                             List<String> impTrackingUrls,
                                                             List<String> clickTrackingUrls,
                                                             BidRequest request) {
        Map<String, List<String>> paramMap = HttpUtil.splitQuery("\\?" + queryString);
        return ThirdPartyTrackingMacroHelper.replaceMacro(impTrackingUrls, clickTrackingUrls, paramMap, request);
    }


    protected MexUrl buildMexUrl(BidRequest request, String priceMacroTag, boolean needSecure, String queryString) {
        String protocol;
        if (needSecure) {
            protocol = HTTPS;
        } else {
            protocol = HTTP;
        }
        //展示链接多加一个价格宏
        String winUrl;
        if (Constants.IFLYTEK_ID.equals(request.getExchange().getId())) {
            // 科大的需要在winurl后面添加价格宏
            winUrl = protocol + WINNOTICE_URL_BASE + queryString + priceMacroTag;
        } else {
            winUrl = protocol + WINNOTICE_URL_BASE + queryString;

        }
        String impUrl = protocol + IMP_TRACK_URL_BASE + queryString + priceMacroTag;
        String clickUrl = protocol + CLICK_TRACK_URL_BASE + queryString;

        return MexUrl.build(impUrl, clickUrl, winUrl);

    }

    //----------------------------------------------------------
    //                        工具类
    //----------------------------------------------------------

    static class TrackingUrlAndLandingPageUrl {
        List<String> impUrls = Lists.newArrayList();       // 下发的展示URL,包括mex,第三方链接
        List<String> clickUrls = Lists.newArrayList();     // 下发的点击URL，包括mex,（第三方链接—非302时)
        String landingPage;                                // 根据跳转类型计算出来
    }

    protected static class ThirdPartyTrackerUrl {
        List<String> impUrls;    //第三方展示
        List<String> clickUrls;  // 第三方点击

        static ThirdPartyTrackerUrl build(List<String> impUrls, List<String> clickUrls) {
            return new ThirdPartyTrackerUrl(impUrls, clickUrls);
        }

        ThirdPartyTrackerUrl(List<String> impUrls, List<String> clickUrls) {
            this.impUrls = (impUrls == null) ? Collections.emptyList() : impUrls;
            this.clickUrls = (clickUrls == null) ? Collections.emptyList() : clickUrls;
        }
    }

    /**
     * Mex 监测地址
     */
    protected static class MexUrl {
        String impUrl;     //展示
        String clickUrl;   //点击
        String winUrl;     //win notice

        static MexUrl build(String impUrl, String clickUrl, String winUrl) {
            return new MexUrl(impUrl, clickUrl, winUrl);
        }

        MexUrl(String impUrl, String clickUrl, String winUrl) {
            this.impUrl = impUrl;
            this.clickUrl = clickUrl;
            this.winUrl = winUrl;
        }
    }


}
