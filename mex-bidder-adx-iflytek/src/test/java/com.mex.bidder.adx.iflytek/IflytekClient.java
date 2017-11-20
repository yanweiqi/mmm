package com.mex.bidder.adx.iflytek;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mex.bidder.util.JsonHelper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;

/**
 * User: donghai
 * Date: 2016/11/23
 */
public class IflytekClient {


    public static void main(String[] args) throws InterruptedException {
        String req = JsonHelper.readFile("iflytek.req.json");
//        req = JsonHelper.readFile("native-request.json");
//        req = JsonHelper.readFile("native-request-1pic.json");
//        req = JsonHelper.readFile("native-req-3pic.json");
        //req = JsonHelper.readFile("native-request-1pic2desc.json");


        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost("localhost").setDefaultPort(9080);
//                .setDefaultHost("123.56.14.96").setDefaultPort(80);
//                .setDefaultHost("101.200.34.111").setDefaultPort(9080);
//                    .setDefaultHost("testdspbidder.ad-mex.com").setDefaultPort(80);
//                    .setDefaultHost("channel.ad-mex.com").setDefaultPort(80);

        JSONObject jsonObject = JSON.parseObject(req);
        // 发送请求
        Runnable runnable = () -> {
            HttpClient client = vertx.createHttpClient(options);

//            for (int i = 0; i < 10000; i++) {
            HttpClientRequest post = client.post("/adsiflytek", resp -> {
                System.out.println("Got response " + resp.statusCode());
                resp.bodyHandler(body -> {
                    if (resp.statusCode() == 200) {
                        String s = new String(body.getBytes());
                        System.out.println("response body --> " + s);
                        JSONObject object = JSONObject.parseObject(s);
                        Object seatbid = object.getJSONArray("seatbid").get(0);

                    } else {
                        System.out.println("Got data " + body.toString("utf-8"));
                    }
                });
            });
            long id = Thread.currentThread().getId();
//                jsonObject.put("id", id + "-" + i);
            post.end(jsonObject.toJSONString());
//            }
        };

        new Thread(runnable).start();
    }

}