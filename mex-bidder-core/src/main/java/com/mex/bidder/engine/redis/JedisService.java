package com.mex.bidder.engine.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/12/15.
 */
@Singleton
public class JedisService {

    @Inject
    private JedisPool jedisPool;


    public List<String> hmget(String key, Set<String> fields) {
        return getFromRedis(key, fields);
    }

    public List<String> hget(Set<String> fields) {
        return getFromRedis(fields);
    }

    //
    List<String> getFromRedis(Set<String> fields) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.mget(fields.toArray(new String[0]));
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }


    /**
     * 查询Redis
     *
     * @param key
     * @param fields
     * @return
     */
    List<String> getFromRedis(String key, Set<String> fields) {
        Jedis jedis = null;
        try {

            jedis = getResource();
            return jedis.hmget(key, fields.toArray(new String[0]));
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 获取资源
     *
     * @return
     * @throws RuntimeException
     */
    public Jedis getResource() {
        Jedis jedis = null;
        try {
//            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//            jedisPoolConfig.set

            jedis = jedisPool.getResource();

        } catch (JedisException e) {
            throw new RuntimeException(e);
        }
        return jedis;
    }
}
