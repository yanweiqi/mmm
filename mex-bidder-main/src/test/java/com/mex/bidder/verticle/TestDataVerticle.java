package com.mex.bidder.verticle;

import com.mex.bidder.api.vertx.guice.GuiceVerticleHelper;
import com.mex.bidder.api.vertx.guice.GuiceVertxBinding;
import com.mex.bidder.engine.bizdata.RedisMessageHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * 加载数据的Verticle
 * <p>
 */
@GuiceVertxBinding(modules = {BidderApiTestModule.class})
public class TestDataVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(TestDataVerticle.class);

    @Inject
    private RedisMessageHandler redisMessageHandler;

    @Override
    public void start() throws Exception {
        super.start();

        GuiceVerticleHelper.inject(this, vertx);

        System.out.println("----------install data verticle -------------");

        redisMessageHandler.initFullDataAtStartUp();

        // 订阅Redis
        RedisOptions config = new RedisOptions().setHost("127.0.0.1");
        RedisClient redis = RedisClient.create(vertx, config);

        redis.psubscribe("rtb.*", res -> {
            if (res.succeeded()) {
                System.out.println("redis sub [rtb.*] channel ok");
            } else {
                logger.error("sub [rtb.*] error.", res.cause().fillInStackTrace());
            }
        });

        vertx.eventBus().consumer("io.vertx.redis.rtb.*", (Message<JsonObject> msg) -> {
            JsonObject value = msg.body().getJsonObject("value");
            System.out.println("receive event -> " + value);
            redisMessageHandler.handle(value);
        });

        System.out.println("--------- install data verticle end ------------");
    }
}
