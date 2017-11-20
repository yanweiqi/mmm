package com.mex.bidder.engine.dmp.impl;

import com.google.api.client.util.Maps;
import com.mex.bidder.engine.util.MD5Utils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Map;

/**
 * 测试配置
 * "userCode": "mex",
 * "authCode": "123321",
 * "host": "115.236.68.59",
 * "port": 8659,
 * <p>
 * 生产配置
 * "userCode": "mobiex",
 * "authCode": "Pws3gSZ1f69GuWTDmdd4p1",
 * "host": "dmp-bj.abeacon.com",
 * <p>
 * user: donghai
 * date: 2017/6/14
 */
public class GetuiDmpServiceImplTest {
    static final String user_code = "mobiex";
    static final String auth_code = "Pws3gSZ1f69GuWTDmdd4p1";
    static final String token = "5070012512f4f4928f36b3f6b17a7c70";


    static final String AUTH_URL_QUERY = "user_code=%s&sign1=%s&sign2=%s&timestamp=%s";
    static HttpClient client;

    static {
        HttpClientOptions options = new HttpClientOptions();
        options.setTcpNoDelay(true);
        options.setTcpKeepAlive(true);
        options.setConnectTimeout(2000);
        options.setDefaultHost("dmp-bj.abeacon.com");
        options.setDefaultPort(80);
        // options.setDefaultHost("115.236.68.59");
        //options.setDefaultPort(8659);
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
        client.get("/accesser/auth?" + queryStr, res -> {
            if (res.statusCode() == 200) {
                res.bodyHandler(body -> {
                    System.out.println(body.toString("utf-8"));
                });
            } else {
                System.out.println("error code=" + res.statusCode());
            }
        }).putHeader("Accept", "application/vnd.dmp.v1+json").end();
        Thread.sleep(10000);
    }

    @Test
    public void retrieve() throws Exception {
        Map<String, String> data = Maps.newHashMap();
        //data.put("os","android");
        data.put("imeiMD5", "555a32ef3e9ee7e2585e78ac456a4829");
        HttpClientRequest clientRequest = client.post("/user/tasklist", res -> {
            if (res.statusCode() == 200) {
                res.bodyHandler(body -> {
                    System.out.println(body.toString("utf-8"));
                });
            } else {
                System.out.println("error code=" + res.statusCode());
            }
        });

        clientRequest.putHeader("Accept", "application/vnd.dmp.v1+json");
        clientRequest.putHeader("Authorization", "Bearer " + token);
        clientRequest.end(Json.encode(data));
        Thread.sleep(10000);
    }

}