package com.mex.bidder.adx.sohu;

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
 * xuchuahao
 * on 2017/3/29.
 */
public class SohuOpenRtbJsonFactory extends OpenRtbJsonFactory {
    protected SohuOpenRtbJsonFactory(@Nullable JsonFactory jsonFactory,
                                     boolean strict,
                                     boolean rootNativeField,
                                     boolean forceNativeAsObject,
                                     @Nullable SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders,
                                     @Nullable Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters) {
        super(jsonFactory, strict, rootNativeField, forceNativeAsObject, extReaders, extWriters);
    }

    public static SohuOpenRtbJsonFactory create() {
        return new SohuOpenRtbJsonFactory(null, false, true, false, null, null);
    }

    protected SohuOpenRtbJsonFactory(SohuOpenRtbJsonFactory config) {
        super(config);
    }

    @Override
    public OpenRtbJsonReader newReader() {
        return new SohuOpenRtbJsonFactory.MyOpenRtbJsonReader(new SohuOpenRtbJsonFactory(this));
    }

    @Override
    public OpenRtbJsonWriter newWriter() {
        return new SohuOpenRtbJsonFactory.MyOpenRtbJsonWriter(new SohuOpenRtbJsonFactory(this));
    }

    static class MyOpenRtbJsonReader extends OpenRtbJsonReader {

        protected MyOpenRtbJsonReader(OpenRtbJsonFactory factory) {

            super(factory);
        }

        @Override
        protected void readBidRequestField(JsonParser par, OpenRtb.BidRequest.Builder req, String fieldName) throws IOException {
            super.readBidRequestField(par, req, fieldName);
            req.setExtension(MexOpenRtbExt.reqNetname, SohuExchange.ID);
        }
    }

    static class MyOpenRtbJsonWriter extends OpenRtbJsonWriter {
        protected MyOpenRtbJsonWriter(OpenRtbJsonFactory factory) {
            super(factory);
        }

        @Override
        protected void writeBidRequestFields(OpenRtb.BidRequest req, JsonGenerator gen) throws IOException {

            String id = req.getId();

            super.writeBidRequestFields(req, gen);
        }
    }


}
