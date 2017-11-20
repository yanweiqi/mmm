package com.mex.bidder.adx.meitu;

import com.google.openrtb.OpenRtb;
import com.meitu.openrtb.MeituOpenRtb;
import com.mex.bidder.engine.util.MexUtil;

/**
 * xuchuahao
 * on 2017/6/14.
 */
public class MeituConst {

    public static MeituOpenRtb.BidResponse.Builder emptyResponse(OpenRtb.BidRequest bidRequest, OpenRtb.BidResponse response) {
        String uuid = MexUtil.uuid();
        MeituOpenRtb.BidResponse.Builder  builder =  MeituOpenRtb.BidResponse.newBuilder()
                .setId(bidRequest.hasId() ? bidRequest.getId() : uuid)
                .setBidid("1")
                .setNbr(MeituOpenRtb.NoBidReason.INVALID_REQUEST)
                ;


        return builder;
    }
}
