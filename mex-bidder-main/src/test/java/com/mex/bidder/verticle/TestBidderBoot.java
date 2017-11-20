package com.mex.bidder.verticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: donghai
 * Date: 2016/11/14
 */
public class TestBidderBoot {
    private static final Logger logger = LoggerFactory.getLogger(TestBidderBoot.class);

    public static void main(String[] args) {
        logger.info("......................abc----");
        int procs = Runtime.getRuntime().availableProcessors();
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(TestDataVerticle.class.getName(),
                new DeploymentOptions().setInstances(1));

        vertx.deployVerticle(TestBidderApi.class.getName(),
                new DeploymentOptions().setInstances(1), event -> {
                    if (event.succeeded()) {
                        System.out.println("Your Vert.x application is started!");
                    } else {
                        System.out.println("Unable to start your application" + event.cause());
                    }
                });


    }

}