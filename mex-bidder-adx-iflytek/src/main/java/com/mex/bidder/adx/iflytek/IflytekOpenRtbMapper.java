package com.mex.bidder.adx.iflytek;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.OpenRtbJsonFactory;
import com.google.openrtb.json.OpenRtbJsonReader;
import com.google.openrtb.json.OpenRtbJsonWriter;
import com.mex.bidder.api.mapper.OpenRtbMapper;
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
public class IflytekOpenRtbMapper implements OpenRtbMapper<String, String, OpenRtb.BidRequest.Builder, String> {
    private static final Logger logger = LoggerFactory.getLogger(IflytekOpenRtbMapper.class);

    private OpenRtbJsonWriter openRtbJsonWriter;
    private OpenRtbJsonReader openRtbJsonReader;

    public IflytekOpenRtbMapper() {
        OpenRtbJsonFactory factory = IflytekOpenRtbJsonFactory.create();
        openRtbJsonWriter = factory.newWriter();
        openRtbJsonReader = factory.newReader();
    }


    @Override
    public String toExchangeBidResponse(@Nullable OpenRtb.BidRequest request, OpenRtb.BidResponse response) {
        try {
            return openRtbJsonWriter.writeBidResponse(response);
        } catch (IOException e) {
            throw new ParseException("com.mex.bidder.adx.iflytek serialize bid response error.", e);
        }
    }

    @Override
    public OpenRtb.BidRequest.Builder toOpenRtbBidRequest(String request) {
        try {
            OpenRtb.BidRequest bidRequest = openRtbJsonReader.readBidRequest(request);
            return bidRequest.toBuilder();
        } catch (IOException e) {
            throw new ParseException("com.mex.bidder.adx.iflytek parse bid request json error.", e);
        }
    }
}
