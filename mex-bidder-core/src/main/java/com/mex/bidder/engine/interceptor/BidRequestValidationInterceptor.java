

package com.mex.bidder.engine.interceptor;

import com.mex.bidder.api.bidding.BidInterceptor;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.interceptor.InterceptorChain;


public class BidRequestValidationInterceptor implements BidInterceptor {

    @Override
    public void execute(InterceptorChain<BidRequest, BidResponse> chain) {
        boolean hasImp = chain.request().openRtb().hasAllimps();
        if (!hasImp) {
            BidResponse response = chain.response();
            response.httpResponse().setStatusCode(400).end();
        }
        chain.proceed();

    }
}
