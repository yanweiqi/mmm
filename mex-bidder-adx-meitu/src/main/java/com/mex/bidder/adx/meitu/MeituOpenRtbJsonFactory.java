package com.mex.bidder.adx.meitu;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.SetMultimap;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.*;
import com.mex.bidder.constants.TelecomOperator;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

/**
 * xuchuahao
 * on 2017/6/12.
 */
public class MeituOpenRtbJsonFactory extends OpenRtbJsonFactory {

    // 出价, 单位: 分(人民币)/千次展示
    private static final int CURRENCY_UNIT = 100;


    protected MeituOpenRtbJsonFactory(@Nullable JsonFactory jsonFactory, boolean strict, boolean rootNativeField, boolean forceNativeAsObject, @Nullable SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders, @Nullable Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters) {
        super(jsonFactory, strict, rootNativeField, forceNativeAsObject, extReaders, extWriters);
    }

    public static MeituOpenRtbJsonFactory create(){
        return new MeituOpenRtbJsonFactory(null, false, true, false, null, null);
    }

    @Override
    public OpenRtbJsonReader newReader() {
        return super.newReader();
    }

    @Override
    public OpenRtbJsonWriter newWriter() {
        return super.newWriter();
    }

    static class MyOpenRtbJsonReader extends OpenRtbJsonReader {

        protected MyOpenRtbJsonReader(OpenRtbJsonFactory factory) {
            super(factory);
        }

        @Override
        protected void readImpField(JsonParser par, OpenRtb.BidRequest.Imp.Builder imp, String fieldName) throws IOException {
            if ("bidfloor".equals(fieldName)) {
                imp.setBidfloor(par.getValueAsDouble() / CURRENCY_UNIT);
            }
            super.readImpField(par, imp, fieldName);
        }

        @Override
        protected void readDeviceField(JsonParser par, OpenRtb.BidRequest.Device.Builder device, String fieldName) throws IOException {
            if ("carrier".equals(fieldName)){
                TelecomOperator telecomOperator = MeituCarrierTypeMapper.mapper.toMex(par.getValueAsString());
                device.setCarrier(telecomOperator.getValue());
            }
            super.readDeviceField(par, device, fieldName);
        }
    }

    static class MyOpenRtbJsonWriter extends OpenRtbJsonWriter {

        protected MyOpenRtbJsonWriter(OpenRtbJsonFactory factory) {
            super(factory);
        }


    }


}
