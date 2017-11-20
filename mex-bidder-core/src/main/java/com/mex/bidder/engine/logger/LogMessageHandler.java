package com.mex.bidder.engine.logger;

import com.google.inject.Inject;
import io.vertx.core.Vertx;

/**
 * xuchuanao
 * on 2017/1/11.
 */
public class LogMessageHandler {

    private final Vertx vertx;

    @Inject
    LogMessageHandler(Vertx vertx ){
        this.vertx = vertx;
    }

    public static void handle() {
//        vertx.eventBus().consumer("channel");

    }
}
