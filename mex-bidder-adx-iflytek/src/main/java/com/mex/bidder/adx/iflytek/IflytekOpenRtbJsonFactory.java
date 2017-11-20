package com.mex.bidder.adx.iflytek;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Lists;
import com.google.common.collect.SetMultimap;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.*;
import com.google.openrtb.util.ProtoUtils;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.constants.TelecomOperator;
import com.mex.bidder.engine.util.MD5Utils;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.engine.util.RtbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.openrtb.json.OpenRtbJsonUtils.*;

/**
 * User: donghai
 * Date: 2016/11/23
 */
public class IflytekOpenRtbJsonFactory extends OpenRtbJsonFactory {

    private static final Logger logger = LoggerFactory.getLogger(IflytekOpenRtbJsonFactory.class);


    protected IflytekOpenRtbJsonFactory(
            @Nullable JsonFactory jsonFactory,
            boolean strict,
            boolean rootNativeField,
            boolean forceNativeAsObject,
            @Nullable SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders,
            @Nullable Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters) {
        super(jsonFactory, strict, rootNativeField, forceNativeAsObject, extReaders, extWriters);
    }

    protected IflytekOpenRtbJsonFactory(IflytekOpenRtbJsonFactory config) {
        super(config);
    }

    public static IflytekOpenRtbJsonFactory create() {
        return new IflytekOpenRtbJsonFactory(null, false, true, false, null, null);
    }

    @Override
    public OpenRtbJsonReader newReader() {
        return new IflytekOpenRtbJsonFactory.MyOpenRtbJsonReader(new IflytekOpenRtbJsonFactory(this));
    }

    @Override
    public OpenRtbJsonWriter newWriter() {
        return new IflytekOpenRtbJsonFactory.MyOpenRtbJsonWriter(new IflytekOpenRtbJsonFactory(this));
    }

    static class MyOpenRtbJsonReader extends OpenRtbJsonReader {

        public MyOpenRtbJsonReader(IflytekOpenRtbJsonFactory factory) {
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
            req.setExtension(MexOpenRtbExt.reqNetname, IflytekExchange.ID);
        }

        @Override
        protected void readImpField(JsonParser par, OpenRtb.BidRequest.Imp.Builder imp, String fieldName) throws IOException {
            if ("secure".equals(fieldName)) {
                String text = par.getText();
                imp.setSecure("1".equals(par.getText()));
            } else if ("native".equals(fieldName)) {
                imp.setNative(iflyReadNative(par));
            } else if ("instl".equals(fieldName)) {
                imp.setExtension(MexOpenRtbExt.iflytekInstl, par.getIntValue());
            } else if ("is_support_deeplink".equals(fieldName)) {
                imp.setExtension(MexOpenRtbExt.isSupportDeeplink, par.getIntValue() == 1 ? true : false);
            } else {
                super.readImpField(par, imp, fieldName);
            }
        }

        public OpenRtb.BidRequest.Imp.Native.Builder iflyReadNative(JsonParser par) throws IOException {
            OpenRtb.BidRequest.Imp.Native.Builder nativ = OpenRtb.BidRequest.Imp.Native.newBuilder();

            OpenRtb.NativeRequest.Builder req = OpenRtb.NativeRequest.newBuilder();

            for (startObject(par); endObject(par); par.nextToken()) {
                String fieldName = getCurrentName(par);
                if (par.nextToken() != JsonToken.VALUE_NULL) {
                    readNativeField(par, req, fieldName);
                }
            }

            nativ.setRequestNative(req);

            return nativ;
        }

        protected void readNativeField(JsonParser par, OpenRtb.NativeRequest.Builder req, String fieldName) throws IOException {

            switch (fieldName) {
                case "title":
                    req.addAssets(readReqTitle(par));
                    break;
                case "img":
                    req.addAssets(readReqImage(par));
                    break;
                case "desc":
                    req.addAssets(readReqData(par));
                    break;
                default:
                    logger.warn("unknown fieldName=" + fieldName);
            }
        }

