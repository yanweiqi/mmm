package com.mex.bidder.adx.adview;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.builder.response.MexResponseBuilder;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.macro.MacroProcessor;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * User: donghai
 * Date: 2016/11/20
 */
@Singleton
public class AdviewMacroProcessor extends MexResponseBuilder implements MacroProcessor {


    @Inject
    public AdviewMacroProcessor(@SysConf JsonObject cnf) {
        super(cnf);
    }

    @Override
    protected boolean needSecure(BidRequest request, BidResponse response) {
        return request.openRtb().getImp(0).getSecure();
    }

    @Override
    protected String getPriceMacroTag() {
        return Constants.MACRO_PRICE_ADVIEW;
    }


    public void process(BidRequest request, BidResponse response) {
        buildMexRtbResponse(request, response);
    }


}
