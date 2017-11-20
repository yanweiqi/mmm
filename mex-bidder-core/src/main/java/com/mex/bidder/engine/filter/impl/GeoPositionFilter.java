package com.mex.bidder.engine.filter.impl;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.engine.util.MexUtil;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.GeoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * xuchuahao
 * on 2017/3/30.
 */
public class GeoPositionFilter implements SimpleAdFilter {

    private static final Logger logger = LoggerFactory.getLogger(GeoPositionFilter.class);

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        OpenRtb.BidRequest.Geo geo = bidRequest.openRtb().getDevice().getGeo();
        double reqLat = geo.getLat();
        double reqLng = geo.getLon();


        logger.info("geoPositonFilter >> groupid="+ad.getAdGroupId()+", requestid="+bidRequest.openRtb().getId());
        List<GeoData> geoDataList = ad.getGeoDataList();

        if (Objects.isNull(geoDataList) || geoDataList.size() == 0){
            return false;
        }

        for (GeoData geoData : geoDataList) {
            double adLat = geoData.getLat();
            double adLng = geoData.getLng();
            double radius = geoData.getRadius();
            double distance = MexUtil.getDistance(reqLat, reqLng, adLat, adLng);
            logger.info("request [reqLat=" + reqLat + ", reqLng=" + reqLng + "], ad [adLat=" + adLat + ", adLng=" + adLng + "], radius=" + radius);
            if (radius > distance) {
                return false;
            }
        }

        bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_GEO_POSITION);
        return true;
    }
}

