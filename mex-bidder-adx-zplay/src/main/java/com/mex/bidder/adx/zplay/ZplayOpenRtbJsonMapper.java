package com.mex.bidder.adx.zplay;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.OpenRtbJsonFactory;
import com.google.openrtb.json.OpenRtbJsonReader;
import com.google.openrtb.json.OpenRtbJsonWriter;
import com.mex.bidder.api.mapper.OpenRtbMapper;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * User: donghai
 * Date: 2016/11/22
 */
@Singleton
public class ZplayOpenRtbJsonMapper implements OpenRtbMapper<String, String, String, String> {
    private static final Logger logger = LoggerFactory.getLogger(ZplayOpenRtbJsonMapper.class);

    private OpenRtbJsonWriter openRtbJsonWriter;
    private OpenRtbJsonReader openRtbJsonReader;

    public ZplayOpenRtbJsonMapper() {
        OpenRtbJsonFactory factory = ZplayOpenRtbJsonFactory.create();
        // 删除ExtWriter，直接在Factory中写出
//        factory.register(new ZplayResponseExtWriter.ImpTracker(), String.class, Bid.class, "imptrackers");
//        factory.register(new ZplayResponseExtWriter.ClickTracker(), String.class, Bid.class, "clktrackers");
//        factory.register(new ZplayResponseExtWriter.CURL(), String.class, Bid.class, "landingpage");

        openRtbJsonWriter = factory.newWriter();
        openRtbJsonReader = factory.newReader();
    }


    @Override
    public String toExchangeBidResponse(@Nullable OpenRtb.BidRequest request, OpenRtb.BidResponse response) {
        try {
            return openRtbJsonWriter.writeBidResponse(response);
        } catch (IOException e) {
            throw new ParseException("zplay serialize bid response error.", e);
        }
    }

    @Override
    public OpenRtb.BidRequest.Builder toOpenRtbBidRequest(String request) {
        try {
            OpenRtb.BidRequest bidRequest = openRtbJsonReader.readBidRequest(request);
            OpenRtb.BidRequest.Builder builder = bidRequest.toBuilder().setExtension(MexOpenRtbExt.reqNetname, ZplayExchange.ID);
//            return bidRequest.toBuilder();
            return builder;
        } catch (IOException e) {
            throw new ParseException("zplay parse bid request json error. [json] = " + request, e);
        }
    }


}
