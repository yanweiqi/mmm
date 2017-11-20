package com.mex.bidder.adx.zplay;

import com.google.inject.Inject;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.builder.response.MexResponseBuilder;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.macro.MacroProcessor;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.protocol.Ad;
import io.vertx.core.json.JsonObject;
import org.slf4j.LoggerFactory;

;


/**
 * User: donghai
 * Date: 2016/11/20
 */
public class ZplayMacroProcessor extends MexResponseBuilder implements MacroProcessor {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ZplayMacroProcessor.class);

    @Inject
    public ZplayMacroProcessor(@SysConf JsonObject cnf) {
        super(cnf);
    }

    @Override
    protected boolean needSecure(BidRequest request, BidResponse response) {
        return buildFlag(request, response.getAdAndPricePair());
    }

    @Override
    protected String getPriceMacroTag() {
        return Constants.MACRO_PRICE_ZPLAY;
    }


    public void process(BidRequest request, BidResponse response) {
        buildMexRtbResponse(request, response);
    }


    private boolean buildFlag(BidRequest request, AdAndPricePair adAndPricePair) {
        Boolean needHttps = request.openRtb().getExtension(MexOpenRtbExt.needHttps);
        Ad baseAd = adAndPricePair.getAd();
        boolean isHttps = baseAd.getIsHttps();// 落地页是否是否https
        String os = request.openRtb().getDevice().getOs();
        if ("ios".equals(os.toLowerCase())) {
            if (isHttps) {
                return true;
            } else {
                return false;
            }
        } else {
            // android 同为https
            if (needHttps && isHttps) {
                return true;
            } else if (!needHttps && !isHttps) {
                // Android 同为 http
                return false;
            } else {
                throw new RuntimeException("groupid=" + baseAd.getAdGroupId() + ", reqid=" + request.openRtb().getId());
            }
        }
    }

}
