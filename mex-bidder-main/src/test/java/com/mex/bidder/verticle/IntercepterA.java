package com.mex.bidder.verticle;

import com.mex.bidder.api.bidding.BidInterceptor;
import com.mex.bidder.api.interceptor.InterceptorChain;

import javax.inject.Inject;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public class IntercepterA implements BidInterceptor {

    @Inject
    FilterService filterService;

    @Override
    public void execute(InterceptorChain chain) {
        filterService.filter();
    }
}
