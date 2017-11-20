package com.mex.bidder.adx.sohu;

import com.google.protobuf.InvalidProtocolBufferException;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;

/**
 * xuchuanao
 * on 2017/3/20.
 */
public class SohuClient {

    public static void main(String[] args) {
        SohuOpenRtb.Request.Builder req = getReq2();
        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions()
//                .setDefaultHost("123.56.14.96").setDefaultPort(80);
                .setDefaultHost("localhost").setDefaultPort(9080);

        HttpClient client = vertx.createHttpClient(options);


        HttpClientRequest post = client.post("/adssohu", resp -> {
            System.out.println("Got response " + resp.statusCode());
            resp.bodyHandler(body -> {
                if (resp.statusCode() == 200) {
                    try {
                        SohuOpenRtb.Response response = SohuOpenRtb.Response.parseFrom(body.getBytes());
                        System.out.println("all = " + response.toString());
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
        SohuOpenRtb.Request request = req.build();
        Buffer buffer = Buffer.buffer(request.toByteArray());
        post.end(buffer);
//      System.out.println(buffer.toString());
    }

    private static SohuOpenRtb.Request.Builder getReq2() {
        SohuOpenRtb.Request.Builder builder = SohuOpenRtb.Request.newBuilder();

        SohuOpenRtb.Request.Impression.Builder impression = SohuOpenRtb.Request.Impression.newBuilder();

        SohuOpenRtb.Request.Impression.Banner.Builder banner = SohuOpenRtb.Request.Impression.Banner.newBuilder();

        SohuOpenRtb.Request.Impression.Banner.Format.Builder format = SohuOpenRtb.Request.Impression.Banner.Format.newBuilder();

        SohuOpenRtb.Request.Device.Builder device = SohuOpenRtb.Request.Device.newBuilder();

        SohuOpenRtb.Request.Site.Builder site = SohuOpenRtb.Request.Site.newBuilder();

        SohuOpenRtb.Request.User.Builder user = SohuOpenRtb.Request.User.newBuilder();

        builder.setVersion(1);
        builder.setBidid("test-c53a74e482cb4a6fb2d141bfbbde8301");
        builder.setIsTest(0);

        site.setName("SOHU_NEWS_APP");
        site.setCategory(0);

        device.setType("Mobile");
        device.setIp("10.16.10.63");
        device.setUa("ae3ce4368df274c49916977a2afc13a6|eeb833daed0e09d8dd7788939aee4636|1fbbbf8dfb3204982d6cf8bf780119dd||Android4.3http://t.adrd.sohuno.com/adgtr/?adps=30000001");
        device.setNetType("WCDMA");
        device.setMobileType("AndroidPhone");
        device.setScreenWidth(300);
        device.setScreenHeight(250);
        device.setImei("ae3ce4368df274c49916977a2afc13a6");
        device.setImsi("eeb833daed0e09d8dd7788939aee4636");
        device.setMac("1fbbbf8dfb3204982d6cf8bf780119dd");
        device.setAndroidID("7b67b521b4ede3b699151b00a77d4628");

        user.setSuid("9703281533506065");
        user.setVersion(1);

        impression.setIdx(0);
        impression.setPid("12238");
        impression.setBidFloor(0);
        impression.setScreenLocation(SohuOpenRtb.Request.Impression.ScreenLocation.FIRSTVIEW);
        impression.setIsPreferredDeals(false);
        impression.setTradingType("RTB");

        banner.addMimes(1);
        banner.setWidth(640);
        banner.setHeight(960);
        banner.setTemplate("picturetxt");

        impression.setBanner(banner);

        builder.addImpression(impression);
        builder.setDevice(device);
        builder.setUser(user);
        builder.setSite(site);

        return builder;
    }

    private static SohuOpenRtb.Request.Builder getReq() {
        SohuOpenRtb.Request.Builder builder = SohuOpenRtb.Request.newBuilder();

        SohuOpenRtb.Request.Impression.Builder impression = SohuOpenRtb.Request.Impression.newBuilder();

        SohuOpenRtb.Request.Impression.Banner.Builder banner = SohuOpenRtb.Request.Impression.Banner.newBuilder();

        SohuOpenRtb.Request.Impression.Banner.Format.Builder format = SohuOpenRtb.Request.Impression.Banner.Format.newBuilder();

        SohuOpenRtb.Request.Device.Builder device = SohuOpenRtb.Request.Device.newBuilder();

        SohuOpenRtb.Request.Site.Builder site = SohuOpenRtb.Request.Site.newBuilder();

        SohuOpenRtb.Request.User.Builder user = SohuOpenRtb.Request.User.newBuilder();

//        bannerBuilder.setMimes()
        banner.setWidth(228);
        banner.setHeight(162);

        device.setAndroidID("androidid1231231");
        device.setIdfa("idfa1231231");
        device.setImei("imei121231");
        device.setIp("192.168.0.215");
        device.setMac("mac123131");
        device.setNetType("wifi");
        device.setOpenUDID("open12312312");
        device.setType("Mobile");


        user.setSuid("uid123123");

        site.setName("sohu");
        site.setPage("http://www.car.com");

        SohuOpenRtb.Request.Impression.Builder imp = impression.setBanner(banner);
        imp.setBidFloor(2);
        imp.setCampaignId("183");
        imp.setTradingType("rtb");
        imp.setLineId("1122");
        imp.setPid("90001");
        imp.addAcceptAdvertisingType("A0101");

        builder.setDevice(device);
        builder.addImpression(imp);
        builder.setUser(user);
        builder.setSite(site);

        builder.setBidid("bidid123123131")
                .setIsTest(0)
                .setVersion(11111);

        return builder;
    }
}
