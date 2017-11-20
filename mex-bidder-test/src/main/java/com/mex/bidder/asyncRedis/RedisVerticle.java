package com.mex.bidder.asyncRedis;

import com.mex.bidder.util.JsonHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * xuchuanao
 * on 2017/1/11.
 */
public class RedisVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        String confJson = JsonHelper.readFile("main.cnf.json");
        JsonObject conf = new JsonObject(confJson);


        super.start(startFuture);
    }
}
