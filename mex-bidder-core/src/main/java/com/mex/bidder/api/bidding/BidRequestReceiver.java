package com.mex.bidder.api.bidding;

import com.codahale.metrics.MetricRegistry;
import com.mex.bidder.api.bidding.BidController;
import com.mex.bidder.api.interceptor.RequestReceiver;
import com.mex.bidder.api.platform.Exchange;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public abstract class BidRequestReceiver<Req, Resp> extends RequestReceiver<BidController> {

    @Inject
    public BidRequestReceiver(
            Exchange exchange, MetricRegistry metricRegistry, BidController controller) {
        super(exchange, metricRegistry, controller);
    }
}
