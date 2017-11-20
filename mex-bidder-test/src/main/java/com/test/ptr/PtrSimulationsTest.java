package com.test.ptr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mex.bidder.util.JsonHelper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;

import java.util.Random;
import java.util.UUID;


public class PtrSimulationsTest {

    static final Random c = new Random();

    static final double prob = 0.6d;

    public static void main(String[] args) throws InterruptedException {


        String jsonReqData = JsonHelper.readFile("zplay/zplay.req.json");
        jsonReqData = JsonHelper.readFile("iflytek/iflytek.req.json");

        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost("channel.ad-mex.com")
                .setDefaultPort(80);

        Vertx vertx = Vertx.vertx();

        new Thread(getRunner(jsonReqData, options, vertx)).start();
        new Thread(getRunner(jsonReqData, options, vertx)).start();
        new Thread(getRunner(jsonReqData, options, vertx)).start();
//        new Thread(getRunner(jsonReqData, options, vertx)).start();
        // new Thread(getRunner(jsonReqData, options, vertx)).start();
        //       new Thread(getRunner(jsonReqData, options, vertx)).start();

        Thread.currentThread().join();
    }

    private static Runnable getRunner(String jsonReqData, HttpClientOptions options, Vertx vertx) {
        return () -> {
            JSONObject reqObject = JSON.parseObject(jsonReqData);
            HttpClient client = vertx.createHttpClient(options);
            HttpClient clientImp = vertx.createHttpClient();
            Random r = new Random(System.currentTimeMillis());
            for (int i = 0; i < 100000; i++) {
                reqObject.put("id", UUID.randomUUID());
                HttpClientRequest httpClientRequest = client.post("/adsiflytek", resp -> {
                    System.out.println("Got response " + resp.statusCode());
                    resp.bodyHandler(body -> {
                        if (resp.statusCode() == 200) {
                            try {
                                JSONObject jsonObject = JSON.parseObject(body.toString());
                                System.out.println(jsonObject);
                                if (jsonObject.containsKey("seatbid")) {
                                    JSONArray seatbidArr = jsonObject.getJSONArray("seatbid");
                                    JSONObject seatbid = seatbidArr.getJSONObject(0);
                                    JSONArray bidArr = seatbid.getJSONArray("bid");
                                    JSONObject bid = bidArr.getJSONObject(0);
                                    JSONObject ext = bid.getJSONObject("ext");
                                    //JSONArray imptrackers = ext.getJSONArray("imptrackers");
                                    //JSONArray clktrackers = ext.getJSONArray("clktrackers");
                                    JSONObject banner_ad = bid.getJSONObject("banner_ad");
                                    JSONArray imptrackers = banner_ad.getJSONArray("impress");
                                    String impUrl = imptrackers.getString(0);

                                    JSONArray clktrackers = banner_ad.getJSONArray("click");
                                    String clickUrl = clktrackers.getString(0);

                                    clientImp.getAbs(impUrl.replace("${AUCTION_PRICE}", "0.00001"), impRes -> {
                                        if (impRes.statusCode() == 200) {
                                            System.out.println("send imp ok");
                                        } else {
                                            System.out.println("send imp error,status=" + impRes.statusCode());
                                        }
                                    }).end();

                                    if (r.nextDouble() < 0.025) {
                                        clientImp.getAbs(clickUrl, impRes -> {
                                            if (impRes.statusCode() == 200) {
                                                System.out.println("send clk ok");
                                            } else {
                                                System.out.println("send clk error,status=" + impRes.statusCode());
                                            }
                                        }).end();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Got data " + body.toString("utf-8"));
                        }
                    });
                });
                httpClientRequest.end(Buffer.buffer(JSON.toJSONBytes(reqObject)));
                try {
                    Thread.sleep(r.nextInt(100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
