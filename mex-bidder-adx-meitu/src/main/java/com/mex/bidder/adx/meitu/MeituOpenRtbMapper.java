package com.mex.bidder.adx.meitu;

import com.google.common.base.Stopwatch;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.OpenRtbJsonReader;
import com.google.openrtb.json.OpenRtbJsonWriter;
import com.meitu.openrtb.MeituOpenRtb;
import com.mex.bidder.api.mapper.OpenRtbMapper;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.constants.TelecomOperator;
import com.mex.bidder.protocol.nativead.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * xuchuahao
 * on 2017/6/12.
 */
@Singleton
public class MeituOpenRtbMapper implements OpenRtbMapper<MeituOpenRtb.BidRequest, MeituOpenRtb.BidResponse,
        MeituOpenRtb.BidRequest.Builder, MeituOpenRtb.BidResponse.Builder> {

    private static final Logger logger = LoggerFactory.getLogger(MeituOpenRtbMapper.class);

    // 底价转换单位
    private static final int CURRENCY_UNIT = 100;

    private OpenRtbJsonReader openRtbJsonReader;
    private OpenRtbJsonWriter openRtbJsonWriter;

    public MeituOpenRtbMapper() {
        MeituOpenRtbJsonFactory factory = MeituOpenRtbJsonFactory.create();
        openRtbJsonReader = factory.newReader();
        openRtbJsonWriter = factory.newWriter();
    }


    @Override
    public MeituOpenRtb.BidResponse.Builder toExchangeBidResponse(@Nullable OpenRtb.BidRequest request, OpenRtb.BidResponse response) {
        checkNotNull(request);
        Stopwatch stopwatch = Stopwatch.createStarted();
        // 无广告下发，直接返回空
        if (response.getSeatbidCount() == 0 || response.getSeatbid(0).getBidCount() == 0) {
            return MeituConst.emptyResponse(request, response);
        }

        MeituOpenRtb.BidResponse.Builder meituResponse = MeituOpenRtb.BidResponse.newBuilder();
        meituResponse.setId(request.getId());
        meituResponse.setBidid(response.getBidid());

        MeituOpenRtb.BidResponse.SeatBid.Builder meituSeatBid = MeituOpenRtb.BidResponse.SeatBid.newBuilder();


        OpenRtb.BidResponse.SeatBid.Bid bid = response.getSeatbid(0).getBid(0);
        MeituOpenRtb.BidResponse.SeatBid.Bid.Builder meituBid = MeituOpenRtb.BidResponse.SeatBid.Bid.newBuilder();
        meituBid.setId(bid.getId());
        meituBid.setImpid(bid.getImpid());
        meituBid.setPrice(bid.getPrice() * CURRENCY_UNIT);
        meituBid.setAdid(bid.getAdid());
        meituBid.setNurl(bid.getNurl());
        if (bid.getAdomainCount() > 0) {
            meituBid.addAllAdomain(bid.getAdomainList());
        }
        meituBid.setIurl(bid.getIurl());
        meituBid.setCid(bid.getCid());
        meituBid.setCrid(bid.getCrid());
        meituBid.setW(bid.getW());
        meituBid.setH(bid.getH());
        meituBid.addAllImpTrackUrls(bid.getExtension(MexOpenRtbExt.imptrackers));
        //click只能接受一个链接，下发方式只能为302
        meituBid.setClkThroughUrl(bid.getExtension(MexOpenRtbExt.clktrackers).get(0));

        MeituOpenRtb.NativeResponse.Builder anative = buildNativeResponse(request, response);

        meituBid.setAdmNative(anative);
        meituSeatBid.addBid(meituBid);
        meituResponse.addSeatbid(meituSeatBid);

        return meituResponse;
    }

    private MeituOpenRtb.NativeResponse.Builder buildNativeResponse(OpenRtb.BidRequest request, OpenRtb.BidResponse response) {
        MeituOpenRtb.NativeResponse.Builder meituNativeResponse = MeituOpenRtb.NativeResponse.newBuilder();
        MeituOpenRtb.NativeResponse.Asset.Builder meituResponseAsset = MeituOpenRtb.NativeResponse.Asset.newBuilder();
        MeituOpenRtb.NativeResponse.Link.Builder meituResponseLink = MeituOpenRtb.NativeResponse.Link.newBuilder();

        OpenRtb.BidResponse.SeatBid.Bid bid = response.getSeatbid(0).getBid(0);
        OpenRtb.NativeResponse nativeResponse = bid.getAdmNative();

        if (nativeResponse.hasVer()) {
            meituNativeResponse.setVer(nativeResponse.getVer());
        }
        // 展示追踪
        if (bid.getExtensionCount(MexOpenRtbExt.imptrackers) > 0) {
            meituNativeResponse.addAllImptrackers(bid.getExtension(MexOpenRtbExt.imptrackers));
        }
        //TODO 下发方式和落地页
        if (bid.hasExtension(MexOpenRtbExt.landingpage)) {
            meituResponseLink.setUrl(bid.getExtension(MexOpenRtbExt.landingpage));
        }
        if (bid.getExtensionCount(MexOpenRtbExt.clktrackers) > 0) {
            meituResponseLink.addAllClicktrackers(bid.getExtension(MexOpenRtbExt.clktrackers));
        }
        meituNativeResponse.setLink(meituResponseLink);


        for (OpenRtb.NativeResponse.Asset asset : nativeResponse.getAssetsList()) {
            if (asset.hasTitle()) {
                OpenRtb.NativeResponse.Asset.Title title = asset.getTitle();
                MeituOpenRtb.NativeResponse.Asset.Title.Builder meituResponseTitle = MeituOpenRtb.NativeResponse.Asset.Title.newBuilder();
                if (title.hasText()) {
                    meituResponseTitle.setText(title.getText());
                }
                meituResponseAsset.setId(asset.getId());
                meituResponseAsset.setTitle(meituResponseTitle);
            } else if (asset.hasData()) {
                OpenRtb.NativeResponse.Asset.Data data = asset.getData();
                MeituOpenRtb.NativeResponse.Asset.Data.Builder meituResponseData = MeituOpenRtb.NativeResponse.Asset.Data.newBuilder();
                if (data.hasLabel()) {
                    meituResponseData.setLabel(data.getLabel());
                }
                if (data.hasValue()) {
                    meituResponseData.setValue(data.getValue());
                }
                meituResponseAsset.setId(asset.getId());
                meituResponseAsset.setData(meituResponseData);
            } else if (asset.hasImg()) {
                OpenRtb.NativeResponse.Asset.Image img = asset.getImg();
                MeituOpenRtb.NativeResponse.Asset.Image.Builder meituResponseImg = MeituOpenRtb.NativeResponse.Asset.Image.newBuilder();
                if (img.hasW()) {
                    meituResponseImg.setW(img.getW());
                }
                if (img.hasH()) {
                    meituResponseImg.setH(img.getH());
                }
                if (img.hasUrl()) {
                    meituResponseImg.setUrl(img.getUrl());
                }
                meituResponseAsset.setId(asset.getId());
                meituResponseAsset.setImg(meituResponseImg);
            }
            meituNativeResponse.addAssets(meituResponseAsset);
        }
        return meituNativeResponse;
    }

    @Override
    public OpenRtb.BidRequest.Builder toOpenRtbBidRequest(MeituOpenRtb.BidRequest meituRequest) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        OpenRtb.BidRequest.Builder mexRequest = OpenRtb.BidRequest.newBuilder().setId(meituRequest.getId());
        mexRequest.setExtension(MexOpenRtbExt.reqNetname, MeituExchange.ID);

        // 如果没有展示信息直接返回
        if (meituRequest.getImpCount() == 0) {
            logger.debug("Reqeust has no impressions");
            mexRequest.setExtension(MexOpenRtbExt.isOk, false);
            return mexRequest;
        }
        String name = meituRequest.getAt().name();
        int number = meituRequest.getAt().getNumber();

        mexRequest.setTmax(meituRequest.getTmax());

        OpenRtb.BidRequest.Imp.Builder imp = buildImp(meituRequest);
        if (imp != null) {
            mexRequest.addImp(imp);
        }

        OpenRtb.BidRequest.Device.Builder device = buildDevice(meituRequest);
        if (device != null) {
            mexRequest.setDevice(device);
        }

        OpenRtb.BidRequest.App.Builder app = buildApp(meituRequest);
        if (app != null) {
            mexRequest.setApp(app);
        }

        OpenRtb.BidRequest.User.Builder user = buildUser(meituRequest);
        if (user != null) {
            mexRequest.setUser(user);
        }

        stopwatch.stop();
        logger.info("meitu to openrtb bid  request time " + stopwatch);
        return mexRequest;
    }

    private OpenRtb.BidRequest.User.Builder buildUser(MeituOpenRtb.BidRequest meituRequest) {
        OpenRtb.BidRequest.User.Builder user = OpenRtb.BidRequest.User.newBuilder();
        MeituOpenRtb.BidRequest.User meituUser = meituRequest.getUser();
        if (meituUser.hasId()) {
            user.setId(meituUser.getId());
        }
        if (meituUser.hasYob()) {
            user.setYob(meituUser.getYob());
        }
        if (meituUser.hasGender()) {
            user.setGender(meituUser.getGender());
        }
        if (meituUser.hasKeywords()) {
            user.setKeywords(meituUser.getKeywords());
        }
        return user;
    }

    private OpenRtb.BidRequest.App.Builder buildApp(MeituOpenRtb.BidRequest meituRequest) {
        OpenRtb.BidRequest.App.Builder app = OpenRtb.BidRequest.App.newBuilder();
        MeituOpenRtb.BidRequest.App meituRequestApp = meituRequest.getApp();
        if (meituRequestApp.hasId()) {
            app.setId(meituRequestApp.getId());
        }
        if (meituRequestApp.hasName()) {
            app.setName(meituRequestApp.getName());
        }
        if (meituRequestApp.hasDomain()) {
            app.setDomain(meituRequestApp.getDomain());
        }
        if (meituRequestApp.hasVer()) {
            app.setVer(meituRequestApp.getVer());
        }
        if (meituRequestApp.hasBundle()) {
            app.setBundle(meituRequestApp.getBundle());
        }

        return app;
    }

    private OpenRtb.BidRequest.Device.Builder buildDevice(MeituOpenRtb.BidRequest meituRequest) {
        OpenRtb.BidRequest.Device.Builder device = OpenRtb.BidRequest.Device.newBuilder();
        MeituOpenRtb.BidRequest.Device meituRequestDevice = meituRequest.getDevice();

        if (meituRequestDevice.hasUa()) {
            device.setUa(meituRequestDevice.getUa());
        }
        if (meituRequestDevice.hasIp()) {
            device.setIp(meituRequestDevice.getIp());
        }
        if (meituRequestDevice.hasDidsha1()) {
            device.setDidsha1(meituRequestDevice.getDidsha1());
        }
        if (meituRequestDevice.hasDidmd5()) {
            device.setDidmd5(meituRequestDevice.getDidmd5());
        }
        if (meituRequestDevice.hasDpidsha1()) {
            device.setDpidsha1(meituRequestDevice.getDpidsha1());
        }
        if (meituRequestDevice.hasDpidmd5()) {
            device.setDpidmd5(meituRequestDevice.getDpidmd5());
        }
        if (meituRequestDevice.hasCarrier()) {
            TelecomOperator telecomOperator = MeituCarrierTypeMapper.mapper.toMex(meituRequestDevice.getCarrier());
            device.setCarrier(telecomOperator.getValue());
        }
        if (meituRequestDevice.hasLanguage()) {
            device.setLanguage(meituRequestDevice.getLanguage());
        }
        if (meituRequestDevice.hasMake()) {
            device.setMake(meituRequestDevice.getMake());
        }
        if (meituRequestDevice.hasModel()) {
            device.setModel(meituRequestDevice.getModel());
        }
        if (meituRequestDevice.hasOs()) {
            device.setOs(meituRequestDevice.getOs());
        }
        if (meituRequestDevice.hasOsv()) {
            device.setOsv(meituRequestDevice.getOsv());
        }
        if (meituRequestDevice.hasHwv()) {
            device.setHwv(meituRequestDevice.getHwv());
        }
        if (meituRequestDevice.hasW()) {
            device.setW(meituRequestDevice.getW());
        }
        if (meituRequestDevice.hasH()) {
            device.setH(meituRequestDevice.getH());
        }
        if (meituRequestDevice.hasConnectiontype()) {
            device.setConnectiontype(MeituConnectionMapper.mapper.
                    toMex(meituRequestDevice.getConnectiontype()));
        }
        if (meituRequestDevice.hasDevicetype()) {
            // 默认是手机的流量
            OpenRtb.DeviceType deviceType = MeituDeviceTypeMapper.mapper.toMex(meituRequestDevice.getDevicetype());
            device.setDevicetype(deviceType);
        }
        if (meituRequestDevice.hasMacsha1()) {
            device.setMacsha1(meituRequestDevice.getMacsha1());
        }
        if (meituRequestDevice.hasMacmd5()) {
            device.setMacmd5(meituRequestDevice.getMacmd5());
        }
        return device;
    }

    private OpenRtb.BidRequest.Imp.Builder buildImp(MeituOpenRtb.BidRequest meituRequest) {
        MeituOpenRtb.BidRequest.Imp meituRequestImp = meituRequest.getImp(0);
        OpenRtb.BidRequest.Imp.Builder imp = OpenRtb.BidRequest.Imp.newBuilder();
        if (meituRequestImp.hasId()) {
            imp.setId(meituRequestImp.getId());
        }
        if (meituRequestImp.hasBidfloor()) {
            imp.setBidfloor(meituRequestImp.getBidfloor() / CURRENCY_UNIT);
        } else {
            imp.setBidfloor(0);
        }
        if (meituRequestImp.hasBidfloorcur()) {
            imp.setBidfloorcur(meituRequestImp.getBidfloorcur());
        }
        if (meituRequestImp.hasSecure()) {
            imp.setSecure(meituRequestImp.getSecure());
        }
        if (meituRequestImp.hasInstl()) {
            imp.setInstl(meituRequestImp.getInstl());
        }
        if (meituRequestImp.hasTagid()) {
            imp.setTagid(meituRequestImp.getTagid());
        }

        OpenRtb.BidRequest.Imp.Banner.Builder banner = buildBanner(meituRequestImp.getBanner());
        OpenRtb.BidRequest.Imp.Native aNative = buildNativeRequest(meituRequestImp.getNative());

        imp.setBanner(banner);
        imp.setNative(aNative);

        return imp;
    }

    private OpenRtb.BidRequest.Imp.Native buildNativeRequest(MeituOpenRtb.BidRequest.Imp.Native meituNative) {
        OpenRtb.BidRequest.Imp.Native.Builder aNative = OpenRtb.BidRequest.Imp.Native.newBuilder();

        if (meituNative.hasRequestNative()) {
            MeituOpenRtb.NativeRequest requestNative = meituNative.getRequestNative();
            OpenRtb.NativeRequest.Builder nativeRequest = OpenRtb.NativeRequest.newBuilder();

            if (requestNative.hasVer()) {
                aNative.setVer(requestNative.getVer());
            }

            if (requestNative.getAssetsCount() > 0) {


                for (MeituOpenRtb.NativeRequest.Asset meituAsset : requestNative.getAssetsList()) {
                    OpenRtb.NativeRequest.Asset.Builder asset = OpenRtb.NativeRequest.Asset.newBuilder();
                    if (meituAsset.hasId()) {
                        asset.setId(meituAsset.getId());
                    }
                    // title
                    if (meituAsset.hasTitle()) {
                        OpenRtb.NativeRequest.Asset.Title.Builder title = OpenRtb.NativeRequest.Asset.Title.newBuilder();
                        title.setLen(meituAsset.getTitle().getLen());
                        asset.setTitle(title);
                    }

                    // data
                    else if (meituAsset.hasData()) {
                        OpenRtb.NativeRequest.Asset.Data.Builder data = OpenRtb.NativeRequest.Asset.Data.newBuilder();
                        data.setLen(meituAsset.getData().getLen());
                        MeituOpenRtb.DataAssetType type = meituAsset.getData().getType();
                        data.setType(DataAssetTypeMapper.toMexDataAssetType(type));
                        asset.setData(data);
                    }

                    // img
                    else if (meituAsset.hasImg()) {
                        OpenRtb.NativeRequest.Asset.Image.Builder img = OpenRtb.NativeRequest.Asset.Image.newBuilder();
                        MeituOpenRtb.NativeRequest.Asset.Image meituImg = meituAsset.getImg();

                        img.setH(meituImg.getH());
                        img.setW(meituImg.getW());
                        img.setType(ImageAssetTypeMapper.toMexImageAssetType(meituImg.getType()));
                        if (meituImg.hasHmin()) {
                            img.setHmin(meituImg.getHmin());
                        }
                        if (meituImg.hasWmin()) {
                            img.setWmin(meituImg.getWmin());
                        }
                        if (meituImg.getMimesCount() > 0) {
                            img.addAllMimes(meituImg.getMimesList());
                        }

                        asset.setImg(img);
                    }
                    nativeRequest.addAssets(asset);
                }
            } else {
                // 没有 assert

            }

            aNative.setRequestNative(nativeRequest);
        } else {
            // native 为空

        }
        return aNative.build();
    }

    private OpenRtb.BidRequest.Imp.Banner.Builder buildBanner(MeituOpenRtb.BidRequest.Imp.Banner meituBanner) {
        OpenRtb.BidRequest.Imp.Banner.Builder banner = OpenRtb.BidRequest.Imp.Banner.newBuilder();
        if (meituBanner.hasId()) {
            banner.setId(meituBanner.getId());
        }
        if (meituBanner.hasW()) {
            banner.setW(meituBanner.getW());
        }
        if (meituBanner.hasH()) {
            banner.setH(meituBanner.getH());
        }
        //

        return banner;
    }

    static class ImageAssetTypeMapper {
        static OpenRtb.ImageAssetType toMexImageAssetType(MeituOpenRtb.ImageAssetType type) {
            switch (type) {
                case ICON:
                    return OpenRtb.ImageAssetType.ICON;
                case LOGO:
                    return OpenRtb.ImageAssetType.LOGO;
                case MAIN:
                    return OpenRtb.ImageAssetType.MAIN;
                default:
                    return null;
            }
        }
    }

    static class DataAssetTypeMapper {
        static OpenRtb.DataAssetType toMexDataAssetType(MeituOpenRtb.DataAssetType type) {
            switch (type) {
                case DESC:
                    return OpenRtb.DataAssetType.DESC;
                case SPONSORED:
                    return OpenRtb.DataAssetType.SPONSORED;
                case RATING:
                    return OpenRtb.DataAssetType.RATING;
                case LIKES:
                    return OpenRtb.DataAssetType.LIKES;
                case DOWNLOADS:
                    return OpenRtb.DataAssetType.DOWNLOADS;
                case PRICE:
                    return OpenRtb.DataAssetType.PRICE;
                case SALEPRICE:
                    return OpenRtb.DataAssetType.SALEPRICE;
                case PHONE:
                    return OpenRtb.DataAssetType.PHONE;
                case ADDRESS:
                    return OpenRtb.DataAssetType.ADDRESS;
                case DESC2:
                    return OpenRtb.DataAssetType.DESC2;
                case DISPLAYURL:
                    return OpenRtb.DataAssetType.DISPLAYURL;
                case CTATEXT:
                    return OpenRtb.DataAssetType.CTATEXT;
                default:
                    return null;
            }
        }
    }
}