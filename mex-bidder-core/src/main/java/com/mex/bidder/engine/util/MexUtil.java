package com.mex.bidder.engine.util;

import com.google.api.client.util.Maps;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.constants.OS;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.DictType;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisOptions;

import java.util.*;

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

    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     *    
     *  * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米   
     *  
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;// 单位是KM
        s = s * 1000;
        return s;
    }

    public static OS toMexOS(BidRequest bidRequest, MexDataContext mexDataContext) {
        String os = getOS(bidRequest);

        if (OS.iOS.name().equalsIgnoreCase(os)) {
            return OS.iOS;
        } else if (OS.Android.name().equalsIgnoreCase(os)) {
            return OS.Android;
        } else {
            Map<String, String> dictMap = mexDataContext.getCommonDictByType(DictType.OS);
            String dicMapOsIndex = dictMap.getOrDefault(os, "");
            OS lookup = OS.lookup(dicMapOsIndex);
            return lookup;
        }
    }

    private static String getOS(BidRequest bidRequest) {
        if (bidRequest.openRtb().hasDevice()) {
            return bidRequest.openRtb().getDevice().getOs();
        } else {
            return "";
        }
    }


    /**
     * 将key和次数对应起来
     *
     * @param keys
     * @param values
     * @return
     */
    public static Map<String, Integer> zipToMap(Set<String> keys, List<String> values) {
        Map<String, Integer> result = Maps.newHashMap();
        int i = 0;
        for (String key : keys) {
            if (Objects.nonNull(values.get(i))) {
                result.put(key, toInteger(values.get(i)));
            } else {
                result.put(key, 0);
            }
            i++;
        }
        return result;
    }

    private static Integer toInteger(String s) {
        return Objects.nonNull(s) ? Integer.parseInt(s) : 0;
    }


    public static void main(String[] args) {
        double distance = getDistance(27.847808, 105.735895, 39.914435, 116.467523);
        System.out.println("===" + distance);
    }

}
