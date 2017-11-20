package com.mex.bidder.adx.sohu;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.util.MexUtil;

/**
 * xuchuahao
 * on 2017/3/27.
 */
public class SohuConst {

    public static SohuOpenRtb.Response.Builder emptyResponse(OpenRtb.BidRequest bidRequest, OpenRtb.BidResponse response) {

        String uuid = MexUtil.uuid();
        String impId;
        if (bidRequest.getImpCount() > 0 && bidRequest.getImp(0).hasId()) {
            impId = bidRequest.getImp(0).getId();
        } else {
            impId = uuid;
        }
        SohuOpenRtb.Response.Builder builder = SohuOpenRtb.Response.newBuilder()
                .setBidid(bidRequest.hasId() ? bidRequest.getId() : uuid)
                .setVersion(bidRequest.getExtension(MexOpenRtbExt.version));

        return builder;
    }
}
