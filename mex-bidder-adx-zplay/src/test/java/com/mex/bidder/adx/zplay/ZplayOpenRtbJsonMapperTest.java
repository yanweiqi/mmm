package com.mex.bidder.adx.zplay;

import com.google.common.collect.Lists;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.util.JsonHelper;
import org.junit.Test;

/**
 * User: donghai
 * Date: 2016/11/28
 */
public class ZplayOpenRtbJsonMapperTest {
    ZplayOpenRtbJsonMapper mapper = new ZplayOpenRtbJsonMapper();

    @Test
    public void toExchangeBidResponse() throws Exception {
        OpenRtb.BidResponse res = OpenRtb.BidResponse.newBuilder()
                .setId("f0ec439aac8e9eb5a4c151aba5b18ebb")
                .addSeatbid(OpenRtb.BidResponse.SeatBid.newBuilder()
                        .setSeat("359")
                        .addBid(OpenRtb.BidResponse.SeatBid.Bid.newBuilder()
                                .setId("1")
                                .setImpid("5cdef32a55397c48b8baeb3cee0c5b5c")
                                .setPrice(1.2)
                                .setAdid("1326")
                                .setNurl("http://dsp.example.com/winnotice?price=%%WIN_PRICE%%")
                                .setAdm("<meta http-equiv='Content-Type'>")
                                .addAdomain("advertiserdomain.com")
                                .setCid("campaign111")
                                .setCrid("2376")
                                .setH(50)
                                .setW(320)
                                .setExtension(MexOpenRtbExt.landingpage, "click.url.com")
                                .setExtension(MexOpenRtbExt.clktrackers, Lists.newArrayList("http://dsp1.com/adclick?id=123398923"))
                                .setExtension(MexOpenRtbExt.imptrackers,
                                        Lists.newArrayList("url1", "url2"))
                        )

                )
                .build();


        System.out.println(mapper.toExchangeBidResponse(null, res));
    }

    @Test
    public void toOpenRtbBidRequest() throws Exception {
        String req = JsonHelper.readFile("zplay.req.json");
        OpenRtb.BidRequest bidRequest = mapper.toOpenRtbBidRequest(req).build();

        System.out.println(bidRequest.getId());
        System.out.println(bidRequest.getExtension(MexOpenRtbExt.isPing));
        System.out.println(bidRequest.getExtension(MexOpenRtbExt.version));
        System.out.println("imei-" + bidRequest.getDevice().getExtension(MexOpenRtbExt.imei));
        System.out.println("mac-" + bidRequest.getDevice().getExtension(MexOpenRtbExt.mac));
        System.out.println("androidId-" + bidRequest.getDevice().getExtension(MexOpenRtbExt.androidId));


    }

}