package com.mex.bidder.adx.adview;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * User: donghai
 * Date: 2016/11/23
 */
public class AdviewOpenRtbJsonFactory extends OpenRtbJsonFactory {

    private static final Logger logger = LoggerFactory.getLogger(AdviewOpenRtbJsonFactory.class);

    private static final int CURRENCY_UNIT = 10000;

    protected AdviewOpenRtbJsonFactory(
            @Nullable JsonFactory jsonFactory,
            boolean strict,
            boolean rootNativeField,
            boolean forceNativeAsObject,
            @Nullable SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders,
            @Nullable Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters) {
        super(jsonFactory, strict, rootNativeField, forceNativeAsObject, extReaders, extWriters);

    }

    public OpenRtbNativeJsonWriter newNativeWriter() {
        return new AdviewOpenRtbNativeJsonWriter(new AdviewOpenRtbJsonFactory(this));
    }

    protected AdviewOpenRtbJsonFactory(AdviewOpenRtbJsonFactory config) {
        super(config);
    }

    public static AdviewOpenRtbJsonFactory create() {
        return new AdviewOpenRtbJsonFactory(null, false, true, false, null, null);
    }

    @Override
    public OpenRtbJsonReader newReader() {
        return new MyOpenRtbJsonReader(new AdviewOpenRtbJsonFactory(this));
    }

    @Override
    public OpenRtbJsonWriter newWriter() {
        return new MyOpenRtbJsonWriter(new AdviewOpenRtbJsonFactory(this));
    }

    static class MyOpenRtbJsonReader extends OpenRtbJsonReader {

        public MyOpenRtbJsonReader(AdviewOpenRtbJsonFactory factory) {
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
            super.readBidRequestField(par, req, fieldName);
            req.setExtension(MexOpenRtbExt.reqNetname, AdViewExchange.ID);
        }

        @Override
        protected void readImpField(JsonParser par, OpenRtb.BidRequest.Imp.Builder imp, String fieldName) throws IOException {
            if ("bidfloor".equals(fieldName)) {
                imp.setBidfloor(par.getValueAsDouble() / CURRENCY_UNIT);
            } else if ("secure".equals(fieldName)) {
                //0=http;1=https;缺省或其他值=http  update 0=http;1=https;缺省http和https都可以
                imp.setSecure("1".equals(par.getText()));
            } else {
                super.readImpField(par, imp, fieldName);
            }

        }


        @Override
        protected void readDeviceField(JsonParser par, OpenRtb.BidRequest.Device.Builder device, String fieldName) throws IOException {
            if ("connectiontype".equals(fieldName)) {
                OpenRtb.ConnectionType connectionType = AdviewConnectionTypeMapper.mapper.toMex(par.getIntValue());
                if (Objects.nonNull(connectionType)) {
                    device.setConnectiontype(connectionType);
                }
            } else if ("devicetype".equals(fieldName)) {
                OpenRtb.DeviceType deviceType = AdviewDeviceTypeMapper.mapper.toMex(par.getIntValue());
                if (Objects.nonNull(deviceType)) {
                    device.setDevicetype(deviceType);
                }
            } else if ("carrier".equals(fieldName)) {
                TelecomOperator telecomOperator = AdviewCarrierTypeMapper.mapper.toMex(par.getValueAsString());
                device.setCarrier(telecomOperator.getValue());
            } else if ("idfa".equals(fieldName)) {
                device.setExtension(MexOpenRtbExt.idfa, par.getText());
            } else if ("ext".equals(fieldName)) {
                for (OpenRtbJsonUtils.startObject(par); OpenRtbJsonUtils.endObject(par); par.nextToken()) {
                    String currentName = OpenRtbJsonUtils.getCurrentName(par);
                    if ("mac".equals(currentName)) {
                        device.setExtension(MexOpenRtbExt.mac, par.getText());
                    } else if ("androidId".equals(currentName)) {
                        device.setExtension(MexOpenRtbExt.androidId, par.getText());
                    } else if ("uuid".equals(currentName)) {
                        // adview 的uuid和imei原始值相同
                        device.setExtension(MexOpenRtbExt.imei, par.getText());
                    }
                }
            } else {
                super.readDeviceField(par, device, fieldName);
            }
        }

    }

    static class MyOpenRtbJsonWriter extends OpenRtbJsonWriter {
        public MyOpenRtbJsonWriter(AdviewOpenRtbJsonFactory factory) {
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
                gen.writeStringField("cur", "RMB");
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
                buildBannerBid(bid, gen);
            } else if (bid.getExtension(MexOpenRtbExt.adType) == MexOpenRtbExt.AdType.NATIVE_AD) {
                //buildNativeBid(bid, gen);
                buildNativeBidNew(bid, gen);
            } else {
                throw new RuntimeException("no ad type found");
            }

        }

