package com.mex.bidder.api.vertx.guice;

import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;

/**
 * Implement VertxModule instead of Module to allow the Container and Vertx instance to be injected
 */
public abstract class VertxModule extends AbstractModule {

    public  abstract void setVertx(Vertx vertx);
}
