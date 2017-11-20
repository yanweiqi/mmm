package com.mex.bidder.adx.zplay;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.SetMultimap;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.*;
import com.google.openrtb.util.ProtoUtils;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.constants.TelecomOperator;
import com.mex.bidder.engine.util.RtbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import static com.google.openrtb.json.OpenRtbJsonUtils.*;

/**
 * User: donghai
 * Date: 2016/11/23
 */
public class ZplayOpenRtbJsonFactory extends OpenRtbJsonFactory {

    private static final Logger logger = LoggerFactory.getLogger(ZplayOpenRtbJsonFactory.class);

    /**
     * 低价转换单位
     */
    private static final int CURRENCY_UNIT = 100;

    private static ZplayDeviceMapper zplayDeviceMapper = new ZplayDeviceMapper();

    protected ZplayOpenRtbJsonFactory(
            @Nullable JsonFactory jsonFactory,
            boolean strict,
            boolean rootNativeField,
            boolean forceNativeAsObject,
            @Nullable SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders,
            @Nullable Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters) {
        super(jsonFactory, strict, rootNativeField, forceNativeAsObject, extReaders, extWriters);
    }

    protected ZplayOpenRtbJsonFactory(ZplayOpenRtbJsonFactory config) {
        super(config);
    }

    public static ZplayOpenRtbJsonFactory create() {
        return new ZplayOpenRtbJsonFactory(null, false, true, false, null, null);
    }

    @Override
    public OpenRtbNativeJsonReader newNativeReader() {
        return new ZplayOpenRtbNativeJsonReader(new ZplayOpenRtbJsonFactory(this));
    }
//
//    @Override
//    public OpenRtbNativeJsonWriter newNativeWriter() {
//        return new ZplayOpenRtbNativeJsonWriter(new ZplayOpenRtbJsonFactory(this));
//    }

    @Override
    public OpenRtbJsonReader newReader() {
        return new MyOpenRtbJsonReader(new ZplayOpenRtbJsonFactory(this));
    }

    @Override
    public OpenRtbJsonWriter newWriter() {
        return new MyOpenRtbJsonWriter(new ZplayOpenRtbJsonFactory(this));
    }

    static class MyOpenRtbJsonReader extends OpenRtbJsonReader {

        public MyOpenRtbJsonReader(ZplayOpenRtbJsonFactory factory) {
            super(factory);
        }

        public OpenRtb.BidRequest readBidRequest(Reader reader) throws IOException {
            OpenRtb.BidRequest.Builder bidRequest = readBidRequest(factory().getJsonFactory().createParser(reader));
            // 设置加密的设备id
            RtbHelper.md5DeviceId(bidRequest.getDeviceBuilder());
            return ProtoUtils.built(bidRequest);
        }


        @Override
        protected void readBidRequestField(JsonParser par, OpenRtb.BidRequest.Builder req, String fieldName) throws IOException {

            // zplay的请求标识字段 need_https 字段为判断  0的话  返回http  1的话返回https
            if (fieldName.equals("ext")) {
                for (startObject(par); OpenRtbJsonUtils.endObject(par); par.nextToken()) {
                    String extfieldName = OpenRtbJsonUtils.getCurrentName(par);
                    if (par.nextToken() != JsonToken.VALUE_NULL) {
                        readRequestExt(par, req, extfieldName);
                    }
                }
            } else {
                super.readBidRequestField(par, req, fieldName);
            }
            req.setExtension(MexOpenRtbExt.reqNetname, ZplayExchange.ID);

            // 设置加密的设备id
            RtbHelper.md5DeviceId(req.getDeviceBuilder());
        }

        @Override
        protected void readDeviceField(JsonParser par, OpenRtb.BidRequest.Device.Builder device, String fieldName) throws IOException {
            if (fieldName.equals("ext")) {
                for (startObject(par); OpenRtbJsonUtils.endObject(par); par.nextToken()) {
                    String extfieldName = OpenRtbJsonUtils.getCurrentName(par);
                    if (par.nextToken() != JsonToken.VALUE_NULL) {
                        readDeviceExt(par, device, extfieldName);
                    }
                }
            } else if (fieldName.equals("devicetype")) {
                OpenRtb.DeviceType value = zplayDeviceMapper.toMex(par.getIntValue());
                if (null != value) {
                    device.setDevicetype(value);
                }

            } else {
                super.readDeviceField(par, device, fieldName);
            }
        }

