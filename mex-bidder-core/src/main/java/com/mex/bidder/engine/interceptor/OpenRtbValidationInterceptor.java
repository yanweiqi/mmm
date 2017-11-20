package com.mex.bidder.engine.interceptor;


import com.google.openrtb.util.OpenRtbValidator;
import com.mex.bidder.api.bidding.BidInterceptor;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.interceptor.InterceptorChain;

import javax.inject.Inject;


public class OpenRtbValidationInterceptor implements BidInterceptor {
    private final OpenRtbValidator validator;

    @Inject
    public OpenRtbValidationInterceptor(OpenRtbValidator validator) {
        this.validator = validator;
    }

    @Override
    public void execute(final InterceptorChain<BidRequest, BidResponse> chain) {
        chain.proceed();

        validator.validate(chain.request().openRtb(), chain.response().openRtb());
    }
}
