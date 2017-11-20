package com.mex.bidder.adx.zplay;

import com.fasterxml.jackson.core.JsonParser;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.OpenRtbJsonFactory;
import com.google.openrtb.json.OpenRtbNativeJsonReader;

import java.io.IOException;

import static com.google.openrtb.json.OpenRtbJsonUtils.*;

/**
 * user: donghai
 * date: 2017/4/25
 */
public class ZplayOpenRtbNativeJsonReader extends OpenRtbNativeJsonReader {
    protected ZplayOpenRtbNativeJsonReader(OpenRtbJsonFactory factory) {
        super(factory);
    }

    @Override
    protected void readNativeRequestField(JsonParser par, OpenRtb.NativeRequest.Builder req, String fieldName) throws IOException {
        super.readNativeRequestField(par, req, fieldName);
    }

    @Override
    protected void readReqAssetField(JsonParser par, OpenRtb.NativeRequest.Asset.Builder asset, String fieldName) throws IOException {
        if ("AssetOneof".equals(fieldName)) {
            readAssetOneofField(par, asset);
        } else {
            super.readReqAssetField(par, asset, fieldName);
        }
    }

    private void readAssetOneofField(JsonParser par, OpenRtb.NativeRequest.Asset.Builder asset) throws IOException {
        for (startObject(par); endObject(par); par.nextToken()) {
            String fieldName = getCurrentName(par);
            switch (fieldName) {
                case "Title":
                    asset.setTitle(readReqTitle(par));
                    break;
                case "Img":
                    asset.setImg(readReqImage(par));
                    break;
                case "Video":
                    asset.setVideo(coreReader().readVideo(par));
                case "Data":
                    asset.setData(readReqData(par));
                default:
                    ;
            }
        }


    }
}
