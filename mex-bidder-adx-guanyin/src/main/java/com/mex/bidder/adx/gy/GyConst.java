package com.mex.bidder.adx.gy;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidResponse;
import com.mex.bidder.engine.util.MexUtil;


/**
 * User: donghai
 * Date: 2016/11/22
 */
public class GyConst {
    public static void main(String[] args) {

        BidResponse build = BidResponse.newBuilder().build();
        System.out.println(build.toByteArray());
    }

    public static GyOpenRtb.BidResponse.Builder emptyResponse(OpenRtb.BidRequest bidRequest,OpenRtb.BidResponse response) {

        String uuid = MexUtil.uuid();
        String impId;
        if (bidRequest.getImpCount() > 0 && bidRequest.getImp(0).hasId()) {
            impId = bidRequest.getImp(0).getId();
        } else {
            impId = uuid;
        }
//
//        GyOpenRtb.BidResponse.Builder  builder =  GyOpenRtb.BidResponse.Builder.
//                .setId(bidRequest.hasId() ? bidRequest.getId() : uuid)
//                .addSeatbid(SeatBid.newBuilder()
//                        .addBid(Bid.newBuilder()
//                                .setId(uuid)
//                                .setImpid(impId)
//                                .setPrice(0)));

        GyOpenRtb.BidResponse.Builder  builder =  GyOpenRtb.BidResponse.newBuilder()
                .setId(bidRequest.hasId() ? bidRequest.getId() : uuid)
//                .addSeatbid(GyOpenRtb.SeatBid.newBuilder().setSeat("sds"))
                .setBidid("1");

        return builder;
    }

}
