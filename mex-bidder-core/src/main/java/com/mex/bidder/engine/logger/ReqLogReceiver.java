package com.mex.bidder.engine.logger;

import com.alibaba.fastjson.JSON;
import com.google.common.net.MediaType;
import com.google.inject.Inject;
import com.mex.bidder.api.http.HttpReceiver;
import com.mex.bidder.engine.util.HttpUtil;
import com.mex.bidder.protocol.Const;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * xuchuanao
 * on 2017/1/12.
 */
public class  ReqLogReceiver implements HttpReceiver, Handler<RoutingContext> {

    @Inject
    Vertx vertx;

    public static final String Key = "mex@123";

    @Override
    public void receive(RoutingContext ctx) {
        String secretKey = ctx.request().getParam("secret");
        if (!Key.equals(secretKey)) {
            HttpUtil.setStatusOk(ctx.response()).end("empty");
            return;
        }
        @Nullable String channel = ctx.request().getParam("channel");
        @Nullable String isOpen = ctx.request().getParam("isopen");

        JsonObject json = new JsonObject();
        json.put("channel",channel);
        json.put("isOpen",isOpen);

        try {

            vertx.eventBus().publish(Const.EB_REQLOG,json);

            HttpUtil.setStatusOk(ctx.response());
            HttpUtil.setMediaType(ctx.response(), MediaType.JSON_UTF_8).end(JSON.toJSONString(json));

        }catch (Exception e){
            HttpUtil.setStatusOk(ctx.response());
            HttpUtil.setMediaType(ctx.response(), MediaType.JSON_UTF_8).end("error");
        }



    }

    @Override
    public void handle(RoutingContext event) {
        receive(event);
    }
}
