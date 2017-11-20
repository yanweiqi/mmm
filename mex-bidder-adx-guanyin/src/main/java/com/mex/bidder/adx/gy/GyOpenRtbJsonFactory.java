package com.mex.bidder.adx.gy;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.SetMultimap;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.*;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

/**
 * xuchuanao
 * on 2017/3/2.
 */
public class GyOpenRtbJsonFactory extends OpenRtbJsonFactory {

    protected GyOpenRtbJsonFactory(@Nullable JsonFactory jsonFactory,
                                   boolean strict, boolean rootNativeField,
                                   boolean forceNativeAsObject,
                                   @Nullable SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders,
                                   @Nullable Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters) {
        super(jsonFactory, strict, rootNativeField, forceNativeAsObject, extReaders, extWriters);
    }

    protected GyOpenRtbJsonFactory(GyOpenRtbJsonFactory config) {
        super(config);
    }

    public static GyOpenRtbJsonFactory create() {
        return new GyOpenRtbJsonFactory(null, false, true, false, null, null);
    }


    @Override
    public OpenRtbJsonReader newReader() {
        return new GyOpenRtbJsonFactory.MyOpenRtbJsonReader(new GyOpenRtbJsonFactory(this));
    }

    @Override
    public OpenRtbJsonWriter newWriter() {
        return new GyOpenRtbJsonFactory.MyOpenRtbJsonWriter(new GyOpenRtbJsonFactory(this));
    }

    static class MyOpenRtbJsonReader extends OpenRtbJsonReader {

        @Override
        protected void readBidRequestField(JsonParser par, OpenRtb.BidRequest.Builder req, String fieldName) throws IOException {
            super.readBidRequestField(par, req, fieldName);
            req.setExtension(MexOpenRtbExt.reqNetname, GyExchange.ID);
        }

        protected MyOpenRtbJsonReader(OpenRtbJsonFactory factory) {
            super(factory);
        }
    }

    static class MyOpenRtbJsonWriter extends OpenRtbJsonWriter {



        protected MyOpenRtbJsonWriter(OpenRtbJsonFactory factory) {
            super(factory);
        }

        @Override
        protected void writeBidResponseFields(OpenRtb.BidResponse resp, JsonGenerator gen) throws IOException {
            gen.writeStringField("id", resp.getId());
            if (resp.getSeatbidCount() != 0) {
                gen.writeArrayFieldStart("seatbid");
                for (OpenRtb.BidResponse.SeatBid seatbid : resp.getSeatbidList()) {
                    writeSeatBid(seatbid, gen);
                }
                gen.writeEndArray();
            }
            if (resp.hasBidid()) {
                gen.writeStringField("bidid", resp.getBidid());
            }
            if (resp.hasCur()) {
                gen.writeStringField("cur", resp.getCur());
            }
            if (resp.hasCustomdata()) {
                gen.writeStringField("customdata", resp.getCustomdata());
            }
            if (resp.hasNbr()) {
                gen.writeNumberField("nbr", resp.getNbr().getNumber());
            }

        }

        @Override
        protected void writeBidFields(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {

            super.writeBidFields(bid, gen);

        }
    }
}
