package com.mex.bidder.adx.baidu;


import com.google.common.base.Stopwatch;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.mapper.OpenRtbMapper;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Baidu 请求相应映射
 * User: donghai
 * Date: 2016/11/17
 */
@Singleton
public class BaiduOpenRtbMapper implements OpenRtbMapper<BaiduRtb.BidRequest, BaiduRtb.BidResponse,
        BaiduRtb.BidRequest.Builder, BaiduRtb.BidResponse.Builder> {
    private static final Logger logger = LoggerFactory.getLogger(BaiduOpenRtbMapper.class);

    @Inject
    private BaiduDeviceMapper deviceMapper;
    @Inject
    private BaiduConnectionTypeMapper connectionTypeMapper;


    /**
     * 低价转换单位
     */
    private static final int CURRENCY_UNIT = 100;

    @Override
    public BaiduRtb.BidResponse.Builder toExchangeBidResponse(@Nullable OpenRtb.BidRequest request, OpenRtb.BidResponse response) {
        checkNotNull(request);

        Stopwatch stopwatch = Stopwatch.createStarted();
        BaiduRtb.BidResponse.Builder dcResponse = BaiduRtb.BidResponse.newBuilder();

        // id：返回信息的ID，这个ID要与request id一致
        dcResponse.setId(request.getId());

        if (response.hasBidid()) {
            dcResponse.setDebugString(response.getBidid());
        }

        for (OpenRtb.BidResponse.SeatBid seatBid : response.getSeatbidList()) {
            for (OpenRtb.BidResponse.SeatBid.Bid bid : seatBid.getBidList()) {
                dcResponse.addAd(buildResponseAd(request, response, bid));
            }
        }
        stopwatch.stop();

        return dcResponse;
    }

    protected BaiduRtb.BidResponse.Ad.Builder buildResponseAd(
            OpenRtb.BidRequest request, OpenRtb.BidResponse response, OpenRtb.BidResponse.SeatBid.Bid bid) {

        BaiduRtb.BidResponse.Ad.Builder dcAd = BaiduRtb.BidResponse.Ad.newBuilder();

        // dcAd.setSequenceId()  // TODO ??
        //  创意 ID
        dcAd.setCreativeId(Long.parseLong(bid.getCrid()));
        dcAd.setWidth(bid.getW());
        dcAd.setHeight(bid.getH());
        dcAd.setLandingPage(bid.getExtension(MexOpenRtbExt.landingpage)); // TODO ??

        if (bid.getExtensionCount(MexOpenRtbExt.imptrackers) != 0) {
            List<String> impTrackerList = bid.getExtension(MexOpenRtbExt.imptrackers);
            dcAd.addAllMonitorUrls(impTrackerList);
        }

        dcAd.setPreferredOrderId(bid.getDealid());

        dcAd.setMaxCpm((int) (bid.getPrice() * CURRENCY_UNIT));

        return dcAd;
    }


    //----------------------------------------------bid Request-------------------------------------------------------------

    @Override
    public OpenRtb.BidRequest.Builder toOpenRtbBidRequest(BaiduRtb.BidRequest baiduRequest) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        OpenRtb.BidRequest.Builder request = OpenRtb.BidRequest.newBuilder()
                .setId(baiduRequest.getId());

        // 请求 ID, 唯一标识本次请求，明文字符串
        request.setId(baiduRequest.getId());

        // 如果没有展示信息直接返回
        if (baiduRequest.getAdslotCount() == 0) {
            logger.info("Request has no impressions");
            request.setExtension(MexOpenRtbExt.isOk, false);
            return request;
        } else if (!baiduRequest.hasMobile()) {
            logger.info("Request has no mobile");
            request.setExtension(MexOpenRtbExt.isOk, false);
            return request;
        }

        request.setAt(OpenRtb.AuctionType.SECOND_PRICE);
        request.setTmax(130);

        // 目前只接一个广告位
        BaiduRtb.BidRequest.AdSlot dcSlot = baiduRequest.getAdslot(0);
        OpenRtb.BidRequest.Imp.Builder imp = buildImp(baiduRequest, dcSlot);
        if (imp != null) {
            request.addImp(imp);
        }

        // app信息
        OpenRtb.BidRequest.App app = buildApp(baiduRequest);
        if (app != null) {
            request.setApp(app);
        }

        // device 信息
        request.setDevice(buildDevice(baiduRequest));


        stopwatch.stop();
        logger.info("Baidu to openrtb bid  request time " + stopwatch);
        return request;

    }

    /**
     * 构建APP
     *
     * @param baiduRequest
     * @return
     */
    private OpenRtb.BidRequest.App buildApp(BaiduRtb.BidRequest baiduRequest) {
        BaiduRtb.BidRequest.Mobile mobile = baiduRequest.getMobile();
        if (mobile.hasMobileApp()) {
            OpenRtb.BidRequest.App.Builder app = OpenRtb.BidRequest.App.newBuilder();
            BaiduRtb.BidRequest.Mobile.MobileApp mobileApp = mobile.getMobileApp();
            if (mobileApp.hasAppId()) {
                app.setId(mobileApp.getAppId());
            }
            if (mobileApp.hasAppBundleId()) {
                app.setBundle(mobileApp.getAppBundleId());
            }
            if (mobileApp.hasAppCategory()) {
                // TODO 映射
                app.setCat(0, String.valueOf(mobileApp.getAppCategory()));
            }
            if (mobileApp.hasAppPublisherId()) {
                app.setPublisher(OpenRtb.BidRequest.Publisher.newBuilder()
                        .setId(String.valueOf(mobileApp.getAppPublisherId())));
            }

            return app.build();
        }

        return null;
    }

    protected OpenRtb.BidRequest.Device.Builder buildDevice(BaiduRtb.BidRequest baiduRequest) {
        OpenRtb.BidRequest.Device.Builder device = OpenRtb.BidRequest.Device.newBuilder();

        if (baiduRequest.hasUserAgent()) {
            device.setUa(baiduRequest.getUserAgent());
        }

        if (baiduRequest.hasIp()) {
            device.setIp(baiduRequest.getIp());
        }

        BaiduRtb.BidRequest.Mobile mobile = baiduRequest.getMobile();
        if (mobile.hasDeviceType()) {
            OpenRtb.DeviceType deviceType = deviceMapper.toMex(mobile.getDeviceType());
            if (Objects.nonNull(deviceType)) {
                device.setDevicetype(deviceMapper.toMex(mobile.getDeviceType()));
            }
        }
        if (mobile.hasBrand()) {
            device.setMake(mobile.getBrand());
        }

        if (mobile.hasModel()) {
            device.setModel(mobile.getModel());
        }
        if (mobile.hasPlatform()) {
            BaiduRtb.BidRequest.Mobile.OS platform = mobile.getPlatform();
            device.setOs(platform.getValueDescriptor().getName().toLowerCase());
        }

        if (mobile.hasOsVersion()) {
            BaiduRtb.BidRequest.Mobile.DeviceOsVersion osVersion = mobile.getOsVersion();
            device.setOsv(osVersion.getOsVersionMajor() + "." + osVersion.getOsVersionMinor()
                    + "." + osVersion.getOsVersionMicro());
        }

        if (mobile.hasScreenWidth()) {
            device.setW(mobile.getScreenWidth());
        }

        if (mobile.hasScreenHeight()) {
            device.setW(mobile.getScreenHeight());
        }

        if (mobile.hasScreenDensity()) {
            device.setPpi((int) mobile.getScreenDensity());
        }

        if (mobile.getForAdvertisingIdCount() != 0) {
            for (BaiduRtb.BidRequest.Mobile.ForAdvertisingID forAdvertisingID : mobile.getForAdvertisingIdList()) {
                if (forAdvertisingID.getType() == BaiduRtb.BidRequest.Mobile.ForAdvertisingID.IDType.ANDROID_ID) {
                    device.setExtension(MexOpenRtbExt.androidId, forAdvertisingID.getId());
                }
                if (forAdvertisingID.getType() == BaiduRtb.BidRequest.Mobile.ForAdvertisingID.IDType.IDFA) {
                    device.setExtension(MexOpenRtbExt.idfa, forAdvertisingID.getId());
                }
            }
        }

        if (mobile.hasCarrierId()) {
            //TODO 映射
            // device.setCarrier(mobile.getScreenHeight());
        }

        if (mobile.hasWirelessNetworkType()) {
            device.setConnectiontype(connectionTypeMapper.toMex(mobile.getWirelessNetworkType().getNumber()));
        }

        if (baiduRequest.hasDetectedLanguage()) {
            device.setLanguage(baiduRequest.getDetectedLanguage());
        }

        OpenRtb.BidRequest.Geo.Builder geo = buildGeo(baiduRequest);
        if (geo != null) {
            device.setGeo(geo);
        }

        return device;
    }

    protected OpenRtb.BidRequest.Imp.Builder buildImp(
            BaiduRtb.BidRequest dcRequest, BaiduRtb.BidRequest.AdSlot dcSlot) {

        OpenRtb.BidRequest.Imp.Builder imp = OpenRtb.BidRequest.Imp.newBuilder();
        // 展示ID，必须字段。
        imp.setId(String.valueOf(dcSlot.getAdBlockKey() + dcSlot.getSequenceId()));

        if (dcSlot.getCreativeTypeCount() != 0) {
            if (CreativeType.isBanner(dcSlot.getCreativeType(0))) {
                imp.setBanner(buildBanner(dcSlot));
            } else {
                // 其他类型的广告
                return null;
            }
        } else {
            // 没有广告类型
            return null;
        }

        if (dcSlot.hasMinimumCpm()) {
            imp.setBidfloor(dcSlot.getMinimumCpm() / CURRENCY_UNIT);
        } else {
            imp.setBidfloor(0);
        }


        return imp;
    }


    protected OpenRtb.BidRequest.Imp.Banner.Builder buildBanner(BaiduRtb.BidRequest.AdSlot dcSlot) {
        OpenRtb.BidRequest.Imp.Banner.Builder banner = OpenRtb.BidRequest.Imp.Banner.newBuilder();

        if (dcSlot.hasWidth()) {
            banner.setW(dcSlot.getWidth());
        }
        if (dcSlot.hasHeight()) {
            banner.setH(dcSlot.getHeight());
        }

        return banner;
    }

    /**
     * 构建地理位置
     *
     * @param baiduRequest
     * @return
     */
    private OpenRtb.BidRequest.Geo.Builder buildGeo(BaiduRtb.BidRequest baiduRequest) {
        if (baiduRequest.hasUserGeoInfo() && baiduRequest.getUserGeoInfo().getUserCoordinateCount() > 0) {
            BaiduRtb.BidRequest.Geo.Coordinate userCoordinate = baiduRequest.getUserGeoInfo().getUserCoordinate(0);
            OpenRtb.BidRequest.Geo.Builder geo = OpenRtb.BidRequest.Geo.newBuilder();
            geo.setLat(userCoordinate.getLatitude());
            geo.setLon(userCoordinate.getLongitude());
        }
        return null;
    }


}