        public final OpenRtb.NativeRequest.Asset.Builder readReqTitle(JsonParser par)
                throws IOException {
            OpenRtb.NativeRequest.Asset.Builder asset = OpenRtb.NativeRequest.Asset.newBuilder();
            asset.setId(0);
            OpenRtb.NativeRequest.Asset.Title.Builder title = OpenRtb.NativeRequest.Asset.Title.newBuilder();
            for (startObject(par); endObject(par); par.nextToken()) {
                String fieldName = getCurrentName(par);
                if (par.nextToken() != JsonToken.VALUE_NULL) {
                    readReqTitleField(par, title, fieldName);
                }
            }
            return asset.setTitle(title);
        }

        protected void readReqTitleField(
                JsonParser par, OpenRtb.NativeRequest.Asset.Title.Builder title, String fieldName)
                throws IOException {
            switch (fieldName) {
                case "len":
                    title.setLen(par.getIntValue());
                    break;
                default:
                    readOther(title, par, fieldName);
            }
        }

        public final OpenRtb.NativeRequest.Asset.Builder readReqImage(JsonParser par)
                throws IOException {
            OpenRtb.NativeRequest.Asset.Builder asset = OpenRtb.NativeRequest.Asset.newBuilder();
            asset.setId(0);
            OpenRtb.NativeRequest.Asset.Image.Builder req = OpenRtb.NativeRequest.Asset.Image.newBuilder();
            req.setType(OpenRtb.ImageAssetType.MAIN);
            for (startObject(par); endObject(par); par.nextToken()) {
                String fieldName = getCurrentName(par);
                if (par.nextToken() != JsonToken.VALUE_NULL) {
                    readReqImageField(par, req, fieldName);
                }
            }
            return asset.setImg(req);
        }

        protected void readReqImageField(
                JsonParser par, OpenRtb.NativeRequest.Asset.Image.Builder image, String fieldName)
                throws IOException {
            switch (fieldName) {
                case "w":
                    image.setW(par.getIntValue());
                    break;
                case "h":
                    image.setH(par.getIntValue());
                    break;
                default:
                    logger.warn("unknown fieldName=" + fieldName);
            }
        }

        public final OpenRtb.NativeRequest.Asset.Builder readReqData(JsonParser par) throws IOException {
            OpenRtb.NativeRequest.Asset.Builder asset = OpenRtb.NativeRequest.Asset.newBuilder();
            asset.setId(0);
            OpenRtb.NativeRequest.Asset.Data.Builder data = OpenRtb.NativeRequest.Asset.Data.newBuilder();
            data.setType(OpenRtb.DataAssetType.DESC);
            for (startObject(par); endObject(par); par.nextToken()) {
                String fieldName = getCurrentName(par);
                if (par.nextToken() != JsonToken.VALUE_NULL) {
                    readReqDataField(par, data, fieldName);
                }
            }
            return asset.setData(data);
        }

        protected void readReqDataField(
                JsonParser par, OpenRtb.NativeRequest.Asset.Data.Builder data, String fieldName) throws IOException {
            switch (fieldName) {
                case "len":
                    data.setLen(par.getIntValue());
                    break;
                default:
                    logger.warn("unknown fieldName=" + fieldName);
            }
        }


        @Override
        protected void readDeviceField(JsonParser par, OpenRtb.BidRequest.Device.Builder device, String fieldName) throws
                IOException {

            if ("ifa".equals(fieldName)) {
                device.setExtension(MexOpenRtbExt.idfa, par.getValueAsString());
                device.setExtension(MexOpenRtbExt.androidId, par.getValueAsString());
            } else if ("devicetype".equals(fieldName)) {
                OpenRtb.DeviceType deviceType = IflytekDeviceTypeMapper.mapper.toMex(par.getIntValue());
                if (Objects.nonNull(deviceType)) {
                    device.setDevicetype(deviceType);
                }
            } else if ("connectiontype".equals(fieldName)) {
                device.setConnectiontype(IflytekConnectionTypeMapper.mapper.toMex(par.getIntValue()));
            } else if ("carrier".equals(fieldName)) {
                TelecomOperator telecomOperator = IflytekCarrierTypeMapper.mapper.toMex(par.getValueAsString());
                device.setCarrier(telecomOperator.getValue());
                logger.info("iflytek carrier=" + telecomOperator.getValue());
            } else if ("mac".equals(fieldName)) {
                device.setExtension(MexOpenRtbExt.mac, par.getValueAsString());
            } else if ("dpid".equals(fieldName)) {
                device.setExtension(MexOpenRtbExt.androidId, par.getValueAsString());
            } else if ("did".equals(fieldName)) {
                device.setExtension(MexOpenRtbExt.imei, par.getValueAsString());
            } else {
                super.readDeviceField(par, device, fieldName);
            }

        }
    }

