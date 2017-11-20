package com.mex.bidder.verticle;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试启动
 * <p>
 * User: donghai
 * Date: 2016/11/20
 */
public class Runner {
    private static final Logger logger = LoggerFactory.getLogger(DataVerticle.class);

    public static void main(String[] args) {
        logger.info("--- Deploy MainVerticle ---");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), event -> {
                    if (event.succeeded()) {
                        logger.info("Your Vert.x application is started!");
                    } else {
                        logger.error("Unable to start your application", event.cause());
                    }
                }
        );
    }
}
