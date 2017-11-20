package com.mex.bidder.api.http;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mex.bidder.engine.constants.Constants;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 路由exchange请求到特定的receiver进行解码，编码
 * <p>
 */
@Singleton
public class ExchangeRouter implements HttpReceiver, Handler<RoutingContext> {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRouter.class);


    private final MetricRegistry metricRegistry;
    private final Timer requestTimer;

    private final ImmutableMap<String, HttpReceiver> routeTable;
    private final Vertx vertx;

    @Inject
    public ExchangeRouter(Set<ExchangeRoute> exchangeRoutes, MetricRegistry metricRegistry, Vertx vertx) {
        Map<String, HttpReceiver> routes = new HashMap<>();
        exchangeRoutes.forEach(exchangeRoute -> {
            routes.put(exchangeRoute.getPath(), exchangeRoute.getHttpReceiver());
        });
        routeTable = ImmutableMap.copyOf(routes);
        this.metricRegistry = metricRegistry;
        requestTimer = buildTimer("base-request-timer");
        this.vertx = vertx;
    }

    @Override
    public void receive(RoutingContext ctx) {
        String exchangePath = ctx.pathParam("channelName");

        // 查找exchange渠道对应的Receiver
        if (routeTable.containsKey(exchangePath)) {
            Timer.Context timerContext = requestTimer.time();
            HttpReceiver httpReceiver = routeTable.get(exchangePath);
            httpReceiver.receive(ctx);
            timerContext.close();
            // 流量累加
            vertx.eventBus().publish(Constants.EB_QPS_VERTICLE,"add_pqs");
        } else {
            logger.warn("no exchange receiver found with name {}, path {}", exchangePath, ctx.request().uri());
            ctx.response().setStatusCode(404).end();
        }
    }

    /**
     * Create a {@link Timer} for this receiver.
     */
    protected Timer buildTimer(String name) {
        return metricRegistry.register(MetricRegistry.name(getClass(), name), new Timer());
    }


    @Override
    public String toString() {
        return routeTable.toString();
    }

    @Override
    public void handle(RoutingContext event) {
        receive(event);
    }
}