        @Override
        protected void readImpField(JsonParser par, OpenRtb.BidRequest.Imp.Builder imp, String fieldName) throws IOException {
            if ("bidfloor".equals(fieldName)) {
                imp.setBidfloor(par.getValueAsDouble() / CURRENCY_UNIT);
            } else if ("native".equals(fieldName)) {
                imp.setNative(readZplayNative(par));
            } else {
                super.readImpField(par, imp, fieldName);
            }
        }


        public OpenRtb.BidRequest.Imp.Native.Builder readZplayNative(JsonParser par) throws IOException {
            OpenRtb.BidRequest.Imp.Native.Builder nativ = OpenRtb.BidRequest.Imp.Native.newBuilder();
            for (startObject(par); endObject(par); par.nextToken()) {
                if (par.nextToken() != JsonToken.VALUE_NULL) {
                    readRequestOneofField(par, nativ);
                }
            }
            return nativ;
        }

        private void readRequestOneofField(JsonParser par, OpenRtb.BidRequest.Imp.Native.Builder nativ) throws IOException {
            for (startObject(par); endObject(par); par.nextToken()) {
                String fieldName = getCurrentName(par);
                nativ.setRequestNative(factory().newNativeReader().readNativeRequest(par));
            }
        }

    }


    static class MyOpenRtbJsonWriter extends OpenRtbJsonWriter {
        public MyOpenRtbJsonWriter(ZplayOpenRtbJsonFactory factory) {
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

            if (bid.getExtension(MexOpenRtbExt.adType) == MexOpenRtbExt.AdType.BANNER_AD) {
                buildBanner(bid, gen);
            } else if (bid.getExtension(MexOpenRtbExt.adType) == MexOpenRtbExt.AdType.NATIVE_AD) {
                buildNative(bid, gen);
            } else {
                throw new RuntimeException("no ad type found");
            }
            writeZplayBidExt(bid, gen);

        }

        private void buildNative(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
            gen.writeStringField("id", bid.getId());
            gen.writeStringField("impid", bid.getImpid());
            gen.writeStringField("adid", bid.getCrid());
            gen.writeNumberField("price", bid.getPrice() * CURRENCY_UNIT);
            if (bid.hasNurl()) {
                gen.writeStringField("nurl", bid.getNurl());
            }

            gen.writeFieldName("AdmOneof");
            gen.writeStartObject();
            gen.writeFieldName("AdmNative");
            gen.writeStartObject();

            writeAssetsArray(bid, gen);
            writeLink(bid, gen);

            gen.writeEndObject();
            gen.writeEndObject();

        }

