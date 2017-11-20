package com.mex.bidder.adx.adview;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.NativeResponse;
import com.google.openrtb.json.OpenRtbJsonUtils;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;

import java.io.IOException;
import java.util.List;

import static com.google.openrtb.json.OpenRtbJsonUtils.writeIntBoolField;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeStrings;

/**
 * user: donghai
 * date: 2017/6/2
 */
public class NativeObjectHelper {
    public static void writeNativeResponseFields(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen)
            throws IOException {
        gen.writeFieldName("native");
        gen.writeStartObject();
        NativeResponse resp = bid.getAdmNative();
        if (resp.hasVer()) {
            gen.writeStringField("ver", resp.getVer());
        }
        if (resp.getAssetsCount() != 0) {
            gen.writeArrayFieldStart("assets");
            for (NativeResponse.Asset asset : resp.getAssetsList()) {
                writeRespAsset(asset, gen);
            }
            gen.writeEndArray();
        }

        if (bid.hasExtension(MexOpenRtbExt.landingpage)) {
            gen.writeFieldName("link");
            gen.writeStartObject();
            writeLandingpage(bid,gen);
            if (bid.getExtensionCount(MexOpenRtbExt.clktrackers) > 0) {
                List<String> clktrackers = bid.getExtension(MexOpenRtbExt.clktrackers);
                OpenRtbJsonUtils.writeStrings("clicktrackers", clktrackers, gen);
            }
            gen.writeEndObject();
        }
        if (bid.getExtensionCount(MexOpenRtbExt.imptrackers) > 0) {
            List<String> imptrackerList = bid.getExtension(MexOpenRtbExt.imptrackers);
            OpenRtbJsonUtils.writeStrings("imptrackers", imptrackerList, gen);
        }
        gen.writeEndObject();
    }

    private static void writeLandingpage(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
        if (Strings.isNullOrEmpty(bid.getExtension(MexOpenRtbExt.deeplink))){
            gen.writeStringField("url", bid.getExtension(MexOpenRtbExt.landingpage));
        } else {
            gen.writeStringField("url",bid.getExtension(MexOpenRtbExt.deeplink));
            //替代落地页地址，如果设备无法访问 url 地址，使用本地址 （可以用作 deepLink 的备选 URL）
            gen.writeStringField("fallback",bid.getExtension(MexOpenRtbExt.landingpage));
        }
    }


    public static void writeRespAsset(NativeResponse.Asset asset, JsonGenerator gen)
            throws IOException {
        gen.writeStartObject();
        writeRespAssetFields(asset, gen);
        //writeExtensions(asset, gen);
        gen.writeEndObject();
    }

    static void writeRespAssetFields(NativeResponse.Asset asset, JsonGenerator gen)
            throws IOException {
        gen.writeNumberField("id", asset.getId());
        if (asset.hasRequired()) {
            writeIntBoolField("required", asset.getRequired(), gen);
        }
        if (asset.hasLink()) {
            gen.writeFieldName("link");
            writeRespLink(asset.getLink(), gen);
        }
        switch (asset.getAssetOneofCase()) {
            case TITLE:
                gen.writeFieldName("title");
                writeRespTitle(asset.getTitle(), gen);
                break;
            case IMG:
                gen.writeFieldName("img");
                writeRespImage(asset.getImg(), gen);
                break;
            case VIDEO:
                gen.writeFieldName("video");
                writeRespVideo(asset.getVideo(), gen);
                break;
            case DATA:
                gen.writeFieldName("data");
                writeRespData(asset.getData(), gen);
                break;
            case ASSETONEOF_NOT_SET:
                //checkRequired(false);
        }
    }

    public static void writeRespTitle(NativeResponse.Asset.Title title, JsonGenerator gen)
            throws IOException {
        gen.writeStartObject();
        writeRespTitleFields(title, gen);
        //writeExtensions(title, gen);
        gen.writeEndObject();
    }

    static void writeRespTitleFields(NativeResponse.Asset.Title title, JsonGenerator gen)
            throws IOException {
        gen.writeStringField("text", title.getText());
    }

    public static void writeRespImage(NativeResponse.Asset.Image image, JsonGenerator gen)
            throws IOException {
        gen.writeStartObject();
        writeRespImageFields(image, gen);
        //writeExtensions(image, gen);
        gen.writeEndObject();
    }

    static void writeRespImageFields(NativeResponse.Asset.Image image, JsonGenerator gen)
            throws IOException {
        gen.writeStringField("url", image.getUrl());
        if (image.hasW()) {
            gen.writeNumberField("w", image.getW());
        }
        if (image.hasH()) {
            gen.writeNumberField("h", image.getH());
        }
    }

    public static void writeRespVideo(NativeResponse.Asset.Video video, JsonGenerator gen)
            throws IOException {
        gen.writeStartObject();
        writeRespVideoFields(video, gen);
        //writeExtensions(video, gen);
        gen.writeEndObject();
    }

    static void writeRespVideoFields(NativeResponse.Asset.Video video, JsonGenerator gen)
            throws IOException {
        gen.writeStringField("vasttag", video.getVasttag());
    }

    public static void writeRespData(NativeResponse.Asset.Data data, JsonGenerator gen)
            throws IOException {
        gen.writeStartObject();
        writeRespDataFields(data, gen);
        //writeExtensions(data, gen);
        gen.writeEndObject();
    }

    static void writeRespDataFields(NativeResponse.Asset.Data data, JsonGenerator gen)
            throws IOException {
        if (data.hasLabel()) {
            gen.writeStringField("label", data.getLabel());
        }
        gen.writeStringField("value", data.getValue());
    }

    public static void writeRespLink(NativeResponse.Link link, JsonGenerator gen)
            throws IOException {
        gen.writeStartObject();
        writeRespLinkFields(link, gen);
        //writeExtensions(link, gen);
        gen.writeEndObject();
    }

    static void writeRespLinkFields(NativeResponse.Link link, JsonGenerator gen)
            throws IOException {
        if (link.hasUrl()) {
            gen.writeStringField("url", link.getUrl());
        }
        writeStrings("clicktrackers", link.getClicktrackersList(), gen);
        if (link.hasFallback()) {
            gen.writeStringField("fallback", link.getFallback());
        }
    }
}
