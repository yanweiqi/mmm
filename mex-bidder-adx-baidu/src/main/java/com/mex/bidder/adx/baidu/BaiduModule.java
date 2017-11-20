package com.mex.bidder.adx.baidu;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.api.http.ExchangeRoute;

import javax.inject.Inject;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public class BaiduModule extends AbstractModule {

    public BaiduModule() {

    }


    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), ExchangeRoute.class).addBinding().toProvider(BaiduRouteProvider.class);
    }

    public static class BaiduRouteProvider implements Provider<ExchangeRoute> {

        BaiduRequestReceiver receiver;

        @Inject
        BaiduRouteProvider(BaiduRequestReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public ExchangeRoute get() {
            return new ExchangeRoute(BaiduExchange.INSTANCE, receiver);
        }
    }
}
