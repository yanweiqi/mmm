package com.mex.bidder.adx.gy;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.builder.response.MexResponseBuilder;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.macro.MacroProcessor;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.engine.util.RtbHelper;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.Banner;
import io.vertx.core.json.JsonObject;
import org.slf4j.LoggerFactory;

/**
 * User: donghai
 * Date: 2016/11/20
 */
public class GyMacroProcessor extends MexResponseBuilder implements MacroProcessor {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GyMacroProcessor.class);

    @Inject
    public GyMacroProcessor(@SysConf JsonObject cnf) {
        super(cnf);
    }

    @Override
    protected boolean needSecure(BidRequest request, BidResponse response) {
        AdAndPricePair adAndPricePair = response.getAdAndPricePair();
        return needHttps(adAndPricePair);
    }

    @Override
    protected String getPriceMacroTag() {
        return Constants.MACRO_PRICE_GY;
    }


    /**
     * 在302状态下，如果landingpage是https的其他下发的链接必须保持统一的https
     * 包括检测链接和物料地址
     *
     * @param adAndPricePair
     * @return
     */
    private boolean needHttps(AdAndPricePair adAndPricePair) {
        Ad ad = adAndPricePair.getAd();
        String landingPage = ad.getLandingPage();

        if (landingPage.contains("https")) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void process(BidRequest request, BidResponse response) {
        buildMexRtbResponse(request, response);
    }

    /**
     * 构建response
     *
     * @param adAndPricePair
     * @param request
     * @param response
     */
    private void buildOpenRtbResponse(AdAndPricePair adAndPricePair, String materialUrl, String nurl, String mexImpTracker,
                                      String mexClkTracker, BidRequest request, BidResponse response) {
        // 选中广告成功后，构建响应对象
        if (adAndPricePair != AdAndPricePair.EMPTY) {
            Banner banner = adAndPricePair.getAd();
//            response.setTargetAd(adAndPricePair.getAd());

            response.openRtb()
//                    .setExtension(MexOpenRtbExt.resAdType, MexOpenRtbExt.AdType.BANNER)
                    .setBidid(MexUtil.uuid())
                    .addSeatbid(
                            OpenRtb.BidResponse.SeatBid.newBuilder().addBid(
                                    OpenRtb.BidResponse.SeatBid.Bid.newBuilder()
                                            .setId(MexUtil.uuid())
                                            .setImpid(request.openRtb().getImp(0).getId())
                                            .setPrice(adAndPricePair.getPrice().doubleValue())
                                            .setAdid(String.valueOf(banner.getCreativeId()))
                                            .setAdm(materialUrl)
                                            .addAdomain(com.google.common.base.Strings.nullToEmpty(banner.getAdverWebsite()))
                                            .setBundle(com.google.common.base.Strings.nullToEmpty(banner.getBundle()))
                                            .setIurl(com.google.common.base.Strings.nullToEmpty(mexImpTracker))
                                            .setCrid(String.valueOf(banner.getCreativeId()))
                                            .setCid(String.valueOf(banner.getAdCampaignId()))
                                            .setW(banner.getWidth())
                                            .setH(banner.getHeight())
                                            .setNurl(nurl)
                                            .setExtension(MexOpenRtbExt.imptrackers, ImmutableList.of(mexImpTracker))
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
