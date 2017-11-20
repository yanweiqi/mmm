package com.mex.bidder.adx.meitu;

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
 * xuchuahao
 * on 2017/6/12.
 */
@Singleton
public class MeituMacroProcessor extends MexResponseBuilder implements MacroProcessor {

    @Inject
    public MeituMacroProcessor(@SysConf JsonObject cnf) {
        super(cnf);
    }

    @Override
    public void process(BidRequest request, BidResponse response) {
        buildMexRtbResponse(request, response);
    }

    @Override
    protected boolean needSecure(BidRequest request, BidResponse response) {
        return request.openRtb().getImp(0).getSecure();
    }

    @Override
    protected String getPriceMacroTag() {
        return Constants.MACRO_PRICE_MEITU;
    }
}
