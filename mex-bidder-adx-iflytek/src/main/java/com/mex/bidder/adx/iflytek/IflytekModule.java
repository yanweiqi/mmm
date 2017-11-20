package com.mex.bidder.adx.iflytek;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.api.http.ExchangeRoute;

import javax.inject.Inject;

/**
 * adview 模块实现
 * User: donghai
 * Date: 2016/11/16
 */
public class IflytekModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), ExchangeRoute.class).addBinding().toProvider(AdviewRouteProvider.class);
    }

    public static class AdviewRouteProvider implements Provider<ExchangeRoute> {

        IflytekBidRequestReceiver receiver;

        @Inject
        AdviewRouteProvider(IflytekBidRequestReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public ExchangeRoute get() {
            return new ExchangeRoute(IflytekExchange.INSTANCE, receiver);
        }
    }
}
