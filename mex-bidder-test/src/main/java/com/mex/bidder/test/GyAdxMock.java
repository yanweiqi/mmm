package com.mex.bidder.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.adx.gy.GyOpenRtb;
import com.mex.bidder.util.JsonHelper;
import com.mex.bidder.util.MexUtil;
import io.vertx.core.buffer.Buffer;
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
public class GyAdxMock extends AdxMock {

    Logger logger = LoggerFactory.getLogger(GyAdxMock.class);

    static  GyOpenRtb.BidRequest bidRequest = GyOpenRtb.BidRequest.newBuilder()
            .setId("1234534625254")
            .setAt(OpenRtb.AuctionType.SECOND_PRICE_VALUE)
            .setTmax(120)
            .addImp(GyOpenRtb.Imp.newBuilder()
                    .setId("1")
                    .setBidfloor(2.2f)
                    .setBanner(GyOpenRtb.Banner.newBuilder()
                            .setId("1")
//                            .setW(300)
//                    .setH(200)
                            .setW(640)
                            .setH(100)
//                            .setW(728)
//                            .setH(90)
                            .setPos(GyOpenRtb.AdPosition.ABOVE_THE_FOLD)
                            .addBattr(GyOpenRtb.CreativeAttribute.ATTR_701)))
//                .addBadv("company1.com")
//                .addBadv("company2.com")

            .setApp(GyOpenRtb.App.newBuilder()
                    .setId("1")
                    .setBundle("sd")
                    .setPaid(11)
                    .setPublisher(GyOpenRtb.Publisher.newBuilder()
                            .setId("1")
                            .setDomain("www.mex.com")))
//                        .setKeywords(2,"dsfd"))
            .setDevice(GyOpenRtb.Device.newBuilder()
//                    .setIp("64.124.253.1")
                    .setIp("1.0.2.1")
                    .setUa("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16)")
                    .setOs("ios")
                    .setDevicetype(GyOpenRtb.DeviceType.MOBILE)
                    .setConnectiontype(GyOpenRtb.ConnectionType.WIFI)
                    .setW(640)
                    .setH(100)
//                    .setW(600)
//                    .setH(100)
//                    .setW(728)
//                    .setH(90)
                    .setGeo(GyOpenRtb.Geo.newBuilder().setType(GyOpenRtb.LocationType.GPS))
//                        .setFlashver("10.1")
                    .setJs(1))
            .setUser(GyOpenRtb.User.newBuilder()
                    .setId("45asdf987656789adfad4678rew656789"))
//                        .setBuyeruid("5df678asd8987656asdf78987654"))
            .setTest(1)
            .setScenario(GyOpenRtb.Scenario.newBuilder().setType(GyOpenRtb.ScenarioType.APP))

            .build();



    public static void main(String[] args) throws Exception {
        GyAdxMock gyAdxMock = new GyAdxMock();
        gyAdxMock.run();

    }

    @Override
    protected void run() {
        CountDownLatch latch = new CountDownLatch(1);
        HttpClientRequest post = client.post("/adsgy", resp -> {
            resp.bodyHandler(body -> {
                if (resp.statusCode() == 200) {
                    String resData = new String(body.getBytes());
                    logger.info("response body --> " + resData);
                    JSONObject jsonObject = JSON.parseObject(resData);
                    JSONArray impUrls = jsonObject.getJSONArray("seatbid").getJSONObject(0).getJSONArray("bid").getJSONObject(0).getJSONArray("nurl");
                    if (null != impUrls) {
                        HttpResponse response = null;
                        if (!impUrls.isEmpty()) {
                            try {
                                String replace = impUrls.getString(0).replace("${AUCTION_PRICE}", "100");
                                HttpClient client = new DefaultHttpClient();
                                HttpGet request = new HttpGet();
                                request.setURI(new URI(replace));
                                response = client.execute(request);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (response.getStatusLine().getStatusCode() == 200) {
                                System.out.println("iflytek imp request is ok !!");
                            } else {
                                System.err.println("iflytek imp request is error !!");
                            }
                        }
                    } else {
                        logger.error("dsp error response imp is null");
                    }
                } else {
                    logger.error("dsp error " + body.toString("utf-8"));
                }
            });
        });

        GyOpenRtb.BidRequest request = bidRequest.toBuilder().setId(MexUtil.uuid()).build();
        post.end(Buffer.buffer(request.toByteArray()));

    }

    @Override
    protected String readJson() {
        return null;
    }

    @Override
    protected String getImpUrl(JSONObject bid) {
        return null;
    }
}
