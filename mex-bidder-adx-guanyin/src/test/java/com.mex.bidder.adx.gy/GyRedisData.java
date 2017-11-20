package com.mex.bidder.adx.gy;

import com.mex.bidder.util.JsonHelper;
import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: donghai
 * Date: 2016/11/23
 */

public class GyRedisData {
    Vertx vertx;
    RedisClient client;

    @Test
    public void gy_redis_data() throws InterruptedException {

        String gyCnf = JsonHelper.readFile("gy.redis.cnf.json");
        String creatives = JsonHelper.readFile("gy.redis.creatives.json");

        CountDownLatch latch = new CountDownLatch(2);
        client.hset("rtb.creatives", "content", creatives, event -> {
            System.out.println("set gy creatives ok");
            latch.countDown();
        });

        /*client.hset("rtb.channel.message", "adsgy", gyCnf, event -> {
            System.out.println("set gy conf ok");
            latch.countDown();
        });*/

        latch.await(30, TimeUnit.SECONDS);
    }


    @Before
    public void setUp() throws Exception {
        // 本地redis
        vertx = Vertx.vertx();
        RedisOptions config = new RedisOptions().setHost("192.168.1.136").setPort(6379);
        client = RedisClient.create(vertx, config);
    }
}
