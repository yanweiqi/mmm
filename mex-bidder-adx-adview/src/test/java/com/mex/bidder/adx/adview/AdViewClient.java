package com.mex.bidder.adx.adview;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.mex.bidder.util.JsonHelper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;

/**
 * User: donghai
 * Date: 2016/11/23
 */
public class AdViewClient {


    public static void main(String[] args) {
        String req = JsonHelper.readFile("adview.req.json_1");
//        req = JsonHelper.readFile("adview-deviceid.json");
        //  req = JsonHelper.readFile("adview-online-native-1.json");

        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost("localhost").setDefaultPort(9080);
//                .setDefaultHost("testdspbidder.ad-mex.com").setDefaultPort(80);

//        .setDefaultHost("channel.ad-mex.com").setDefaultPort(80);
//        .setDefaultHost("123.56.14.96").setDefaultPort(8089);


        JSONObject jsonObject = JSON.parseObject(req);
        // 发送请求
        Runnable runnable = () -> {
            HttpClient client = vertx.createHttpClient(options);
            Stopwatch stopwatch = Stopwatch.createStarted();
            for (int i = 0; i < 1; i++) {
                HttpClientRequest post = client.post("/adsview", resp -> {
                    System.out.println("Got response " + resp.statusCode());
                    resp.bodyHandler(body -> {
                        if (resp.statusCode() == 200) {
                            System.out.println("response body --> " + new String(body.getBytes()));
                        } else {
                            System.out.println("response body --> " + new String(body.getBytes()));
                            System.out.println("Got data " + body.toString("utf-8"));
                        }
                    });
                });
                //   long id = Thread.currentThread().getId();
                //   jsonObject.put("id", id + "-" + i);
                post.end(jsonObject.toJSONString());
                try {
                    Thread.sleep(10l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopwatch.stop();
            System.out.println("------OVER------" + stopwatch);
        };

        new Thread(runnable).start();
        //new Thread(runnable).start();
        // new Thread(runnable).start();
    }
}
