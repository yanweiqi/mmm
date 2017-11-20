package com.mex.bidder.adx.zplay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.protobuf.ExtensionRegistry;
import com.mex.bidder.util.JsonHelper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public class ZplayJsonClient {
    private static final ExtensionRegistry registry = ExtensionRegistry.newInstance();

    public static void main(String[] args) {
        String req = JsonHelper.readFile("zplay.req.json");

        Vertx vertx = Vertx.vertx();
        // HttpClientOptions options = new HttpClientOptions()
        //         .setDefaultHost("106.75.8.97").setDefaultPort(9901);
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost("localhost").setDefaultPort(9080);


        JSONObject jsonObject = JSON.parseObject(req);
        // 发送请求
        Runnable runnable = () -> {
            HttpClient client = vertx.createHttpClient(options);
            Stopwatch stopwatch = Stopwatch.createStarted();
            // for (int i = 0; i < 100000; i++) {
            HttpClientRequest post = client.post("/adszp?debug=true", resp -> {
                System.out.println("Got response " + resp.statusCode());
                resp.bodyHandler(body -> {
                    if (resp.statusCode() == 200) {
                        System.out.println("response body --> " + new String(body.getBytes()));
                    } else {
                        System.out.println("Got data " + body.toString("utf-8"));
                    }
                });
            });
            //   long id = Thread.currentThread().getId();
            //   jsonObject.put("id", id + "-" + i);
            post.end(jsonObject.toJSONString());
            //Thread.sleep();
            //}
            //  stopwatch.stop();

            // System.out.println("------OVER------" + stopwatch);
        };

        new Thread(runnable).start();
        //new Thread(runnable).start();
        // new Thread(runnable).start();
    }
}
