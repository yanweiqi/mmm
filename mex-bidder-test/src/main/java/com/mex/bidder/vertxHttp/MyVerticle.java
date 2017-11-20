package com.mex.bidder.vertxHttp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * xuchuanao
 * on 2017/1/20.
 */
public class MyVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {


        vertx.deployVerticle("com.foo.vertivle",res -> {
            if (res.succeeded()){
                startFuture.complete();
            } else {
                startFuture.failed();
            }
        });
        super.start(startFuture);
    }
}
