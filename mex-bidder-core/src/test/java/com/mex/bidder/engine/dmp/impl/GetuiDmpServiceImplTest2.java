package com.mex.bidder.engine.dmp.impl;

import com.google.api.client.util.Maps;
import com.mex.bidder.engine.util.MD5Utils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Map;

/**
 * user: donghai
 * date: 2017/6/14
 */
public class GetuiDmpServiceImplTest2 {
    static final String user_code = "mex";
    static final String auth_code = "123321";
    static final String token = "d8066f00f85d9ca11a66eb6aa80eafcd";


    static final String AUTH_URL_QUERY = "user_code=%s&sign1=%s&sign2=%s&timestamp=%s";
    static HttpClient client;

    static {
        HttpClientOptions options = new HttpClientOptions();
        options.setTcpNoDelay(true);
        options.setTcpKeepAlive(true);
        options.setConnectTimeout(2000);
        options.setDefaultHost("115.236.68.59");
        options.setDefaultPort(8659);
        client = Vertx.vertx().createHttpClient(options);
    }

    @Test
    public void auth() throws Exception {
        GetuiDmpServiceImpl service = new GetuiDmpServiceImpl(Vertx.vertx(), new JsonObject());
        Thread.sleep(100000);
    }

    @Test
    public void myAuth() throws Exception {
        String timestamp = System.currentTimeMillis() + "";
        System.out.println(timestamp + " --timestampMd5= " + MD5Utils.MD5(timestamp));
        String sign1 = MD5Utils.MD5(user_code + MD5Utils.MD5(timestamp));
        String sign2 = MD5Utils.MD5(user_code + auth_code + MD5Utils.MD5(timestamp));
        String queryStr = String.format(AUTH_URL_QUERY, user_code, sign1, sign2, timestamp);
        System.out.println("sign1=" + sign1);
        System.out.println("sign2=" + sign2);
        System.out.println("http://115.236.68.59:8659/accesser/auth?" + queryStr);




        Thread.sleep(10000);
    }

    @Test
    public void retrieve() throws Exception {
        Map<String, String> data = Maps.newHashMap();
        data.put("os","android");
       // data.put("imei","867628024632234");
        data.put("imeiMd5","555a32ef3e9ee7e2585e78ac456a4829");


//        clientRequest.putHeader("Accept", "application/vnd.dmp.v1+json");
//        clientRequest.putHeader("Authorization", "Bearer " + token);
//        clientRequest.end(Json.encode(data));
        Thread.sleep(10000);
    }

}