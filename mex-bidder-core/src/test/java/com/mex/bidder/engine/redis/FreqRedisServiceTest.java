package com.mex.bidder.engine.redis;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableSet;
import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * User: donghai
 * Date: 2017/1/11
 */
public class FreqRedisServiceTest {

    RedisClient client;

    public static void main(String[] args) throws Exception {
        FreqRedisServiceTest freqRedisServiceTest = new FreqRedisServiceTest();
        freqRedisServiceTest.getFreqCap();
    }


    @Before
    public void setUp() throws Exception {
        Vertx vertx = Vertx.vertx();
        RedisOptions redisOptions = new RedisOptions()
                .setHost("localhost")
                .setPort(6379);
        //redisOptions.setAuth("dsplocalredis");
        client = RedisClient.create(vertx, redisOptions);
    }

    @Test
    public void getFreqCap() throws Exception {
        FreqRedisService freqRedisService = new FreqRedisService(client);
        CountDownLatch latch = new CountDownLatch(1);
        freqRedisService.getFreqCap("k_1", ImmutableSet.of("f1", "f2"), res -> {
            if (res.succeeded()) {
                List<String> result = res.result();
                System.out.println(JSON.toJSONString(result));
            } else {
                System.out.println(res.cause().fillInStackTrace().getMessage());
            }
            latch.countDown();
        });

        latch.await();
    }

}