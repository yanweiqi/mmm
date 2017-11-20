package com.mex.bidder.adx.sohu;

import com.google.inject.Inject;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.builder.response.MexResponseBuilder;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.macro.MacroProcessor;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.protocol.Ad;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xuchuahao
 * on 2017/3/27.
 */
public class SohuMacroProcessor extends MexResponseBuilder implements MacroProcessor {


    private static final Logger logger = LoggerFactory.getLogger(SohuMacroProcessor.class);

    @Inject
    public SohuMacroProcessor(@SysConf JsonObject cnf) {
        super(cnf);
    }

    @Override
    protected boolean needSecure(BidRequest request, BidResponse response) {
        AdAndPricePair adAndPricePair = response.getAdAndPricePair();
        return needHttps(adAndPricePair);
    }

    @Override
    protected String getPriceMacroTag() {
        return Constants.MACRO_PRICE_SOHU;
    }

    @Override
    public void process(BidRequest request, BidResponse response) {
        buildMexRtbResponse(request, response);
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


}
