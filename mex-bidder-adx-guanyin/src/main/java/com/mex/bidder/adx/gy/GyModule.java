package com.mex.bidder.adx.gy;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.api.http.ExchangeRoute;

import javax.inject.Inject;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public class GyModule extends AbstractModule {

    public GyModule() {

    }


    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), ExchangeRoute.class).addBinding().toProvider(GyRouteProvider.class);
    }

    public static class GyRouteProvider implements Provider<ExchangeRoute> {

        GyRequestReceiver receiver;

        @Inject
        GyRouteProvider(GyRequestReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public ExchangeRoute get() {
            return new ExchangeRoute(GyExchange.INSTANCE, receiver);
        }
    }
}
