package com.mex.bidder.adx.gy;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.mex.bidder.api.bidding.BidController;
import com.mex.bidder.api.bidding.BidInterceptor;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public class GyRequestReceiverTest {
    @Inject
     GyRequestReceiver gyRequestReceiver;

    @Before
    public void setUp() throws Exception {

        Injector injector = Guice.createInjector(new GyModule(), new AbstractModule() {
            @Override
            protected void configure() {
                MetricRegistry metricRegistry = new MetricRegistry();
                bind(MetricRegistry.class).toInstance(metricRegistry);
            }

            @Provides
            @Singleton
            public BidController provideBidController(
                    MetricRegistry metricRegistry) {
                List<? extends BidInterceptor> interceptors = new ArrayList<>();
                return new BidController(interceptors, metricRegistry);
            }
        });
        injector.injectMembers(this);

    }

    @Test
    public void test1(){
        System.out.print("sd");
    }

    @Test
    public void receive() throws Exception {
        System.out.println(gyRequestReceiver);
    }

}