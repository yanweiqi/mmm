package com.mex.bidder.api.openrtb.json;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.SetMultimap;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.*;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.logger.impl.LogUtil;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public class MexOpenRtbJsonFactory extends OpenRtbJsonFactory {
    protected MexOpenRtbJsonFactory(
            @Nullable JsonFactory jsonFactory,
            boolean strict,
            boolean rootNativeField,
            boolean forceNativeAsObject,
            @Nullable SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders,
            @Nullable Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters) {
        super(jsonFactory, strict, rootNativeField, forceNativeAsObject, extReaders, extWriters);
    }

    protected MexOpenRtbJsonFactory(MexOpenRtbJsonFactory config) {
        super(config);
    }

    public static MexOpenRtbJsonFactory create() {
        return new MexOpenRtbJsonFactory(null, false, true, true, null, null);
    }

    @Override
    public OpenRtbJsonReader newReader() {
        return new MyOpenRtbJsonReader(new MexOpenRtbJsonFactory(this));
    }

    @Override
    public OpenRtbJsonWriter newWriter() {
        return new MyOpenRtbJsonWriter(new MexOpenRtbJsonFactory(this));
    }

    static class MyOpenRtbJsonReader extends OpenRtbJsonReader {

        public MyOpenRtbJsonReader(MexOpenRtbJsonFactory factory) {
            super(factory);
        }


        @Override
        protected void readDeviceField(
                JsonParser par, OpenRtb.BidRequest.Device.Builder device, String fieldName)
                throws IOException {

            super.readDeviceField(par, device, fieldName);
        }
    }

    static class MyOpenRtbJsonWriter extends OpenRtbJsonWriter {
        public MyOpenRtbJsonWriter(MexOpenRtbJsonFactory factory) {
            super(factory);
        }


        @Override
        protected void writeBidRequestFields(OpenRtb.BidRequest req, JsonGenerator gen) throws IOException {
            super.writeBidRequestFields(req, gen);
            gen.writeStringField("netname", req.getExtension(MexOpenRtbExt.reqNetname));
            gen.writeStringField("logtime", DateTime.now().toString(LogUtil.DATE_FORMAT));
            gen.writeStringField("logtype", "request");
        }

        @Override
        protected void writeDeviceFields(OpenRtb.BidRequest.Device device, JsonGenerator gen) throws IOException {

            super.writeDeviceFields(device, gen);

            if (device.hasExtension(MexOpenRtbExt.imei)) {
                gen.writeStringField("imei", device.getExtension(MexOpenRtbExt.imei));
            }
            if (device.hasExtension(MexOpenRtbExt.mac)) {
                gen.writeStringField("mac", device.getExtension(MexOpenRtbExt.mac));
            }
            if (device.hasExtension(MexOpenRtbExt.androidId)) {
                gen.writeStringField("androidId", device.getExtension(MexOpenRtbExt.androidId));
            }

            if (device.hasExtension(MexOpenRtbExt.idfa)) {
                gen.writeStringField("idfa", device.getExtension(MexOpenRtbExt.idfa));
            }

        }


        @Override
        protected void writeBidResponseFields(OpenRtb.BidResponse resp, JsonGenerator gen) throws IOException {
            super.writeBidResponseFields(resp, gen);
            gen.writeStringField("netname", resp.getExtension(MexOpenRtbExt.resNetname));
            gen.writeStringField("logtime", DateTime.now().toString(LogUtil.DATE_FORMAT));
            gen.writeStringField("logtype", "response");

            if (resp.getExtensionCount(MexOpenRtbExt.debugInfo) != 0) {
                gen.writeFieldName("nbr");
                gen.writeStartObject();
                for (MexOpenRtbExt.MexNbrField mexNbrField : resp.getExtension(MexOpenRtbExt.debugInfo)) {
                    gen.writeStringField(mexNbrField.getCode(), mexNbrField.getNbr());
                }
                gen.writeEndObject();
            }
        }

        @Override
        protected void writeBidFields(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
            super.writeBidFields(bid, gen);

            gen.writeNumberField("adgroupid", bid.getExtension(MexOpenRtbExt.adgroupid));
            gen.writeNumberField("adverid", bid.getExtension(MexOpenRtbExt.adverid));
            gen.writeStringField("ip",bid.getExtension(MexOpenRtbExt.ip));
            gen.writeNumberField("productid",bid.getExtension(MexOpenRtbExt.productid));

            List<String> imptrackerList = bid.getExtension(MexOpenRtbExt.imptrackers);
            OpenRtbJsonUtils.writeStrings("imptrackers", imptrackerList, gen);

            List<String> clktrackers = bid.getExtension(MexOpenRtbExt.clktrackers);
            OpenRtbJsonUtils.writeStrings("clktrackers", clktrackers, gen);


            if (bid.hasExtension(MexOpenRtbExt.landingpage)) {
                gen.writeStringField("clkurl", bid.getExtension(MexOpenRtbExt.landingpage));
            }
        }
    }
}