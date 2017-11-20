package com.mex.bidder.adx.meitu;

import com.google.protobuf.InvalidProtocolBufferException;
import com.meitu.openrtb.MeituOpenRtb;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;

/**
 * xuchuahao
 * on 2017/6/12.
 */
public class MeituClient {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost("localhost").setDefaultPort(9080);
//                .setDefaultHost("testdspbidder.ad-mex.com").setDefaultPort(80);

        HttpClient client = vertx.createHttpClient(options);

        Runnable send1000 = () -> {
            HttpClientRequest localhost = client.post("/adsmeitu", resp -> {
                System.out.println("Got response " + resp.statusCode());
                resp.bodyHandler(body -> {
                    if (resp.statusCode() == 200) {
                        try {
                            MeituOpenRtb.BidResponse bidRes = MeituOpenRtb.BidResponse.parseFrom(body.getBytes());
                            System.out.println("all = " + bidRes.toString());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("Got data " + body.toString("utf-8"));
                    }
                });

            });
//            MeituOpenRtb.BidRequest request = bidRequest.toBuilder().setId(MexUtil.uuid()).build();
            MeituOpenRtb.BidRequest request = bidRequestNative.toBuilder().build();
            localhost.end(Buffer.buffer(request.toByteArray()));
        };

        new Thread(send1000).start();
    }

    static MeituOpenRtb.BidRequest bidBannerRequest = MeituOpenRtb.BidRequest.newBuilder()
            .setId("meitu-test-123")
            .setAt(MeituOpenRtb.AuctionType.SECOND_PRICE)
            .setTmax(100)
            .addWseat("white1").addWseat("white2")
            .setAllimps(true)
            .addCur("RMB")
            .addBcat(MeituOpenRtb.ContentCategory.CATE001).addBcat(MeituOpenRtb.ContentCategory.CATE002)
            .addBapp("com.popstar").addBapp("com.game")
            .setTest(true)
            .addImp(MeituOpenRtb.BidRequest.Imp.newBuilder()
                    .setId("imp123")
                    .setDisplaymanager("1.0")
                    .setInstl(true)
                    .setTagid("ad123")
                    .setBidfloor(2.00d)
                    .setBidfloorcur("CNY")
                    .setClickbrowser(true)
                    .setSecure(false)
                    .setBanner(MeituOpenRtb.BidRequest.Imp.Banner.newBuilder()
                            .setW(640)
                            .setH(100)
                            .setId("banner1231231")
                            .setPos(MeituOpenRtb.AdPosition.ABOVE_THE_FOLD)
                            .addBattr(MeituOpenRtb.CreativeAttribute.AD_CAN_BE_SKIPPED)
                            .addMimes("image/jpg").addMimes("image/gif")
                            .addFormat(MeituOpenRtb.BidRequest.Imp.Banner.Format.newBuilder().setW(320).setH(50))
                    )

            )
            .setApp(MeituOpenRtb.BidRequest.App.newBuilder()
                    .setId("app123")
                    .setName("xiaomi")
                    .setDomain("xiaomi.com")
                    .addCat(MeituOpenRtb.ContentCategory.CATE001)
                    .addSectioncat(MeituOpenRtb.ContentCategory.CATE002)
                    .addPagecat(MeituOpenRtb.ContentCategory.CATE003)
                    .setVer("1.0")
                    .setBundle("1231l2j3klj")
                    .setPaid(true)

            )
            .setDevice(MeituOpenRtb.BidRequest.Device.newBuilder()
                    .setDnt(true)
                    .setUa("ua")
                    .setIp("172.24.12.33")
                    .setGeo(MeituOpenRtb.BidRequest.Geo.newBuilder()
                            .setLat(11.23323d)
                            .setLon(44.21312)
                            .setCountry("china")
                            .setRegion("ll")
                    )
                    .setDidsha1("imeisha1123123")
                    .setDidmd5("imeimd5123123")
                    .setDpidsha1("androididsha1123123")
                    .setDpidmd5("androididmd51231231")
                    .setIpv6("ipv6lkfdld")
                    .setCarrier("")
                    .setLanguage("zh")
                    .setMake("apple")
                    .setModel("iphone")
                    .setOs("ios")
                    .setOsv("10.11")
                    .setHwv("iPhone 5s")
                    .setW(1024)
                    .setH(1000)
                    .setPpi(5000)
                    .setJs(true)
                    .setDevicetype(MeituOpenRtb.DeviceType.MOBILE)
                    .setMacsha1("macsha1qkejqlj")
                    .setMacmd5("macmd512312")


            )
            .setUser(MeituOpenRtb.BidRequest.User.newBuilder()
                    .setId("userid123123")
                    .setYob(19901203)
                    .setGender("M")

            ).build();


