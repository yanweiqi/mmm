package com.mex.bidder.verticle;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.Lists;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.adx.adview.AdviewModule;
import com.mex.bidder.adx.zplay.ZplayModule;
import com.mex.bidder.api.bidding.BidController;
import com.mex.bidder.api.bidding.BidInterceptor;
import com.mex.bidder.api.http.ExchangeRoute;
import com.mex.bidder.api.vertx.guice.VertxModule;
import com.mex.bidder.engine.bizdata.RedisMessageHandler;
import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import javax.inject.Singleton;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.Set;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: donghai
 * Date: 2016/11/16
 */
public class BidderApiTestModule extends VertxModule {
    private Vertx vertx;

    @Override
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void configure() {
        MetricRegistry metricRegistry = new MetricRegistry();

        binder().bind(MetricRegistry.class).toInstance(metricRegistry);

//        ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry).build();
//        reporter.start(1, TimeUnit.SECONDS);

        Timer timer = metricRegistry.timer(MetricRegistry.name(BidderApiTestModule.class, "get-latency"));
        Meter meterTps = metricRegistry.meter(MetricRegistry.name(BidderApiTestModule.class, "request", "tps"));

//        Set<ExchangeRoute> exchangeRoutes = new HashSet<>();
//        ExchangeRoute myAdx = new ExchangeRoute(NoExchange.INSTANCE, new HttpReceiver() {
//
//            @Override
//            public void receive(RoutingContext ctx) {
//                meterTps.mark();
//                ctx.response().setStatusCode(200);
//                ctx.response().end("hello world");
//            }
//        });
//        exchangeRoutes.add(myAdx);

//        Multibinder.newSetBinder(binder, HttpRoute.class).addBinding()
//                .toProvider(BidRouteProvider.class).in(Scopes.SINGLETON);
//        binder.bind(ExchangeRouter.class).toInstance(new ExchangeRouter(exchangeRoutes, metricRegistry));
        Multibinder<ExchangeRoute> routeMultibinder = Multibinder.newSetBinder(binder(), ExchangeRoute.class);
        routeMultibinder.addBinding().toProvider(ZplayModule.ZplayRouteProvider.class);
        routeMultibinder.addBinding().toProvider(AdviewModule.AdviewRouteProvider.class);

        Multibinder<BidInterceptor> interceptorMultibinder = Multibinder.newSetBinder(binder(),
                BidInterceptor.class, BidInterceptors.class);
        interceptorMultibinder.addBinding().to(IntercepterA.class);

        //binder.bind(ZplayRequestReceiver.class);
        // binder.bind(ExchangeRouter.class);

        binder().bind(RedisMessageHandler.class).toProvider(new Provider<RedisMessageHandler>() {
            @Override
            public RedisMessageHandler get() {
                return messageHandler();
            }
        });
    }

    @Provides
    @Singleton
    public BidController provideBidController(
            MetricRegistry metricRegistry, @BidInterceptors Set<BidInterceptor> interceptors) {

        return new BidController(Lists.newArrayList(interceptors), metricRegistry);
    }


    public RedisMessageHandler messageHandler() {
        RedisOptions config = new RedisOptions().setHost("123.59.150.167");
        RedisClient client = RedisClient.create(vertx, config);
        return new RedisMessageHandler(vertx, client);
    }

    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    public @interface BidInterceptors {
    }


}
