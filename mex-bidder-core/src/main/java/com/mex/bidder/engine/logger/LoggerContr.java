package com.mex.bidder.engine.logger;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import com.mex.bidder.api.http.ExchangeRouter;
import com.mex.bidder.api.http.HttpReceiver;
import com.mex.bidder.engine.util.HttpUtil;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xuchuanao
 * on 2017/1/11.
 *
 * 请求日志控制
 */
@Singleton
public class LoggerContr implements HttpReceiver, Handler<RoutingContext> {
    private static final Logger logger = LoggerFactory.getLogger(LoggerContr.class);

    public static final String Key = "mex@123";
    public static final String EB_REQLOG_VERTICLE = "eb.reqlog.verticle";

    @Override
    public void receive(RoutingContext ctx) {
        String secretKey = ctx.request().getParam("secret");
        @Nullable String channel = ctx.request().getParam("channel");
        @Nullable String isOpen = ctx.request().getParam("isOpen");

        if (!Key.equals(secretKey)) {
            HttpUtil.setStatusOk(ctx.response()).end("empty");
            return;
        }

        Vertx vertx = Vertx.vertx();

        vertx.eventBus().publish(EB_REQLOG_VERTICLE, channel+"||"+isOpen);

    }

    @Override
    public void handle(RoutingContext event) {
        receive(event);
    }
}
