package com.mex.bidder.adx.zplay;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidResponse;
import org.junit.Test;

/**
 * User: donghai
 * Date: 2016/11/20
 */
public class ZplayRequestReceiverTest {
    @Test
    public void receive() throws Exception {

    }

    @Test
    public void newResponse() throws Exception {

    }

    private OpenRtb.BidResponse ZplayResponse(BidResponse bidResponse) {
        OpenRtb.BidResponse.Builder builder = OpenRtb.BidResponse.newBuilder()
                .setId(bidResponse.openRtb().getId())
                .addSeatbid(OpenRtb.BidResponse.SeatBid.newBuilder()
                        .addBid(OpenRtb.BidResponse.SeatBid.Bid.newBuilder()
                                .setId("1")
                                .setImpid("102")
                                .setPrice(9.43)
                                .setAdid("314")
                                .setNurl("http://adserver.com/winnotice?impid=102")
                                .setAdm(
                                        "%3C!DOCTYPE%20html%20PUBLIC%20%5C%22-"
                                                + "%2F%2FW3C%2F%2FDTD%20XHTML%201.0%20Transitional%2F%2FEN%5C%22%20%5C%22htt"
                                                + "p%3A%2F%2Fwww.w3.org%2FTR%2Fxhtml1%2FDTD%2Fxhtml1-"
                                                + "transitional.dtd%5C%22%3E%3Chtml%20xmlns%3D%5C%22http%3A%2F%2Fwww.w3.org%2F1"
                                                + "999%2Fxhtml%5C%22%20xml%3Alang%3D%5C%22en%5C%22%20lang%3D%5C%22en%5C%22"
                                                + "%3E...%3C%2Fhtml%3E")
                                .addAdomain("advertiserdomain.com")
                                .setIurl("http://adserver.com/pathtosampleimage")
                                .setCid("campaign111")
                                .setCrid("creative112"))
                        .setSeat("512"))
                .setBidid("abc1123")
                .setCur("RMB");
        return builder.build();
    }


}