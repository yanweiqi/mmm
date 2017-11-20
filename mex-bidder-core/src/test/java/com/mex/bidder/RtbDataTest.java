package com.mex.bidder;

import com.google.common.collect.Lists;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public class RtbDataTest {

    public static final OpenRtb.BidResponse bidResponse = OpenRtb.BidResponse.newBuilder()
            .setId("1-2-3-4-5")
            .addSeatbid(OpenRtb.BidResponse.SeatBid.newBuilder()
                    .addBid(OpenRtb.BidResponse.SeatBid.Bid.newBuilder()
                            .setId("1")
                            .setImpid("102")
                            .setPrice(9.43)
                            .setAdid("314")
                            .setNurl("http://adserver.com/winnotice?impid=102")
                            .setAdm("adm-1-90")
                            .addAdomain("advertiserdomain.com")
                            .setIurl("http://adserver.com/pathtosampleimage")
                            .setCid("campaign111")
                            .setCrid("creative112")
                            .setExtension(MexOpenRtbExt.landingpage, "mex.click.url")
                            .setExtension(MexOpenRtbExt.clktrackers, Lists.newArrayList("click.track1", "track2"))
                    )
                    .setSeat("512"))
            .setBidid("abc1123")
            .setCur("RMB").build();

    public static final OpenRtb.BidRequest bidRequest = OpenRtb.BidRequest.newBuilder()
            .setId("1234534625254")
            .setAt(OpenRtb.AuctionType.SECOND_PRICE)
            .setTmax(120)
            .addImp(OpenRtb.BidRequest.Imp.newBuilder()
                    .setId("1")
                    .setBanner(OpenRtb.BidRequest.Imp.Banner.newBuilder()
                            .setW(300)
                            .setH(250)
                            .setPos(OpenRtb.AdPosition.ABOVE_THE_FOLD)
                            .addBattr(OpenRtb.CreativeAttribute.USER_INTERACTIVE)))
            .addBadv("company1.com")
            .addBadv("company2.com")
            .setApp(OpenRtb.BidRequest.App.newBuilder())
            .setDevice(OpenRtb.BidRequest.Device.newBuilder()
                    .setIp("64.124.253.1")
                    .setUa("Mozilla/5.0")
                    .setOs("OS X")
                    .setExtension(MexOpenRtbExt.mac, "mac-123")
                    .setExtension(MexOpenRtbExt.imei, "imei-123")
                    .setExtension(MexOpenRtbExt.androidId, "androidId-456")
                    .setExtension(MexOpenRtbExt.idfa, "adid-11")
            )
            .setUser(OpenRtb.BidRequest.User.newBuilder()
                    .setId("45asdf987656789adfad4678rew656789")
                    .setBuyeruid("5df678asd8987656asdf78987654"))
            .build();
}
