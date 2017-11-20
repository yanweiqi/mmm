package com.mex.bidder.adx.gy;

import com.google.api.client.util.Lists;
import com.google.common.base.Stopwatch;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidRequest.Device;
import com.google.openrtb.OpenRtb.BidRequest.Geo;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.mex.bidder.api.mapper.OpenRtbMapper;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.constants.TelecomOperator;
import com.mex.bidder.engine.util.MexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Gy 请求相应映射
 * User: donghai
 * Date: 2016/11/17
 */
@Singleton
public class GyOpenRtbMapper implements OpenRtbMapper<GyOpenRtb.BidRequest, GyOpenRtb.BidResponse,
        GyOpenRtb.BidRequest.Builder, GyOpenRtb.BidResponse.Builder> {
    private static final Logger logger = LoggerFactory.getLogger(GyOpenRtbMapper.class);

    @Inject
    private GyDeviceMapper deviceMapper;
    @Inject
    private GyConnectionTypeMapper connectionTypeMapper;

    //    private static final String ADM = "{\"src\":\"${src}\",title:\"\",\"text\":\"\",\"icon\":\"\"}";
    private static final String ADM = "{\"src\":\"${src}\",\"title\":\"\",\"text\":\"\",\"icon\":\"\"}";


    /**
     * 低价转换单位
     */
    private static final int CURRENCY_UNIT = 100;

    @Override
    public GyOpenRtb.BidResponse.Builder toExchangeBidResponse(@Nullable OpenRtb.BidRequest request, OpenRtb.BidResponse response) {
        checkNotNull(request);
        Stopwatch stopwatch = Stopwatch.createStarted();

        // 无广告下发，直接返回空
        if (response.getSeatbidCount() == 0 || response.getSeatbid(0).getBidCount() == 0) {
            //debugInfo 信息
            //gen.writeStringField("debugInfo", response.getExtension(MexOpenRtbExt.debugInfo));
            return GyConst.emptyResponse(request, response);
        }

        GyOpenRtb.BidResponse.Builder gyResponse = GyOpenRtb.BidResponse.newBuilder();

        gyResponse.setId(request.getId());

        OpenRtb.BidResponse.SeatBid.Bid mexBid = response.getSeatbid(0).getBid(0);

        GyOpenRtb.Bid.Builder gyBidBuilder = GyOpenRtb.Bid.newBuilder();

        gyBidBuilder.setId(mexBid.getId());                              // TODO mex uuid
        gyBidBuilder.setImpid(mexBid.getImpid());                        // 曝光 id
        gyBidBuilder.setPrice((float) (mexBid.getPrice() * CURRENCY_UNIT));        // 出价，单位为分单价变更
//        logger.info("mex bidprice = " + mexBid.getPrice() + " ，乘积 = " + (mexBid.getPrice() * CURRENCY_UNIT)
//                + ",gy response price = " + gyBidBuilder.getPrice());
        gyBidBuilder.setAdid(mexBid.getAdid());                          // 物料 ID

        gyBidBuilder.setNurl(mexBid.getNurl());
        if (mexBid.hasBundle()) {
            gyBidBuilder.setBundle(mexBid.getBundle());
        }

        gyBidBuilder.setIurl(mexBid.getIurl());
        gyBidBuilder.setW(mexBid.getW());
        gyBidBuilder.setH(mexBid.getH());
        // {"src":"",title:"",text":"","icon":""}

        gyBidBuilder.setAdm(ADM.replace("${src}", mexBid.getAdm()));
        gyBidBuilder.setAdomain(mexBid.getAdomain(0));
        gyBidBuilder.setCurl(mexBid.getExtension(MexOpenRtbExt.clktrackers).get(0));
        gyBidBuilder.setType(GyOpenRtb.AdType.IMAGE);
        gyBidBuilder.setAdmtype(GyOpenRtb.AdmType.JSON);
        gyBidBuilder.setCid(mexBid.getCid());
        gyBidBuilder.setCrid(mexBid.getCrid());

        gyResponse.setBidid(MexUtil.uuid());

        gyResponse.addSeatbid(GyOpenRtb.SeatBid.newBuilder().setSeat("dspid").addBid(gyBidBuilder.build()));

        stopwatch.stop();
        logger.info("Gy to openrtb bid  request time " + stopwatch);

        return gyResponse;
    }

    @Override
    public OpenRtb.BidRequest.Builder toOpenRtbBidRequest(GyOpenRtb.BidRequest gyRequest) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        OpenRtb.BidRequest.Builder mexRequest = OpenRtb.BidRequest.newBuilder()
                .setId(gyRequest.getId());
        mexRequest.setExtension(MexOpenRtbExt.reqNetname, GyExchange.ID);


        // 如果没有展示信息直接返回
        if (gyRequest.getImpCount() == 0) {
            // noImp.inc();
            logger.debug("Request has no impressions");
            mexRequest.setExtension(MexOpenRtbExt.isOk, false);
            return mexRequest;
        }

        mexRequest.setAt(OpenRtb.AuctionType.SECOND_PRICE);
        mexRequest.setTmax(gyRequest.getTmax());

        Imp.Builder imp = buildImp(gyRequest);
        if (imp != null) {
            mexRequest.addImp(imp);
        }


        mexRequest.setDevice(buildDevice(gyRequest));

        OpenRtb.BidRequest.App.Builder app = buildApp(gyRequest);
        if (app != null) {
            mexRequest.setApp(app);
        }

        OpenRtb.BidRequest.User.Builder user = buildUser(gyRequest);
        if (user != null) {
            mexRequest.setUser(user);
        }

        stopwatch.stop();
        logger.info("Gy to openrtb bid  request time " + stopwatch);
        return mexRequest;

    }

    protected Imp.Builder buildImp(GyOpenRtb.BidRequest gyRequest) {
        GyOpenRtb.Imp gyRequestImp = gyRequest.getImp(0);
        Imp.Builder imp = Imp.newBuilder();
        if (gyRequestImp.hasId()) {

            imp.setId(gyRequestImp.getId());
        }
        if (gyRequestImp.hasBidfloor()) {

            imp.setBidfloor(gyRequestImp.getBidfloor() / CURRENCY_UNIT);
        } else {
            imp.setBidfloor(0);
        }
//                .setBidfloorcur(gyRequestImp.getBidfloorcur())
//                .setInstl(gyRequestImp.getInstl());

        if (gyRequestImp.hasBanner()) {
            imp.setBanner(buildBanner(gyRequest.getImp(0).getBanner()));
        }

        return imp;
    }

    protected Imp.Banner.Builder buildBanner(GyOpenRtb.Banner gyBanner) {
        Imp.Banner.Builder banner = Imp.Banner.newBuilder();
        if (gyBanner.hasW()) {
            banner.setW(gyBanner.getW());
        }
        if (gyBanner.hasH()) {
            banner.setH(gyBanner.getH());
        }
        if (gyBanner.hasPos()) {
            banner.setPos(OpenRtb.AdPosition.valueOf(gyBanner.getPos().getNumber()));
        }

        return banner;
    }


    protected Imp.Video.Builder buildVideo(Imp.Video gyVideo) {
        Imp.Video.Builder video = Imp.Video.newBuilder();
        //TODO
        return video;
    }

    protected Device.Builder buildDevice(GyOpenRtb.BidRequest gyBidRequest) {
        Device.Builder device = Device.newBuilder();
        if (!gyBidRequest.hasDevice()) {
            return device;
        }

        GyOpenRtb.Device gyDevice = gyBidRequest.getDevice();

        if (gyDevice.hasOs()) {
            device.setOs(gyDevice.getOs());
        }
//        device.setDnt(gyDevice.getDnt());
        if (gyDevice.hasOsv()) {
            device.setOsv(gyDevice.getOsv());
        }
        if (gyDevice.hasMake()) {
            device.setMake(gyDevice.getMake());
        }
        if (gyDevice.hasIp()) {
            device.setIp(gyDevice.getIp());
        }
        if (gyDevice.hasUa()) {
            device.setUa(gyDevice.getUa());
        }

        if (gyDevice.hasHwv()) {
            device.setHwv(gyDevice.getHwv());
        }
        if (gyDevice.hasW()) {
            device.setW(gyDevice.getW());
        }
        if (gyDevice.hasH()) {
            device.setH(gyDevice.getH());
        }
        if (gyDevice.hasMac()) {
            device.setMacsha1(gyDevice.getMac());
        }
        if (gyDevice.hasIdfa()) {
            device.setIfa(gyDevice.getIdfa());
        }

        if (gyDevice.hasImei()) {
            device.setExtension(MexOpenRtbExt.imei, gyDevice.getImei());
        }
        if (gyDevice.hasAndroidid()) {
            device.setExtension(MexOpenRtbExt.androidId, gyDevice.getAndroidid());
        }
        if (gyDevice.hasConnectiontype()) {
            device.setConnectiontype(connectionTypeMapper.toMex(gyDevice.getConnectiontype().getNumber()));
        }
        if (gyDevice.hasDevicetype()) {
            OpenRtb.DeviceType deviceType = deviceMapper.toMex(gyDevice.getDevicetype().getNumber());
            if (Objects.nonNull(deviceType)) {
                device.setDevicetype(deviceType);
            }
        }
        Geo.Builder geo = buildGeo(gyDevice);
        if (geo != null) {
            device.setGeo(geo);
        }

        if (gyDevice.hasCarrier()) {
            TelecomOperator telecomOperator = GyCarrierTypeMapper.mapper.toMex(gyDevice.getCarrier());
            device.setCarrier(telecomOperator.getValue());
            logger.info("gy carrier=" + telecomOperator.getValue() + ", reqid=" + gyBidRequest.getId());
        }


        return device;
    }


    protected Geo.Builder buildGeo(GyOpenRtb.Device gyDevice) {

        Geo.Builder geo = Geo.newBuilder();
        GyOpenRtb.Geo geo1 = gyDevice.getGeo();

        if (geo1.hasLat()) {
            geo.setLat(geo1.getLat());
        }
        if (geo1.hasLon()) {
            geo.setLon(geo1.getLon());
        }
        if (geo1.hasType()) {
            geo.setType(OpenRtb.LocationType.valueOf(geo1.getType().getNumber()));
        }
        if (geo1.hasCountry()) {
            geo.setCountry(geo1.getCountry());
        }
        if (geo1.hasCity()) {
            geo.setCity(geo1.getCity());
        }
        if (geo1.hasZip()) {
            geo.setZip(geo1.getZip());
        }
        if (geo1.hasProvince()) {
            geo.setRegion(geo1.getProvince());
        }
        if (geo1.hasUtcoffset()) {
            geo.setUtcoffset(geo1.getUtcoffset());
        }

        // TODO
        return geo;
    }

    protected OpenRtb.BidRequest.App.Builder buildApp(GyOpenRtb.BidRequest gyRequest) {
        if (!gyRequest.hasApp()) {
            return null;
        }

        OpenRtb.BidRequest.App.Builder app = OpenRtb.BidRequest.App.newBuilder();
        List<String> list = Lists.newArrayList();
        GyOpenRtb.App gyApp = gyRequest.getApp();
        if (gyApp.hasCat()) {
            list.add(gyApp.getCat().getNumber() + "");
        }
        if (gyApp.hasPagecat()) {
            list.add(gyApp.getPagecat().getNumber() + "");
        }
        if (gyApp.hasSectioncat()) {
            list.add(gyApp.getSectioncat().getNumber() + "");
        }
        app.setId(gyApp.getId())
                .setName(gyApp.hasName() ? gyApp.getName() : "")
                .setVer(gyApp.hasVer() ? gyApp.getVer() : "")
                .setBundle(gyApp.hasBundle() ? gyApp.getBundle() : "")
                .addAllCat(list)
        ;
        return app;
    }

    protected
    @Nullable
    OpenRtb.BidRequest.User.Builder buildUser(GyOpenRtb.BidRequest gyRequest) {
        if (!gyRequest.hasUser()) {
            return null;
        }
        OpenRtb.BidRequest.User.Builder builder = OpenRtb.BidRequest.User.newBuilder();

        if (gyRequest.hasUser()) {
            GyOpenRtb.User user = gyRequest.getUser();
            if (user.hasId()) {
                builder.setId(user.getId());
            }
            if (user.hasYob()) {
                builder.setYob(user.getYob());
            }
            if (user.hasGender()) {
                builder.setGender(user.getGender());
            }

            if (user.getKeywordsCount() > 0) {
                builder.setKeywords(user.getKeywords(0));
            }
        }

        if (gyRequest.hasDevice()) {
            if (gyRequest.getDevice().hasGeo()) {
                builder.setGeo(buildGeo(gyRequest.getDevice()));
            }
        }


        // TODO
        return builder;
    }
}
