package com.mex.bidder.engine.redis;

import com.google.inject.AbstractModule;
import io.vertx.core.json.JsonObject;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Objects;

/**
 * xuchuanao
 * on 2016/12/28.
 */
public class JedisModule extends AbstractModule {

    private JsonObject cnf;

    public JedisModule(JsonObject cnf) {
        this.cnf = cnf;
    }

    @Override
    protected void configure() {
        bind(JedisPool.class).toInstance(newJedis());

    }

    public JedisPool newJedis() {

        JsonObject redisJson = cnf.getJsonObject("query-redis");
        String host = redisJson.getString("host");
        Integer port = redisJson.getInteger("port");
        String auth = redisJson.getString("auth");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(20);
        poolConfig.setMinIdle(8);
        poolConfig.setMaxWaitMillis(1000);
        poolConfig.setMaxTotal(150);
        poolConfig.setTestOnBorrow(true);
        if (Objects.nonNull(auth)) {
            return new JedisPool(poolConfig, host, port, 20, auth);
        } else {
            return new JedisPool(poolConfig, host, port, 20);
        }

    }
}
