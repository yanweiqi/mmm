package com.mex.bidder.engine.redis;

import com.google.api.client.util.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.redis.RedisClient;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/12/15.
 */
@Singleton
public class FreqRedisService {

    private RedisClient redisClient;

    @Inject
    public FreqRedisService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void getFreqCap(String key, Set<String> fields, Handler<AsyncResult<List<String>>> resultHandler) {
        redisClient.hmget(key, Lists.newArrayList(fields), ar -> {
            if (ar.succeeded()) {
                System.out.println("----");
                 resultHandler.handle(Future.succeededFuture());
            } else {
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

}