        private void buildNativeBidNew(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {

            gen.writeStringField("id", bid.getId());
            gen.writeStringField("impid", bid.getImpid());
            gen.writeNumberField("price", (int) (bid.getPrice() * CURRENCY_UNIT));
            gen.writeNumberField("paymode", 1); // 1=CPM
            gen.writeNumberField("adct", 1); // val = 1 （打开网页 ）
            gen.writeNumberField("admt", 8); // val= 8(原生广告 )
            if (bid.hasAdid()) {
                gen.writeStringField("adid", bid.getAdid());
            }

            // native写出
            NativeObjectHelper.writeNativeResponseFields(bid, gen);

            if (bid.getAdomainCount() > 0) {
                OpenRtbJsonUtils.writeStrings("adomain", bid.getAdomainList(), gen);
            }
            if (bid.hasNurl()) {
                gen.writeStringField("wurl", bid.getNurl());
            }
            if (bid.hasCrid()) {
                gen.writeStringField("crid", bid.getCrid());
                gen.writeStringField("cid", bid.getCrid());
            }

        }

        private void buildNativeBid(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
            gen.writeStringField("id", bid.getId());
            gen.writeStringField("impid", bid.getImpid());
            gen.writeNumberField("price", (int) (bid.getPrice() * CURRENCY_UNIT));
            gen.writeNumberField("paymode", 1); // 1=CPM
            gen.writeNumberField("adct", 1); // val = 1 （打开网页 ）
            gen.writeNumberField("admt", 8); // val= 8(原生广告 )
            if (bid.hasAdid()) {
                gen.writeStringField("adid", bid.getAdid());
            }

            // native写出
            gen.writeFieldName("native");
            nativeWriter().writeNativeResponse(bid.getAdmNative(), gen);

            if (bid.hasExtension(MexOpenRtbExt.landingpage)) {
                gen.writeStringField("adurl", bid.getExtension(MexOpenRtbExt.landingpage));
            }
            if (bid.getAdomainCount() > 0) {
                OpenRtbJsonUtils.writeStrings("adomain", bid.getAdomainList(), gen);
            }

            if (bid.hasNurl()) {
                gen.writeStringField("wurl", bid.getNurl());
            }
            if (bid.hasCrid()) {
                gen.writeStringField("crid", bid.getCrid());
                gen.writeStringField("cid", bid.getCrid());
            }

            if (bid.getExtensionCount(MexOpenRtbExt.imptrackers) > 0) {
                List<String> imptrackerList = bid.getExtension(MexOpenRtbExt.imptrackers);
                gen.writeFieldName("nurl");
                gen.writeStartObject();
                OpenRtbJsonUtils.writeStrings("0", imptrackerList, gen);
                gen.writeEndObject();
            }

            if (bid.getExtensionCount(MexOpenRtbExt.clktrackers) > 0) {
                List<String> clktrackers = bid.getExtension(MexOpenRtbExt.clktrackers);
                OpenRtbJsonUtils.writeStrings("curl", clktrackers, gen);
            }
        }

        private void buildBannerBid(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
            gen.writeStringField("id", bid.getId());
            gen.writeStringField("impid", bid.getImpid());
            gen.writeNumberField("price", (int) (bid.getPrice() * CURRENCY_UNIT));
            gen.writeNumberField("paymode", 1);
            gen.writeNumberField("adct", 1); // TODO 广告点击行为 暂定 val = 1 （打开网页 ）
            gen.writeNumberField("admt", 1); // TODO 广告类型  暂定 val= 1(图片广告 )

            if (bid.hasAdid()) {
                gen.writeStringField("adid", bid.getAdid());
            }

            gen.writeStringField("adi", bid.getAdm());
            gen.writeStringField("adt", "");
            gen.writeStringField("ads", "");

            if (bid.hasW()) {
                gen.writeNumberField("adw", bid.getW());
            }
            if (bid.hasH()) {
                gen.writeNumberField("adh", bid.getH());
            }

            if (bid.hasExtension(MexOpenRtbExt.landingpage)) {
                //
                gen.writeStringField("adurl", bid.getExtension(MexOpenRtbExt.landingpage));
            }
            if (!Strings.isNullOrEmpty(bid.getExtension(MexOpenRtbExt.deeplink))) {
                gen.writeStringField("deeplink", bid.getExtension(MexOpenRtbExt.deeplink));
            }

            if (bid.getAdomainCount() > 0) {
                OpenRtbJsonUtils.writeStrings("adomain", bid.getAdomainList(), gen);
            }

            if (bid.hasNurl()) {
                gen.writeStringField("wurl", bid.getNurl());
            }


            if (bid.hasCrid()) {
                gen.writeStringField("crid", bid.getCrid());
                gen.writeStringField("cid", bid.getCrid());
            }

            if (bid.getExtensionCount(MexOpenRtbExt.imptrackers) > 0) {
                List<String> imptrackerList = bid.getExtension(MexOpenRtbExt.imptrackers);
                gen.writeFieldName("nurl");
                gen.writeStartObject();
                OpenRtbJsonUtils.writeStrings("0", imptrackerList, gen);
                gen.writeEndObject();
            }

            if (bid.getExtensionCount(MexOpenRtbExt.clktrackers) > 0) {
                List<String> clktrackers = bid.getExtension(MexOpenRtbExt.clktrackers);
                OpenRtbJsonUtils.writeStrings("curl", clktrackers, gen);
            }
        }

    }


}
