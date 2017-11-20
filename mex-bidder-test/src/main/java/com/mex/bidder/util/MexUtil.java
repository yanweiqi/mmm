package com.mex.bidder.util;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.protocol.Ad;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisOptions;

import java.util.UUID;

/**
 * User: donghai
 * Date: 2016/11/19
 */
public class MexUtil {

    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String uuid(OpenRtb.BidRequest bidRequest, Ad ad) {
        return null;
    }

    public static RedisOptions createRedisOptions(JsonObject conf) {
        RedisOptions options = new RedisOptions();
        options.setHost(conf.getString("host"));
        options.setPort(conf.getInteger("port"));
        return options;
    }


}
