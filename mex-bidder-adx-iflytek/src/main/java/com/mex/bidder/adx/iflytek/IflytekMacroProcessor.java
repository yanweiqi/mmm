package com.mex.bidder.adx.iflytek;

import com.google.inject.Inject;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.builder.response.MexResponseBuilder;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.macro.MacroProcessor;
import io.vertx.core.json.JsonObject;

import javax.inject.Singleton;

/**
 * User: donghai
 * Date: 2016/11/20
 */
@Singleton
public class IflytekMacroProcessor extends MexResponseBuilder implements MacroProcessor {

    @Inject
    public IflytekMacroProcessor(@SysConf JsonObject cnf) {
        super(cnf);
    }

    @Override
    protected boolean needSecure(BidRequest request, BidResponse response) {
        boolean secure = request.openRtb().getImp(0).getSecure();
        return secure;
    }

    @Override
    protected String getPriceMacroTag() {
        return Constants.MACRO_PRICE_IFLYTEK;
    }

    @Override
    public void process(BidRequest request, BidResponse response) {
        buildMexRtbResponse(request, response);
    }

}