        private void writeAssetsArray(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {

            gen.writeArrayFieldStart("assets");
            for (OpenRtb.NativeResponse.Asset asset : bid.getAdmNative().getAssetsList()) {
                writeNativeAsset(asset, gen);
            }
            gen.writeEndArray();
        }

        private static void writeLink(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
            gen.writeFieldName("link");
            if (Strings.isNullOrEmpty(bid.getExtension(MexOpenRtbExt.deeplink))) {
                writeLinkExt(gen, bid.getExtension(MexOpenRtbExt.landingpage), 1);
            } else {
                // deeplink
                writeLinkExt(gen, bid.getExtension(MexOpenRtbExt.deeplink), 2);
            }
        }

        private static void writeLinkExt(JsonGenerator gen, String url, int linkType) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("url", url);
            {
                gen.writeFieldName("ext");
                gen.writeStartObject();
                // 广告动作类型， 1: 在app内webview打开目标链接， 2： 在系统浏览器打开目标链接, 3：打开地图，4： 拨打电话，5：播 放视频, 6:App下载
                gen.writeNumberField("link_type", linkType);
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }

        private void writeNativeAsset(OpenRtb.NativeResponse.Asset asset, JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("id", asset.getId());
            if (asset.hasImg()) {
                gen.writeFieldName("img");
                gen.writeStartObject();
                gen.writeStringField("url", asset.getImg().getUrl());
                gen.writeEndObject();
            } else if (asset.hasTitle()) {
                gen.writeFieldName("title");
                gen.writeStartObject();
                gen.writeStringField("text", asset.getTitle().getText());
                gen.writeEndObject();
            } else if (asset.hasData()) {
                gen.writeFieldName("data");
                gen.writeStartObject();
                gen.writeStringField("label", asset.getData().getLabel());
                gen.writeStringField("value", asset.getData().getValue());
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }

        private void buildBanner(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
            gen.writeStringField("id", bid.getId());
            gen.writeStringField("impid", bid.getImpid());
            gen.writeNumberField("price", bid.getPrice() * CURRENCY_UNIT);
            if (bid.hasAdid()) {
                gen.writeStringField("adid", bid.getAdid());
            }
            if (bid.hasNurl()) {
                gen.writeStringField("nurl", bid.getNurl());
            }

            writeStrings("adomain", bid.getAdomainList(), gen);
            if (bid.hasBundle()) {
                gen.writeStringField("bundle", bid.getBundle());
            }
            if (bid.hasIurl()) {
                gen.writeStringField("iurl", bid.getIurl());
            }
            if (bid.hasCid()) {
                gen.writeStringField("cid", bid.getCid());
            }
            if (bid.hasCrid()) {
                gen.writeStringField("crid", bid.getCrid());
            }
            writeContentCategories("cat", bid.getCatList(), gen);
            writeEnums("attr", bid.getAttrList(), gen);
            if (bid.hasDealid()) {
                gen.writeStringField("dealid", bid.getDealid());
            }
            if (bid.hasW()) {
                gen.writeNumberField("w", bid.getW());
            }
            if (bid.hasH()) {
                gen.writeNumberField("h", bid.getH());
            }
            if (bid.hasApi()) {
                gen.writeNumberField("api", bid.getApi().getNumber());
            }
            if (bid.hasProtocol()) {
                gen.writeNumberField("protocol", bid.getProtocol().getNumber());
            }
            if (bid.hasQagmediarating()) {
                gen.writeNumberField("qagmediarating", bid.getQagmediarating().getNumber());
            }
            if (bid.hasExp()) {
                gen.writeNumberField("exp", bid.getExp());
            }
        }
    }


    static void writeZplayBidExt(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
        gen.writeFieldName("ext");
        gen.writeStartObject();
        // 只有banner才通过clkurl返回落地页，native是通过native.link返回
        if (bid.getExtension(MexOpenRtbExt.adType) == MexOpenRtbExt.AdType.BANNER_AD) {
            if (!Strings.isNullOrEmpty(bid.getExtension(MexOpenRtbExt.deeplink))) {
                // deeplink
                gen.writeStringField("clkurl", bid.getExtension(MexOpenRtbExt.deeplink));
            } else {
                gen.writeStringField("clkurl", bid.getExtension(MexOpenRtbExt.landingpage));
            }
        }
        if (bid.getExtension(MexOpenRtbExt.adType) == MexOpenRtbExt.AdType.NATIVE_AD) {
            gen.writeNumberField("action", 1);
        }

        OpenRtbJsonUtils.writeStrings("imptrackers", bid.getExtension(MexOpenRtbExt.imptrackers), gen);
        OpenRtbJsonUtils.writeStrings("clktrackers", bid.getExtension(MexOpenRtbExt.clktrackers), gen);

        gen.writeEndObject();
    }

    /**
     * request ext
     *
     * @param par
     * @param req
     * @param fieldName
     * @throws IOException
     */
    static void readRequestExt(JsonParser par, OpenRtb.BidRequest.Builder req, String fieldName) throws IOException {
        if ("is_ping".equals(fieldName)) {
            req.setExtension(MexOpenRtbExt.isPing, par.getBooleanValue());
        }
        if ("version".equals(fieldName)) {
            req.setExtension(MexOpenRtbExt.version, par.getIntValue());
        }
        if ("need_https".equals(fieldName)) {
            boolean intValue = par.getBooleanValue();
            req.setExtension(MexOpenRtbExt.needHttps, intValue);
        }

    }

    static void readImpExt(JsonParser par, OpenRtb.BidRequest.Imp.Builder imp, String fieldName) throws IOException {
        if ("is_splash_screen".equals(fieldName)) {
            imp.setExtension(MexOpenRtbExt.isSplashScreen, par.getBooleanValue());
        }

    }

    static void readDeviceExt(JsonParser par, OpenRtb.BidRequest.Device.Builder device, String fieldName) throws IOException {

        if ("mac".equals(fieldName)) {
            device.setExtension(MexOpenRtbExt.mac, par.getValueAsString());
        }
        if ("imei".equals(fieldName)) {
            device.setExtension(MexOpenRtbExt.imei, par.getValueAsString());
        }
        if ("android_id".equals(fieldName)) {
            device.setExtension(MexOpenRtbExt.androidId, par.getValueAsString());
        }
        if ("adid".equals(fieldName)) {
            device.setExtension(MexOpenRtbExt.idfa, par.getValueAsString());
        }
        if ("plmn".equals(fieldName)) {
            TelecomOperator telecomOperator = ZplayCarrierTypeMapper.mapper.toMex(par.getValueAsString());
            device.setCarrier(telecomOperator.getValue());
            logger.info("zplay carrier=" + telecomOperator.getValue());
        }
    }

}