    static class MyOpenRtbJsonWriter extends OpenRtbJsonWriter {
        public MyOpenRtbJsonWriter(IflytekOpenRtbJsonFactory factory) {
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
                gen.writeStringField("cur", "CNY");
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

            gen.writeStringField("id", bid.getId());
            gen.writeStringField("impid", bid.getImpid());
            gen.writeNumberField("price", bid.getPrice());
            gen.writeStringField("nurl", bid.getNurl());
            gen.writeStringField("lattr", "1"); // 1:网页,2:下载,3:应用市场

            // todo 类型

            if (bid.getExtension(MexOpenRtbExt.adType) == MexOpenRtbExt.AdType.BANNER_AD) {
                buildBannerAd(bid, gen);
            } else if (bid.getExtension(MexOpenRtbExt.adType) == MexOpenRtbExt.AdType.NATIVE_AD) {
                buildNativeAd(bid, gen);
            } else {
                throw new RuntimeException("no ad type found");
            }

        }

        private void buildNativeAd(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
            OpenRtb.NativeResponse resp = bid.getAdmNative();
            NativeAd nativeAd = new NativeAd();
            nativeAd.setLanding(bid.getExtension(MexOpenRtbExt.landingpage));

            List<String> imptrackerList = bid.getExtension(MexOpenRtbExt.imptrackers);
            nativeAd.setImptrackers(imptrackerList);

            if (bid.getExtensionCount(MexOpenRtbExt.clktrackers) > 0) {
                List<String> clktrackers = bid.getExtension(MexOpenRtbExt.clktrackers);
                nativeAd.setClicktrackers(clktrackers);
            }

            List<String> img_urls = Lists.newArrayList();
            int imageCount = 0;
            for (OpenRtb.NativeResponse.Asset asset : resp.getAssetsList()) {
                if (asset.hasTitle()) {
                    nativeAd.setTitle(asset.getTitle().getText());
                } else if (asset.hasImg()) {
                    imageCount = imageCount + 1;
                    img_urls.add(asset.getImg().getUrl());
                } else if (asset.hasData()) {
                    nativeAd.setDesc(asset.getData().getValue());
                }
            }
            nativeAd.setImg_urls(img_urls);
            nativeAd.setImageCount(imageCount);

            gen.writeFieldName("native_ad");
            if (!Strings.isNullOrEmpty(bid.getExtension(MexOpenRtbExt.deeplink))) {
                nativeAd.setDeeplink(bid.getExtension(MexOpenRtbExt.deeplink));
            }
            writeNativeAdObject(nativeAd, gen);
        }

        private void writeNativeAdObject(NativeAd nativeAd, JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            this.writeNativeAdFields(nativeAd, gen);
            gen.writeEndObject();
        }

