package com.mex.bidder.adx.baidu;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.macro.MacroProcessor;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.engine.util.MexTrackingUrl;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.engine.util.RtbHelper;
import com.mex.bidder.protocol.Banner;
import io.vertx.core.json.JsonObject;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * User: donghai
 * Date: 2016/11/20
 */
@Singleton
public class BaiduMacroProcessor extends MexTrackingUrl implements MacroProcessor {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BaiduMacroProcessor.class);

    @Inject
    public BaiduMacroProcessor(@SysConf JsonObject cnf) {
        super(cnf);
    }

    public void process(BidRequest request, BidResponse response) {
        AdAndPricePair adAndPricePair = response.getAdAndPricePair();

        // bidder无下发广告，直接返回
        if (adAndPricePair == AdAndPricePair.EMPTY) {
            return;
        }

        String queryString = buildQueryString(request, response);

        String nurl = Constants.HTTP + WINNOTICE_URL + queryString;

        String mexImpTracker = Constants.HTTP + IMP_TRACK_URL + queryString
                + "&price=%%PRICE%%";

        String mexClkTracker = Constants.HTTP + CLICK_TRACK_URL + queryString;

        buildOpenRtbResponse(adAndPricePair, nurl, mexImpTracker, mexClkTracker, request, response);
    }

    /**
     * 构建response
     *
     * @param adAndPricePair
     * @param request
     * @param response
     */
    private void buildOpenRtbResponse(AdAndPricePair adAndPricePair, String nurl, String iurl, String mexClkTracker, BidRequest request, BidResponse response) {
        // 选中广告成功后，构建响应对象
        if (adAndPricePair != AdAndPricePair.EMPTY) {
            Banner banner = adAndPricePair.getAd();
//            response.setTargetAd(adAndPricePair.getAd());

            response.openRtb()
//                    .setExtension(MexOpenRtbExt.resAdType, MexOpenRtbExt.AdType.BANNER)
                    .addSeatbid(
                            OpenRtb.BidResponse.SeatBid.newBuilder().addBid(
                                    OpenRtb.BidResponse.SeatBid.Bid.newBuilder()
                                            .setId(MexUtil.uuid())
                                            .setImpid(request.openRtb().getImp(0).getId())
                                            .setPrice(adAndPricePair.getPrice().doubleValue())
                                            .setAdid(String.valueOf(banner.getCreativeId()))
                                            .setAdm(Constants.HTTP+banner.getMaterialUrl())
                                            .addAdomain(com.google.common.base.Strings.nullToEmpty(banner.getAdverWebsite()))
                                            .setBundle(com.google.common.base.Strings.nullToEmpty(banner.getBundle()))
                                            .setIurl(com.google.common.base.Strings.nullToEmpty(iurl))
                                            .setCrid(String.valueOf(banner.getCreativeId()))
                                            .setCid(String.valueOf(banner.getAdCampaignId()))
                                            .setW(banner.getWidth())
                                            .setH(banner.getHeight())
                                            .setNurl(nurl)
                                            .setExtension(MexOpenRtbExt.imptrackers, ImmutableList.of(iurl))
                                            .setExtension(MexOpenRtbExt.clktrackers, ImmutableList.of(mexClkTracker))
                                            .setExtension(MexOpenRtbExt.landingpage, banner.getLandingPage())
                            )
                    );
        } else {
            logger.warn("no ad found for adx [{}] with impId={} and request object {}",
                    request.getExchange(), request.openRtb().getImp(0).getId(),
                    RtbHelper.openRtbBidRequestToJson(request.openRtb()));
        }
    }

}
