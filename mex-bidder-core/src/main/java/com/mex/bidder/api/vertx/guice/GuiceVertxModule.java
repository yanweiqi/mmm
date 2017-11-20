package com.mex.bidder.api.vertx.guice;


import com.google.inject.Binder;
import com.google.inject.Module;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

/**
 * The module used by GuiceVertx
 */
public class GuiceVertxModule implements Module {

    private final Vertx vertx;


    public GuiceVertxModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(Vertx.class).toInstance(vertx);
        binder.bind(EventBus.class).toInstance(vertx.eventBus());
    }
}