        private void writeNativeAdFields(NativeAd nativeAd, JsonGenerator gen) throws IOException {
            if (null != nativeAd.getTitle()) {
                gen.writeStringField("title", nativeAd.getTitle());
            }

            // instl = 7  一图一文 图片通过img传
            if (nativeAd.getImageCount() == 1 && Strings.emptyToNull(nativeAd.getTitle()) != null
                    && Strings.emptyToNull(nativeAd.getDesc()) == null) {
                gen.writeStringField("img", nativeAd.getImg_urls().get(0));
            }
            //  instl = 8  一图两文 图片通过img传
            else if (nativeAd.getImageCount() == 1 && Strings.emptyToNull(nativeAd.getTitle()) != null
                    && Strings.emptyToNull(nativeAd.getDesc()) != null) {
                gen.writeStringField("img", nativeAd.getImg_urls().get(0));
                gen.writeStringField("desc", Strings.nullToEmpty(nativeAd.getDesc()));
            }
            // instl = 12 一图    图片通过img_urls传
            else if (nativeAd.getImageCount() == 1 && Strings.emptyToNull(nativeAd.getTitle()) == null) {
                OpenRtbJsonUtils.writeStrings("img_urls", nativeAd.getImg_urls(), gen);
            }
            // instl = 13 三图一文 图片通过img_urls传
            else if (nativeAd.getImageCount() == 3 && Strings.emptyToNull(nativeAd.getTitle()) != null) {
                OpenRtbJsonUtils.writeStrings("img_urls", nativeAd.getImg_urls(), gen);
            }

            gen.writeStringField("landing", nativeAd.getLanding());
            if (!Strings.isNullOrEmpty(nativeAd.getDeeplink())){
                gen.writeStringField("deep_link", nativeAd.getDeeplink());
            }
            OpenRtbJsonUtils.writeStrings("imptrackers", nativeAd.getImptrackers(), gen);
            OpenRtbJsonUtils.writeStrings("clicktrackers", nativeAd.getClicktrackers(), gen);
        }

        private void buildBannerAd(OpenRtb.BidResponse.SeatBid.Bid bid, JsonGenerator gen) throws IOException {
            Banner_ad banner_ad = new Banner_ad();
            banner_ad.setMtype(2);
            banner_ad.setTitle("");
            banner_ad.setDesc("");
            banner_ad.setImage_url(bid.getAdm());
            banner_ad.setHtml("");
            banner_ad.setLanding(bid.getExtension(MexOpenRtbExt.landingpage));
            banner_ad.setH(bid.getH());
            banner_ad.setW(bid.getW());
            banner_ad.setPackage_name("");

            List<String> imptrackerList = bid.getExtension(MexOpenRtbExt.imptrackers);
            banner_ad.setImpress(imptrackerList);

            if (bid.getExtensionCount(MexOpenRtbExt.clktrackers) > 0) {
                List<String> clktrackers = bid.getExtension(MexOpenRtbExt.clktrackers);
                banner_ad.setClick(clktrackers);
            }
            gen.writeFieldName("banner_ad");
            if (!Strings.isNullOrEmpty(bid.getExtension(MexOpenRtbExt.deeplink))) {
                banner_ad.setDeeplink(bid.getExtension(MexOpenRtbExt.deeplink));
            }
            writeApp(banner_ad, gen);

        }

        public final void writeApp(Banner_ad banner_ad, JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            this.writeAppFields(banner_ad, gen);
            // this.writeExtensions(app, gen);
            gen.writeEndObject();
        }

        protected void writeAppFields(Banner_ad banner_ad, JsonGenerator gen) throws IOException {
            gen.writeNumberField("mtype", banner_ad.getMtype());
            gen.writeStringField("title", banner_ad.getTitle());
            gen.writeStringField("desc", banner_ad.getDesc());
            gen.writeStringField("image_url", banner_ad.getImage_url());
            gen.writeStringField("html", banner_ad.getHtml());
            gen.writeStringField("landing", banner_ad.getLanding());
            if (!Strings.isNullOrEmpty(banner_ad.getDeeplink())){
                gen.writeStringField("deep_link", banner_ad.getDeeplink());
            }
            gen.writeNumberField("w", banner_ad.getW());
            gen.writeNumberField("h", banner_ad.getH());
            OpenRtbJsonUtils.writeStrings("impress", banner_ad.getImpress(), gen);
            OpenRtbJsonUtils.writeStrings("click", banner_ad.getClick(), gen);
            gen.writeStringField("package_name", banner_ad.getPackage_name());
        }
    }
}
