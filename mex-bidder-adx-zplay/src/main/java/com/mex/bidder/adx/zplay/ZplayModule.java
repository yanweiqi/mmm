package com.mex.bidder.adx.zplay;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.api.http.ExchangeRoute;

import javax.inject.Inject;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public class ZplayModule extends AbstractModule {

    public ZplayModule() {

    }


    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), ExchangeRoute.class).addBinding().toProvider(ZplayRouteProvider.class);
    }

    public static class ZplayRouteProvider implements Provider<ExchangeRoute> {

        ZplayRequestReceiver receiver;

        @Inject
        ZplayRouteProvider(ZplayRequestReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public ExchangeRoute get() {
            return new ExchangeRoute(ZplayExchange.INSTANCE, receiver);
        }
    }
}
