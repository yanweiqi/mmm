package com.mex.bidder.adx.zplay;

import com.google.common.base.Stopwatch;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidRequest.Device;
import com.google.openrtb.OpenRtb.BidRequest.Geo;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.mex.bidder.api.mapper.OpenRtbMapper;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Zplay 请求相应映射
 * User: donghai
 * Date: 2016/11/17
 */
@Singleton
public class ZplayOpenRtbPbMapper implements OpenRtbMapper<OpenRtb.BidRequest, OpenRtb.BidResponse,
        OpenRtb.BidRequest.Builder, OpenRtb.BidResponse.Builder> {
    private static final Logger logger = LoggerFactory.getLogger(ZplayOpenRtbPbMapper.class);

    @Inject
    private ZplayDeviceMapper deviceMapper;
    @Inject
    private ZplayConnectionTypeMapper connectionTypeMapper;


    /**
     * 低价转换单位
     */
    private static final int CURRENCY_UNIT = 100;

    @Override
    public OpenRtb.BidResponse.Builder toExchangeBidResponse(@Nullable OpenRtb.BidRequest request, OpenRtb.BidResponse response) {
        checkNotNull(request);
        Stopwatch stopwatch = Stopwatch.createStarted();

        // 无广告下发，直接返回空
        if (response.getSeatbidCount() == 0 || response.getSeatbid(0).getBidCount() == 0) {
            return ZplayConst.emptyResponse(request);
        }

        OpenRtb.BidResponse.Builder zplayResponse = OpenRtb.BidResponse.newBuilder();
        OpenRtb.BidResponse.SeatBid.Bid mexBid = response.getSeatbid(0).getBid(0);

        OpenRtb.BidResponse.SeatBid.Bid.Builder zplayBidBuilder = OpenRtb.BidResponse.SeatBid.Bid.newBuilder();
        zplayBidBuilder.setId(mexBid.getId());                              // mex uuid
        zplayBidBuilder.setImpid(mexBid.getImpid());                        // 曝光 id
        zplayBidBuilder.setPrice(mexBid.getPrice() * CURRENCY_UNIT);        // 出价，单位为分单价变更
        zplayBidBuilder.setAdid(mexBid.getAdid());                          // 物料 ID

        zplayBidBuilder.setNurl(mexBid.getNurl());
        zplayBidBuilder.setBundle(mexBid.getBundle());
        zplayBidBuilder.setIurl(mexBid.getIurl());
        zplayBidBuilder.setW(mexBid.getW());
        zplayBidBuilder.setH(mexBid.getH());
        zplayBidBuilder.setAdm(mexBid.getAdm());

        zplayBidBuilder.setExtension(ZadxExt.clkurl, mexBid.getExtension(MexOpenRtbExt.landingpage));
        zplayBidBuilder.setExtension(ZadxExt.imptrackers, mexBid.getExtension(MexOpenRtbExt.imptrackers));
        zplayBidBuilder.setExtension(ZadxExt.clktrackers, mexBid.getExtension(MexOpenRtbExt.clktrackers));

        zplayResponse.addSeatbid(OpenRtb.BidResponse.SeatBid.newBuilder().addBid(zplayBidBuilder.build()));

        stopwatch.stop();
        logger.info("Zplay to openrtb bid  request time " + stopwatch);

        return zplayResponse;
    }

    @Override
    public OpenRtb.BidRequest.Builder toOpenRtbBidRequest(OpenRtb.BidRequest zplayRequest) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        OpenRtb.BidRequest.Builder request = OpenRtb.BidRequest.newBuilder()
                .setId(zplayRequest.getId());

        // ping请求直接返回
        if (zplayRequest.getExtension(ZadxExt.isPing)) {
            return request;
        }

        // 如果没有展示信息直接返回
        if (zplayRequest.getImpCount() == 0) {
            // noImp.inc();
            logger.debug("Request has no impressions");
            request.setExtension(MexOpenRtbExt.isOk, false);
            return request;
        }

        request.setAt(OpenRtb.AuctionType.SECOND_PRICE);
        request.setTmax(zplayRequest.getTmax());

        Imp.Builder imp = buildImp(zplayRequest);
        if (imp != null) {
            request.addImp(imp);
        }


        request.setDevice(buildDevice(zplayRequest));

        request.setApp(buildApp(zplayRequest));

        OpenRtb.BidRequest.User.Builder user = buildUser(zplayRequest);
        if (user != null) {
            request.setUser(user);
        }

        stopwatch.stop();
        logger.info("Zplay to openrtb bid  request time " + stopwatch);
        return request;

    }

    protected Imp.Builder buildImp(OpenRtb.BidRequest zplayRequest) {
        Imp zplayRequestImp = zplayRequest.getImp(0);
        Imp.Builder imp = Imp.newBuilder().setId(zplayRequestImp.getId())
                .setBidfloor(zplayRequestImp.getBidfloor() / CURRENCY_UNIT)
                .setBidfloorcur(zplayRequestImp.getBidfloorcur())
                .setInstl(zplayRequestImp.getInstl());

        if (zplayRequestImp.hasBanner()) {
            imp.setBanner(buildBanner(zplayRequest.getImp(0).getBanner()));
        } else if (zplayRequestImp.hasVideo()) {
            imp.setVideo(buildVideo(zplayRequest.getImp(0).getVideo()));
        } else {
            // imp.setNative();
        }

        return imp;
    }

    protected Imp.Banner.Builder buildBanner(OpenRtb.BidRequest.Imp.Banner zpBanner) {
        Imp.Banner.Builder banner = Imp.Banner.newBuilder()
                .setW(zpBanner.getW())
                .setH(zpBanner.getH())
                .setW(zpBanner.getH())
                .setH(zpBanner.getW())
                .setPos(zpBanner.getPos())
                .addAllBattr(zpBanner.getBattrList());
        return banner;
    }

    protected Imp.Video.Builder buildVideo(OpenRtb.BidRequest.Imp.Video zpVideo) {
        Imp.Video.Builder video = Imp.Video.newBuilder();
        //TODO
        return video;
    }

    protected Device.Builder buildDevice(OpenRtb.BidRequest zplayBidRequest) {
        Device.Builder device = Device.newBuilder();
        if (!zplayBidRequest.hasDevice()) {
            return device;
        }

        OpenRtb.BidRequest.Device zplayDevice = zplayBidRequest.getDevice();

        device.setOs(zplayDevice.getOs());
        device.setDnt(zplayDevice.getDnt());
        device.setOsv(zplayDevice.getOsv());
        device.setMake(zplayDevice.getMake());
        device.setIp(zplayDevice.getIp());
        device.setUa(zplayDevice.getUa());

        device.setHwv(zplayDevice.getHwv());
        device.setW(zplayDevice.getW());
        device.setH(zplayDevice.getH());
        device.setMacsha1(zplayDevice.getMacsha1());
        device.setDidsha1(zplayDevice.getDidsha1());
        device.setDpidsha1(zplayDevice.getDpidsha1());
        device.setConnectiontype(connectionTypeMapper.toMex(zplayDevice.getConnectiontype().getNumber()));
        OpenRtb.DeviceType deviceType = deviceMapper.toMex(zplayDevice.getDevicetype().getNumber());
        logger.info("zplay devicetype="+zplayDevice.getDevicetype().getNumber()+", mex deviceType="+deviceType);
        if (Objects.nonNull(deviceType)){
            device.setDevicetype(deviceType);
        }

        // openrtb ext
        device.setExtension(MexOpenRtbExt.mac, zplayDevice.getExtension(ZadxExt.mac));
        device.setExtension(MexOpenRtbExt.imei, zplayDevice.getExtension(ZadxExt.imei));
        device.setExtension(MexOpenRtbExt.androidId, zplayDevice.getExtension(ZadxExt.androidId));
        device.setExtension(MexOpenRtbExt.idfa, zplayDevice.getExtension(ZadxExt.adid));

        Geo.Builder geo = buildGeo(zplayDevice);
        if (geo != null) {
            device.setGeo(geo);
        }

        return device;
    }

    protected
    @Nullable
    Geo.Builder buildGeo(OpenRtb.BidRequest.Device zplayDevice) {
        if (!zplayDevice.hasGeo()) {
            return null;
        }

        Geo.Builder geo = Geo.newBuilder();
        // TODO
        return geo;
    }

    protected OpenRtb.BidRequest.App.Builder buildApp(OpenRtb.BidRequest zplayRequest) {
        OpenRtb.BidRequest.App.Builder app = OpenRtb.BidRequest.App.newBuilder();
        app.setId(zplayRequest.getApp().getId())
                .setName(zplayRequest.getApp().getName())
                .setVer(zplayRequest.getApp().getVer())
                .setBundle(zplayRequest.getApp().getBundle())
                .addAllCat(zplayRequest.getApp().getCatList());
        return app;
    }

    protected
    @Nullable
    OpenRtb.BidRequest.User.Builder buildUser(OpenRtb.BidRequest zplayRequest) {
        if (!zplayRequest.hasUser()) {
            return null;
        }
        OpenRtb.BidRequest.User.Builder builder = OpenRtb.BidRequest.User.newBuilder();
        // TODO
        return builder;
    }
}