    static MeituOpenRtb.BidRequest bidRequestNative = MeituOpenRtb.BidRequest.newBuilder()
            .setId("meitu-test-123")
            .setAt(MeituOpenRtb.AuctionType.SECOND_PRICE)
            .setTmax(100)
            .addWseat("white1").addWseat("white2")
            .setAllimps(true)
            .addCur("RMB")
            .addBcat(MeituOpenRtb.ContentCategory.CATE001).addBcat(MeituOpenRtb.ContentCategory.CATE002)
            .addBapp("com.popstar").addBapp("com.game")
            .setTest(true)
            .addImp(MeituOpenRtb.BidRequest.Imp.newBuilder()
                    .setId("imp123")
                    .setDisplaymanager("1.0")
                    .setInstl(true)
                    .setTagid("ad123")
                    .setBidfloor(2.00d)
                    .setBidfloorcur("CNY")
                    .setClickbrowser(true)
                    .setSecure(false)
                    .setNative(MeituOpenRtb.BidRequest.Imp.Native.newBuilder()
                            .setRequestNative(MeituOpenRtb.NativeRequest.newBuilder()
                                    .addAssets(MeituOpenRtb.NativeRequest.Asset.newBuilder()
                                            .setId(1)
                                            .setImg(MeituOpenRtb.NativeRequest.Asset.Image.newBuilder()
                                                    .setW(710)
                                                    .setH(360)
                                                    .setType(MeituOpenRtb.ImageAssetType.MAIN)))
                                    .addAssets(MeituOpenRtb.NativeRequest.Asset.newBuilder()
                                            .setId(2)
                                            .setImg(MeituOpenRtb.NativeRequest.Asset.Image.newBuilder()
                                                    .setW(100)
                                                    .setH(100)
                                                    .setType(MeituOpenRtb.ImageAssetType.ICON)))
                                    .addAssets(MeituOpenRtb.NativeRequest.Asset.newBuilder()
                                            .setId(3)
                                            .setTitle(MeituOpenRtb.NativeRequest.Asset.Title.newBuilder()
                                                    .setLen(30)))
                                    .addAssets(MeituOpenRtb.NativeRequest.Asset.newBuilder()
                                            .setId(4)
                                            .setData(MeituOpenRtb.NativeRequest.Asset.Data.newBuilder()
                                                    .setLen(100)
                                                    .setType(MeituOpenRtb.DataAssetType.DESC)))
                                    .addAssets(MeituOpenRtb.NativeRequest.Asset.newBuilder()
                                            .setId(5)
                                            .setData(MeituOpenRtb.NativeRequest.Asset.Data.newBuilder()
                                                    .setLen(10)
                                                    .setType(MeituOpenRtb.DataAssetType.CTATEXT)))
                            )
                    )
            )
            .setApp(MeituOpenRtb.BidRequest.App.newBuilder()
                    .setId("app123")
                    .setName("xiaomi")
                    .setDomain("xiaomi.com")
                    .addCat(MeituOpenRtb.ContentCategory.CATE001)
                    .addSectioncat(MeituOpenRtb.ContentCategory.CATE002)
                    .addPagecat(MeituOpenRtb.ContentCategory.CATE003)
                    .setVer("1.0")
                    .setBundle("1231l2j3klj")
                    .setPaid(true)

            )
            .setDevice(MeituOpenRtb.BidRequest.Device.newBuilder()
                    .setDnt(true)
                    .setUa("ua")
                    .setIp("172.24.12.33")
                    .setGeo(MeituOpenRtb.BidRequest.Geo.newBuilder()
                            .setLat(11.23323d)
                            .setLon(44.21312)
                            .setCountry("china")
                            .setRegion("ll")
                    )
                    .setDidsha1("imeisha1123123")
                    .setDidmd5("imeimd5123123")
                    .setDpidsha1("androididsha1123123")
                    .setDpidmd5("androididmd51231231")
                    .setIpv6("ipv6lkfdld")
                    .setCarrier("")
                    .setLanguage("zh")
                    .setMake("apple")
                    .setModel("iphone")
                    .setOs("ios")
                    .setOsv("10.11")
                    .setHwv("iPhone 5s")
                    .setW(1024)
                    .setH(1000)
                    .setPpi(5000)
                    .setJs(true)
                    //.setDevicetype(MeituOpenRtb.DeviceType.MOBILE)
                    .setMacsha1("macsha1qkejqlj")
                    .setMacmd5("macmd512312")
                    .setConnectiontype(MeituOpenRtb.ConnectionType.WIFI)


            )
            .setUser(MeituOpenRtb.BidRequest.User.newBuilder()
                    .setId("userid123123")
                    .setYob(19901203)
                    .setGender("M")

            ).build();


}
