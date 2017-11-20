package com.mex.bidder.adx.sohu;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.api.http.ExchangeRoute;

import javax.inject.Inject;

/**
 * xuchuahao
 * on 2017/3/20.
 */
public class SohuModule extends AbstractModule {

    public SohuModule() {

    }

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), ExchangeRoute.class).addBinding().toProvider(SohuRouterProvider.class);
    }

    public static class SohuRouterProvider implements Provider<ExchangeRoute> {

        SohuRequesReceiver receiver;

        @Inject
        SohuRouterProvider(SohuRequesReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public ExchangeRoute get() {
            return new ExchangeRoute(SohuExchange.INSTANCE, receiver);
        }
    }
}
