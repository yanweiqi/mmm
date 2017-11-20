package com.mex.bidder.api.http;

import io.vertx.ext.web.RoutingContext;

/**
 * Handle an HTTP request.
 */
public interface HttpReceiver {

    void receive(RoutingContext ctx);
}
