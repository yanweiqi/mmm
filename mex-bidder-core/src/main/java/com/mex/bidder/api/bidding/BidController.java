package com.mex.bidder.api.bidding;

import com.codahale.metrics.MetricRegistry;
import com.mex.bidder.api.interceptor.StandardInterceptorController;

import java.util.List;

/**
 * Controller for bidding.
 */
public class BidController
        extends StandardInterceptorController<BidRequest, BidResponse> {

    public BidController(
            List<? extends BidInterceptor> interceptors, MetricRegistry metricRegistry) {
        super(interceptors, metricRegistry);
    }
}
