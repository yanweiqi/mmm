import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.adx.zplay.ZplayModule;
import com.mex.bidder.adx.zplay.ZplayRequestReceiver;
import com.mex.bidder.api.bidding.BidController;
import com.mex.bidder.api.bidding.BidInterceptor;
import com.mex.bidder.api.http.ExchangeRoute;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public class ZplayRequestReceiverTest {
    @Inject
    ZplayRequestReceiver zplayRequestReceiver;

    @Before
    public void setUp() throws Exception {

        Injector injector = Guice.createInjector(new ZplayModule(), new AbstractModule() {
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
    public void receive() throws Exception {
        System.out.println(zplayRequestReceiver);
    }

}