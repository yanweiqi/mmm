package com.mex.bidder.adx.sohu;

import com.google.common.base.Stopwatch;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.OpenRtbJsonReader;
import com.google.openrtb.json.OpenRtbJsonWriter;
import com.mex.bidder.api.mapper.OpenRtbMapper;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * xuchuahao
 * on 2017/3/22.
 */
public class SohuOpenRtbMapper implements OpenRtbMapper<SohuOpenRtb.Request, SohuOpenRtb.Response,
        SohuOpenRtb.Request.Builder, SohuOpenRtb.Response.Builder> {

    private static final Logger logger = LoggerFactory.getLogger(SohuOpenRtbMapper.class);

    private OpenRtbJsonWriter openRtbJsonWriter;
    private OpenRtbJsonReader openRtbJsonReader;

    private static final  int CURRENCY_UNIT = 100;

    public SohuOpenRtbMapper(){
       /* SohuOpenRtbJsonFactory factory = SohuOpenRtbJsonFactory.create();

        factory.register(new SohuResponseExtWriter.ImpTracker(), String.class, OpenRtb.BidResponse.SeatBid.Bid.class, "imptrackers");
        factory.register(new SohuResponseExtWriter.ClickTracker(), String.class, OpenRtb.BidResponse.SeatBid.Bid.class, "clktrackers");
        factory.register(new SohuResponseExtWriter.CURL(), String.class, OpenRtb.BidResponse.SeatBid.Bid.class, "landingpage");

        openRtbJsonWriter = factory.newWriter();
        openRtbJsonReader = factory.newReader();*/

    }



    @Override
    public SohuOpenRtb.Response.Builder toExchangeBidResponse(@Nullable OpenRtb.BidRequest request, OpenRtb.BidResponse response) {
        checkNotNull(request);
        Stopwatch stopwatch = Stopwatch.createStarted();
        // 无广告下发，直接返回空
        if (response.getSeatbidCount() == 0 || response.getSeatbid(0).getBidCount() == 0) {
            return SohuConst.emptyResponse(request, response);
        }

        SohuOpenRtb.Response.Builder sohuResponse = SohuOpenRtb.Response.newBuilder()
//                .setBidid(response.getBidid())
                .setBidid(request.getId())
                .setVersion(request.getExtension(MexOpenRtbExt.version));

        SohuOpenRtb.Response.SeatBid.Builder sohuSeatBid = SohuOpenRtb.Response.SeatBid.newBuilder();

        SohuOpenRtb.Response.Bid.Builder sohuBid = SohuOpenRtb.Response.Bid.newBuilder();

        OpenRtb.BidResponse.SeatBid.Bid bid = response.getSeatbid(0).getBid(0);


        sohuBid.setAdurl(bid.getAdm());
        sohuBid.setPrice(new Double(bid.getPrice() * CURRENCY_UNIT).intValue());
        sohuBid.setClickPara(bid.getExtension(MexOpenRtbExt.clktrackers).get(0));
        sohuBid.setDisplayPara(bid.getExtension(MexOpenRtbExt.imptrackers).get(0));
        sohuBid.setNurl(bid.getNurl());

        sohuSeatBid.addBid(sohuBid);

        sohuResponse.addSeatbid(sohuSeatBid);

        logger.info("sohu to openrtb bid  request time " + stopwatch);

        return sohuResponse;
    }


    @Override
    public OpenRtb.BidRequest.Builder toOpenRtbBidRequest(SohuOpenRtb.Request request) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        OpenRtb.BidRequest.Builder mexRequest = OpenRtb.BidRequest.newBuilder()
                .setId(request.getBidid())
                .setExtension(MexOpenRtbExt.version,request.getVersion());
        mexRequest.setExtension(MexOpenRtbExt.reqNetname, SohuExchange.ID);

        if (request.getImpressionCount() == 0) {
            logger.error("Request has no impressions");
            mexRequest.setExtension(MexOpenRtbExt.isOk, false);
            return mexRequest;
        }
        mexRequest.setAt(OpenRtb.AuctionType.SECOND_PRICE);

        OpenRtb.BidRequest.Imp.Builder imp = buildImp(request);
        if (null != imp) {
            mexRequest.addImp(imp);
        }

        OpenRtb.BidRequest.Device.Builder device = buildDevice(request);
        if (null != device) {
            mexRequest.setDevice(device);
        }

        OpenRtb.BidRequest.User.Builder user = buildUser(request);
        if (null != user) {
            mexRequest.setUser(user);
        }

//        OpenRtb.BidRequest.App.Builder app = buildApp(request);

        stopwatch.stop();
        logger.info("sohu to openrtb bid  request time " + stopwatch);
        return mexRequest;
    }


    private OpenRtb.BidRequest.User.Builder buildUser(SohuOpenRtb.Request request) {
        SohuOpenRtb.Request.User sohuUser = request.getUser();
        OpenRtb.BidRequest.User.Builder user = OpenRtb.BidRequest.User.newBuilder();
        // TODO

        return user;
    }

    private OpenRtb.BidRequest.Device.Builder buildDevice(SohuOpenRtb.Request request) {
        SohuOpenRtb.Request.Device sohuDevice = request.getDevice();
        OpenRtb.BidRequest.Device.Builder device = OpenRtb.BidRequest.Device.newBuilder();


        if (sohuDevice.hasType()) {
            String type = sohuDevice.getType().toUpperCase();
            SohuDeviceTypeMapper.SohuDeviceDic sohuDeviceType = SohuDeviceTypeMapper.SohuDeviceDic.valueOf(type);
            int val = sohuDeviceType.val;
            OpenRtb.DeviceType deviceType = SohuDeviceTypeMapper.mapper.toMex(val);
            device.setDevicetype(deviceType);
        }
        if (sohuDevice.hasNetType()) {
            String netType = sohuDevice.getNetType().toUpperCase();
            OpenRtb.ConnectionType connectionType = SohuNetTypeMapper.mapper.toMex(netType);
            device.setConnectiontype(connectionType);
        }
        if (sohuDevice.hasAndroidID()) {
            // TODO

        }
        if (sohuDevice.hasImei()) {
            device.setDidmd5(sohuDevice.getImei());
            device.setOs("ios");
        }
        if (sohuDevice.hasCarrier()) {
            device.setCarrier(sohuDevice.getCarrier());
        }
        if (sohuDevice.hasIp()) {
            device.setIp(sohuDevice.getIp());
        }
        if (sohuDevice.hasIdfa()) {
            device.setExtension(MexOpenRtbExt.idfa, sohuDevice.getIdfa());
            device.setOs("android");
        }

        if (sohuDevice.hasMac()) {
            device.setMacmd5(sohuDevice.getMac());
        }
        if (sohuDevice.hasOpenUDID()) {
            //TODO
        }

        return device;
    }

    private OpenRtb.BidRequest.Imp.Banner.Builder buildBanner(SohuOpenRtb.Request request) {
        SohuOpenRtb.Request.Impression.Banner sohuBanner = request.getImpression(0).getBanner();
        OpenRtb.BidRequest.Imp.Banner.Builder banner = OpenRtb.BidRequest.Imp.Banner.newBuilder();

        // template  TODO 特型模板。如果没有提供 format 字段，则此字段表示精 确要求的特型模板，否则表示 推荐的特型模板。

        if (sohuBanner.getFormatCount() != 0) {

            List<SohuOpenRtb.Request.Impression.Banner.Format> formatList = sohuBanner.getFormatList();

            formatList.forEach(sohuFormat -> {
                OpenRtb.BidRequest.Imp.Banner.Format.Builder format = OpenRtb.BidRequest.Imp.Banner.Format.newBuilder();
                if (sohuFormat.hasH() && sohuFormat.hasW()) {
                    format.setH(sohuFormat.getH());
                    format.setW(sohuFormat.getW());
                }
                banner.addFormat(format);
            });

        } else {
            if (sohuBanner.hasHeight() && sohuBanner.hasWidth()) {
                banner.setH(sohuBanner.getHeight());
                banner.setW(sohuBanner.getWidth());
            } else {
                throw new RuntimeException("sohu openRTB banner's width or height or format cannot be null, reqbidid=" + request.getBidid());
            }
        }

        if (sohuBanner.getMimesCount() != 0) {
            sohuBanner.getMimesList().forEach(sohuMime -> {
                banner.addMimes(sohuMime + "");
            });
        }


        return banner;
    }

    private OpenRtb.BidRequest.Imp.Builder buildImp(SohuOpenRtb.Request request) {
        SohuOpenRtb.Request.Impression.Builder sohuImp = request.getImpression(0).toBuilder();
        OpenRtb.BidRequest.Imp.Builder imp = OpenRtb.BidRequest.Imp.newBuilder();

        if (sohuImp.hasPid()){
            imp.setId(sohuImp.getPid());
        }
        if (sohuImp.hasBidFloor()) {
            imp.setBidfloor(sohuImp.getBidFloor()/CURRENCY_UNIT);
        }

        if (sohuImp.hasTradingType()) {
            logger.info("sohu tradingType is " + sohuImp.getTradingType());
        }

        OpenRtb.BidRequest.Imp.Banner.Builder banner = buildBanner(request);
        if (null != banner) {
            imp.setBanner(banner);
        }

        return imp;
    }
}
