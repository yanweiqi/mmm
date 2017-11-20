package com.mex.bidder.adx.zplay.zplay;

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

public class ZplayRedisData {
    Vertx vertx;
    RedisClient client;

    @Test
    public void adview_redis_data() throws InterruptedException {

        String adxCnf = JsonHelper.readFile("zplay.redis.cnf.json");
        String creatives = JsonHelper.readFile("zplay.redis.creatives.json");

        CountDownLatch latch = new CountDownLatch(2);
        client.hset("rtb.creatives", "content", creatives, event -> {
            System.out.println("set adx creatives ok");
            latch.countDown();
        });

        client.hset("rtb.channel.message", "adszp", adxCnf, event -> {
            System.out.println("set adx conf ok");
            latch.countDown();
        });

        latch.await(30, TimeUnit.SECONDS);
    }


    @Before
    public void setUp() throws Exception {
        // 本地redis
        vertx = Vertx.vertx();
        RedisOptions config = new RedisOptions().setHost("192.168.99.100");
        client = RedisClient.create(vertx, config);
    }
}
