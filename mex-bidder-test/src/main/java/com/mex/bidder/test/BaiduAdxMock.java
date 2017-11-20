package com.mex.bidder.test;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mex.bidder.adx.baidu.BaiduRtb;
import com.mex.bidder.engine.util.MexUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;

/**
 * Created by Administrator on 2016/12/27.
 */
public class BaiduAdxMock extends AdxMock {

    static
        BaiduRtb.BidRequest baiduBidRequest=BaiduRtb.BidRequest.newBuilder()
                .setId("12312312312312")
                .setUserGeoInfo(BaiduRtb.BidRequest.Geo.newBuilder()
                        .addUserCoordinate(BaiduRtb.BidRequest.Geo.Coordinate.newBuilder()
                                .setLatitude(116.359882f)
                                .setLongitude(39.890268f)))
                .addAdslot(BaiduRtb.BidRequest.AdSlot.newBuilder().setSequenceId(1)
                        .setWidth(640)
                        .setHeight(100)
                        .addCreativeType(1)  //允许的创意类型   1 图片2 FLASH 3 Video
                        .setMinimumCpm(6000) //mininum_cpm：发布商设置的底价，单位分。
                        .setAdslotType(0)) //adslot_type：展示类型
                .setMobile(BaiduRtb.BidRequest.Mobile.newBuilder()
                        .setDeviceType(BaiduRtb.BidRequest.Mobile.MobileDeviceType.HIGHEND_PHONE)
                        .setPlatform(BaiduRtb.BidRequest.Mobile.OS.valueOf(2))
                        .setWirelessNetworkType(BaiduRtb.BidRequest.Mobile.WirelessNetworkType.MOBILE_4G)
                        .setScreenWidth(300)
                        .setScreenHeight(250)
                        .build())
                .setIp("1.0.2.1")
                .build();

    public static void main(String[] args) throws Exception {
        BaiduAdxMock baiduAdxMock = new BaiduAdxMock();
        baiduAdxMock.run();
    }

    @Override
    protected void run()  {
        Runnable send1000 = () -> {
//            for (int i = 0; i < 5000; i++) {

                HttpClientRequest localhost = client.post("/adsmbd", resp -> {
                    System.out.println("Got response " + resp.statusCode());
                    resp.bodyHandler(body -> {
                        if (resp.statusCode() == 200) {
                            try {
                                BaiduRtb.BidResponse bidRes = BaiduRtb.BidResponse.parseFrom(body.getBytes());
                                System.out.println("responseId = " + bidRes.getId());
                                System.out.println("Bidid = " + bidRes.getAd(0).getSequenceId());
                                System.out.println("bid ====== " + bidRes.toString());
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("Got data " + body.toString("utf-8"));
                        }
                    });
                });
                BaiduRtb.BidRequest request = baiduBidRequest.toBuilder().setId(MexUtil.uuid()).build();
                localhost.end(Buffer.buffer(request.toByteArray()));
//            }
        };
        new Thread(send1000).start();
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
