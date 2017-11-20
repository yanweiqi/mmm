package com.mex.bidder.api.vertx.guice;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Base class for Guice/Vert.x integration.
 */
public abstract class GuiceVerticle extends AbstractVerticle {


    @Override
    public final void start(Future<Void> startedResult) {
        GuiceVerticleHelper.inject(this, vertx);
        onStart(startedResult);
    }

    /**
     * 子类实现verticle初始化
     */
    public void onStart() {

    }


    public void onStart(Future<Void> startedResult) {
        onStart();
        startedResult.complete();
    }

}
