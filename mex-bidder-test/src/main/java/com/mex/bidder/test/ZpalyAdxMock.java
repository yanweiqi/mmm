package com.mex.bidder.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mex.bidder.util.JsonHelper;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/12/27.
 */
public class ZpalyAdxMock extends AdxMock {

    Logger logger = LoggerFactory.getLogger(ZpalyAdxMock.class);

    public static void main(String[] args) throws Exception {
        ZpalyAdxMock zpalyAdxMock = new ZpalyAdxMock();
        zpalyAdxMock.run();

    }

    @Override
    protected void run() {
        send("adszp","{AUCTION_BID_PRICE}","iurl");
    }

    @Override
    protected String readJson() {
        return JsonHelper.readFile("zplay/zplay.req.json");
    }

    @Override
    protected String getImpUrl(JSONObject bid) {
        JSONArray impUrls = bid.getJSONArray("nurl");
        if(!impUrls.isEmpty()){
            return impUrls.getString(0);
        }
        return null;
    }
}
