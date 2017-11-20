package com.mex.bidder.adx.adview;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.OpenRtbJsonFactory;
import com.google.openrtb.json.OpenRtbNativeJsonWriter;

import java.io.IOException;

/**
 * adview 原生响应
 * user: donghai
 * date: 2017/4/24
 */
public class AdviewOpenRtbNativeJsonWriter extends OpenRtbNativeJsonWriter {
    protected AdviewOpenRtbNativeJsonWriter(OpenRtbJsonFactory factory) {
        super(factory);
    }

    @Override
    protected void writeNativeResponseFields(OpenRtb.NativeResponse resp, JsonGenerator gen) throws IOException {
        if (resp.hasVer()) {
            gen.writeStringField("ver", resp.getVer());
        }
        if (resp.getAssetsCount() != 0) {
            gen.writeArrayFieldStart("assets");
            for (OpenRtb.NativeResponse.Asset asset : resp.getAssetsList()) {
                writeRespAsset(asset, gen);
            }
            gen.writeEndArray();
        }
    }
}
