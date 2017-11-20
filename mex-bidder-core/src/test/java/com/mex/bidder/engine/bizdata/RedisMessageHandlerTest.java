package com.mex.bidder.engine.bizdata;

import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: donghai
 * Date: 2016/11/18
 */
public class RedisMessageHandlerTest {
    @Test
    public void handle() throws Exception {

    }

    @Test
    public void handleAdxConfig() throws Exception {

    }

    @Test
    public void handleAdxCreativeState() throws Exception {

    }

    @Test
    public void initFullDataAtStartUp() throws Exception {

    }

    Vertx vertx;
    RedisClient client;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        RedisOptions config = new RedisOptions();
        // config.setHost("123.59.150.167");
        config.setHost("127.0.0.1");
        client = RedisClient.create(vertx, config);

    }

    @Test
    public void handleChannelConf() throws Exception {

        RedisMessageHandler handler = new RedisMessageHandler(vertx, client);

        CountDownLatch latch = new CountDownLatch(1);
//
//        handler.handleAdxConfig();
//        handler.handleCreatives();

        System.out.println("---- end --");
        latch.await(20, TimeUnit.SECONDS);

//        client.mget

    }

    @Test
    public void handleCreatives() throws Exception {
        RedisMessageHandler handler = new RedisMessageHandler(vertx, client);
        CountDownLatch latch = new CountDownLatch(1);
//        handler.handleCreatives();
        System.out.println("---- end --");
        latch.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void handleChannelCharDict() throws Exception {
        RedisMessageHandler handler = new RedisMessageHandler(vertx, client);
        CountDownLatch latch = new CountDownLatch(1);
//        handler.handleChannelCommonCharDict();
        System.out.println("---- end --");
        latch.await(130, TimeUnit.SECONDS);
    }

    @Test
    public void getChannelIndependentDict() throws InterruptedException {
        RedisMessageHandler handler = new RedisMessageHandler(vertx, client);
        CountDownLatch latch = new CountDownLatch(1);
//        handler.getChannelIndependentDict("adszp");
        System.out.println("---- end --");
        latch.await(130, TimeUnit.SECONDS);
    }


}