package com.mex.bidder.adx.meitu;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.api.http.ExchangeRoute;

import javax.inject.Inject;

/**
 * xuchuahao
 * on 2017/6/12.
 */
public class MeituModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), ExchangeRoute.class).addBinding().toProvider(MeituRouteProvider.class);
    }

    public static class MeituRouteProvider implements Provider<ExchangeRoute> {

        MeituBidRequestReceiver receiver;

        @Inject
        MeituRouteProvider(MeituBidRequestReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public ExchangeRoute get() {
            return new ExchangeRoute(MeituExchange.INSTANCE, receiver);
        }
    }
}
