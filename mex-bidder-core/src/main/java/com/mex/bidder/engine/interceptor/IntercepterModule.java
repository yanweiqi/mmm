package com.mex.bidder.engine.interceptor;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.api.bidding.BidController;
import com.mex.bidder.api.bidding.BidInterceptor;

import javax.inject.Singleton;
import java.util.Set;

/**
 * User: donghai
 * Date: 2016/11/20
 */
public class IntercepterModule extends AbstractModule {
    @Override
    protected void configure() {
        // 多个拦截器注入
        Multibinder<BidInterceptor> interceptorMultibinder = Multibinder.newSetBinder(binder(), BidInterceptor.class);
        interceptorMultibinder.addBinding().to(MexBidIntercepter.class);
    }

    @Provides
    @Singleton
    public BidController provideBidController(
            MetricRegistry metricRegistry,
            Set<BidInterceptor> interceptors) {

        return new BidController(ImmutableList.copyOf(interceptors), metricRegistry);
    }
}
