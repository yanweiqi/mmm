package com.mex.bidder.engine.redis;

import com.google.inject.AbstractModule;
import com.mex.bidder.config.FreqRedis;
import com.mex.bidder.config.PubSubRedis;
import com.mex.bidder.config.Redis;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.util.Objects;

/**
 * User: donghai
 * Date: 2016/11/20
 */
public class RedisModule extends AbstractModule {

    private Vertx vertx;
    private JsonObject cnf;

    public RedisModule(Vertx vertx, JsonObject cnf) {
        this.vertx = vertx;
        this.cnf = cnf;
    }

    @Override
    protected void configure() {
        bind(RedisClient.class).annotatedWith(Redis.class).toInstance(newRedisClient());
        bind(RedisClient.class).annotatedWith(PubSubRedis.class).toInstance(newRedisClient());
        bind(RedisClient.class).annotatedWith(FreqRedis.class).toInstance(FreqRedisClient());
    }

    public RedisClient newRedisClient() {
        JsonObject redis = cnf.getJsonObject("message-redis");
        String host = redis.getString("host");
        Integer port = redis.getInteger("port");
        String auth = redis.getString("auth");
        Objects.requireNonNull(host, "message-redis.host can't be empty");
        Objects.requireNonNull(port, "message-redis.port can't be empty");

        RedisOptions config = new RedisOptions().setHost(host).setPort(port)
                .setTcpKeepAlive(true).setTcpNoDelay(true);
        if (Objects.nonNull(auth)) {
            config.setAuth(auth);
        }
        RedisClient client = RedisClient.create(vertx, config);
        return client;
    }


    public RedisClient FreqRedisClient() {
        JsonObject redis = cnf.getJsonObject("query-redis");
        String host = redis.getString("host");
        Integer port = redis.getInteger("port");
        String auth = redis.getString("auth");
        Objects.requireNonNull(host, "query-redis.host can't be empty");
        Objects.requireNonNull(port, "query-redis.port can't be empty");

        RedisOptions config = new RedisOptions()
                .setHost(host)
                .setPort(port)
                .setTcpKeepAlive(true)
                .setTcpNoDelay(true);
        if (Objects.nonNull(auth)) {
            config.setAuth(auth);
        }
        RedisClient client = RedisClient.create(vertx, config);
        return client;
    }
}
