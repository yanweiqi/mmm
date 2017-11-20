package com.mex.bidder.engine.filter.impl;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xuchuanao
 * on 2017/2/22.
 */
public class HttpsFilter implements SimpleAdFilter {

    private static final Logger logger = LoggerFactory.getLogger(HttpsFilter.class);

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        String id = bidRequest.getExchange().getId();
        boolean channelHttps = bidRequest.openRtb().getImp(0).getSecure();
        boolean adHttps = ad.getIsHttps();
//        boolean channelHttps = bidRequest.openRtb().getExtension(MexOpenRtbExt.channelHttps);
        if ("adsiflytek".equals(id)) {
            if (channelHttps == adHttps) {
                // 渠道和广告的落地页，都是https或者http
                logger.info("adsiflytek http pass, channel channelHttps=" + channelHttps + ", ad adHttps=" + adHttps + ", groupId" + ad.getAdGroupId());
                return false;
            }

           /* logger.info("adsiflytek http pass, channel channelHttps=" + channelHttps + ", ad adHttps=" + adHttps + ", groupId" + ad.getAdGroupId());
            return false;*/
        } else if ("adszp".equals(id)) {
            OpenRtb.BidRequest.Device device =
                    bidRequest.openRtb().getDevice();
            // zplay的need_https字段子扩展字段中
            channelHttps = bidRequest.openRtb().getExtension(MexOpenRtbExt.needHttps);
            if (adHttps) {
                // 素材 https
                if (device.hasOs()) {
                    if ("ios".equals(device.getOs().toLowerCase())) {
                        // os = ios;全部支持https
                        logger.info("adszp ios http pass, " + commonLog(ad, bidRequest, channelHttps, adHttps));
                        return false;
                    } else if (channelHttps && "android".equals(device.getOs().toLowerCase())) {
                        // 渠道 https ，os = android
                        logger.info("adszp android http pass, " + commonLog(ad, bidRequest, channelHttps, adHttps));
                        return false;
                    } else {
                        // 渠道 http ，os = android
                        logger.info("adszp android http filter, " + commonLog(ad, bidRequest, channelHttps, adHttps));
                    }

                } else {
                    logger.info("adszp nodevice http filter, " + commonLog(ad, bidRequest, channelHttps, adHttps));
                }
            } else {
                if (channelHttps) {
                    // 渠道 https ; 素材 http
                    logger.info("adszp http filter, " + commonLog(ad, bidRequest, channelHttps, adHttps));
                } else {
                    // 渠道 http ； 素材 http
                    logger.info("adszp http pass, " + commonLog(ad, bidRequest, channelHttps, adHttps));
                    return false;
                }
            }

        } else if ("adsview".equals(id)) {
            if (bidRequest.openRtb().getImpCount() > 0 && bidRequest.openRtb().getImp(0).hasSecure()) {
                if (channelHttps == adHttps) {
                    // 渠道和广告的落地页，都是https或者http
                    logger.info("adsview http pass, " + commonLog(ad, bidRequest, channelHttps, adHttps));
                    return false;
                }
            } else {
                logger.info("adsview http pass, req secure is null, reqestId=" + bidRequest.openRtb().getId());
                return false;
            }
        } else if (Constants.GY_ID.equals(id)) {
            return false;
        } else if (Constants.SOHU_ID.equals(id)) {
            return false;
        } else if (Constants.BAIDU_ID.equals(id)) {
            return false;
        } else if (Constants.MEITU_ID.equals(id)) {
            return false;
        }

        // HTTPS 不匹配
        bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_HTTPS);
        return true;
    }

    private String commonLog(Ad ad, BidRequest bidRequest, boolean needHttps, boolean isHttps) {
        String log = "channel needHttps=" + needHttps + ", ad isHttps=" + isHttps
                + ", groupId" + ad.getAdGroupId() + ", reqestId=" + bidRequest.openRtb().getId();
        return log;
    }
}