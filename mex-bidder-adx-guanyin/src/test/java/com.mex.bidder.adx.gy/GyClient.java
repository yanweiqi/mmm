package com.mex.bidder.adx.gy;

import com.google.openrtb.OpenRtb;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mex.bidder.engine.util.MexUtil;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;

import java.io.File;
import java.io.FileOutputStream;

/*
 * User: donghai
 * Date: 2016/11/22
 */
public class GyClient {
//    private static final ExtensionRegistry registry = ExtensionRegistry.newInstance();

 /*   static {
        String data = JsonHelper.readFile("gy_req_data.json");
        JsonObject json = new JsonObject(data);

        GyOpenRtb.BidRequest.Builder builder = GyOpenRtb.BidRequest.newBuilder();
        builder.setId(json.getString("id"));

    }
*/


    static GyOpenRtb.BidRequest bidRequest = GyOpenRtb.BidRequest.newBuilder()
            .setId("1234534dsfsdf625254")
            .setAt(OpenRtb.AuctionType.SECOND_PRICE_VALUE)
            .setTmax(120)
            .addImp(GyOpenRtb.Imp.newBuilder()
                    .setId("1")
                    .setBidfloor(1.5f)
                    .setBanner(GyOpenRtb.Banner.newBuilder()
                            .setId("WHhMxAA7AkKsEQAPAHpV")
//                            .setW(640)
//                            .setH(100)
                            .setW(640)
                            .setH(960)

                            .setPos(GyOpenRtb.AdPosition.ABOVE_THE_FOLD)
                            .addBattr(GyOpenRtb.CreativeAttribute.ATTR_701)))
//                .addBadv("company1.com")
//                .addBadv("company2.com")

            .setApp(GyOpenRtb.App.newBuilder()
                    .setId("1")
                    .setBundle("sd")
                    .setPaid(11)
                    .setName("龙岩KK网")
                    .setPublisher(GyOpenRtb.Publisher.newBuilder()
                            .setId("1")
                            .setDomain("www.mex.com")))
//                        .setKeywords(2,"dsfd"))

            .setDevice(GyOpenRtb.Device.newBuilder()
//                    .setIp("64.124.253.1")
                    .setConnectiontype(GyOpenRtb.ConnectionType.WIFI)
                    .setIp("223.104.176.44")
                    .setUa("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16)")
                    .setOs("ios")
                    .setDevicetype(GyOpenRtb.DeviceType.MOBILE)
                    .setW(640)
                    .setH(100)
                    .setGeo(GyOpenRtb.Geo.newBuilder().setType(GyOpenRtb.LocationType.GPS))
//                        .setFlashver("10.1")
                    .setJs(1))
            .setUser(GyOpenRtb.User.newBuilder()
                    .setId("45asdf987656789adfad4678rew656789"))
//                        .setBuyeruid("5df678asd8987656asdf78987654"))
            .setTest(1)
            .setScenario(GyOpenRtb.Scenario.newBuilder().setType(GyOpenRtb.ScenarioType.APP))

            .build();


    public static void main2(String[] args) throws Exception {

        GyOpenRtb.BidRequest request = bidRequest.toBuilder().setId(MexUtil.uuid()).build();
        byte[] bytes = request.toByteArray();
        FileOutputStream fileOutputStream = new FileOutputStream(new File("728_90.bidrequest.error.data"));
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
        ;
    }

    public static void main(String[] args) {


//        GyOpenRtb.registerAllExtensions(registry);


        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions()
//                .setDefaultHost("123.56.14.96").setDefaultPort(80);
                .setDefaultHost("localhost").setDefaultPort(9080);
//        HttpClientOptions options = new HttpClientOptions()
//                .setDefaultHost("101.200.34.111").setDefaultPort(9901);
//        .setDefaultHost("101.200.34.111").setDefaultPort(9080);
        HttpClient client = vertx.createHttpClient(options);

        Runnable send1000 = () -> {
//            for (int i = 0; i < 5000; i++) {

            HttpClientRequest localhost = client.post("/adsgy", resp -> {
                System.out.println("Got response " + resp.statusCode());
                resp.bodyHandler(body -> {
                    if (resp.statusCode() == 200) {
                        try {
                            GyOpenRtb.BidResponse bidRes = GyOpenRtb.BidResponse.parseFrom(body.getBytes());
                            System.out.println("all = " + bidRes.toString());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("Got data " + body.toString("utf-8"));
                    }
                });

            });
//            GyOpenRtb.BidRequest request = bidRequest.toBuilder().setId(MexUtil.uuid()).build();
            GyOpenRtb.BidRequest request = bidRequest.toBuilder().build();
            localhost.end(Buffer.buffer(request.toByteArray()));
//            }
        };

        new Thread(send1000).start();
    }
}
