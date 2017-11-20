package com.mex.bidder.adx.baidu;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.mex.bidder.engine.util.MexUtil;


/**
 * User: donghai
 * Date: 2016/11/22
 */
public class BaiduConst {
    public static void main(String[] args) {

        BidResponse build = BidResponse.newBuilder().build();
        System.out.println(build.toByteArray());
    }

    public static BidResponse.Builder emptyResponse(OpenRtb.BidRequest bidRequest) {

        String uuid = MexUtil.uuid();
        String impId;
        if (bidRequest.getImpCount() > 0 && bidRequest.getImp(0).hasId()) {
            impId = bidRequest.getImp(0).getId();
        } else {
            impId = uuid;
        }

        BidResponse.Builder builder = BidResponse.newBuilder()
                .setId(bidRequest.hasId() ? bidRequest.getId() : uuid)
                .addSeatbid(SeatBid.newBuilder()
                        .addBid(Bid.newBuilder()
                                .setId(uuid)
                                .setImpid(impId)
                                .setPrice(0)));
        return builder;
    }

}
