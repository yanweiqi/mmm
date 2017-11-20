
package com.mex.bidder.engine.metrics;

import com.google.common.net.MediaType;
import com.mex.bidder.api.http.HttpReceiver;
import com.mex.bidder.engine.util.HttpUtil;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Processes ping request.
 */
public class PingHttpReceiver implements HttpReceiver, Handler<RoutingContext> {

    @Override
    public void receive(RoutingContext ctx) {
        HttpUtil.setStatusOk(ctx.response());
        HttpUtil.setMediaType(ctx.response(), MediaType.PLAIN_TEXT_UTF_8)
                .putHeader("Cache-Control", "must-revalidate,no-cache,no-store")
                .end("pong");
    }

    @Override
    public void handle(RoutingContext event) {
        receive(event);
    }
}
