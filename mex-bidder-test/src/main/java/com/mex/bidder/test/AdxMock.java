package com.mex.bidder.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.mex.bidder.util.JsonHelper;
import com.mex.bidder.util.MexUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;


/**
 * user: donghai
 * date: 2016/12/26
 */
public abstract class AdxMock {

    private static final Logger logger = LoggerFactory.getLogger(AdviewAdxMock.class);

    protected Vertx vertx;
    protected HttpClient client;
    protected HttpClient monitorClient;


    protected void setMonitor(Vertx vertx) {
        Properties properties = load();
        String host = properties.getProperty("tracker.host");
        String port = properties.getProperty("tracker.port", "80");
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost(host).setDefaultPort(Integer.parseInt(port))
                .setConnectTimeout(100);
        monitorClient = vertx.createHttpClient(options);
    }


    protected AdxMock() {
        Properties properties = load();
        String host = properties.getProperty("bidder.host");
        String port = properties.getProperty("bidder.port", "8080");
        vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost(host).setDefaultPort(Integer.parseInt(port))
                .setConnectTimeout(100);
        client = vertx.createHttpClient(options);

        setMonitor(vertx);
    }

    protected abstract void run();

    protected void send(String adxId, String macro, String field) {

        Runnable runnable = () -> {
            for (int i = 0; i < 1; i++) {
                HttpClientRequest post = client.post("/" + adxId, resp -> {
                    resp.bodyHandler(body -> {
                        if (resp.statusCode() == 200) {
                            String resData = new String(body.getBytes());
                            logger.info("response body --> " + resData);
                            JSONObject jsonObject = JSON.parseObject(resData);

                            JSONObject bidJson = jsonObject.getJSONArray("seatbid").getJSONObject(0).getJSONArray("bid").getJSONObject(0);
                            String url = getImpUrl(bidJson);
                            HttpResponse response = null;
                            if (null != url) {
                                try {
                                    String replace = url.replace(macro, "100");
                                    org.apache.http.client.HttpClient client = new DefaultHttpClient();
                                    HttpGet request = new HttpGet();
                                    request.setURI(new URI(replace));
                                    response = client.execute(request);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (response.getStatusLine().getStatusCode() == 200) {
                                System.out.println("adview imp request is ok !!");
                            } else {
                                System.err.println("adview imp request is error !!");
                            }

                        } else {
                            logger.error("dsp error " + body.toString("utf-8"));
                        }
                    });
                });
                JSONObject jsonObject = JSON.parseObject(readJson());
                jsonObject.put("id", MexUtil.uuid());
                post.end(jsonObject.toJSONString());

            }
        };

        for (int i = 0 ; i < 1 ; i ++){
            new Thread(runnable).start();
        }

    }

    protected abstract String readJson();

    protected abstract String getImpUrl(JSONObject bid);

    protected Properties load() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = Resources.getResource("develop/config.properties").openStream();
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {

        }

        return properties;
    }

}
