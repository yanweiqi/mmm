package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Sets;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.DeviceIdTargetingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * 设置ID定向
 * user: donghai
 * date: 2017/2/24
 */
public class DeviceIdFilter implements SimpleAdFilter {
    private static final Logger logger = LoggerFactory.getLogger(DeviceIdFilter.class);
    @Inject
    private MexDataContext mexDataContext;

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        DeviceIdTargetingData deviceIdTargetingData = mexDataContext.getDeviceIdTargetingData();
        // 无设备定向，直接返回
        if (Objects.isNull(deviceIdTargetingData)) {
            return false;
        }

        // 获取ID
        Set<String> idSets = getDeviceIds(bidRequest.openRtb());

        boolean is = deviceIdTargetingData.isFilter(ad.getAdGroupId(), idSets);

        logger.info("requestid=" + bidRequest.openRtb().getId() + ", adgroupid=" + ad.getAdGroupId() + ", idSets=" + idSets.toArray());

        if (is) {
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_DevIdW);
            return true;
        } else {
            return false;
        }
    }

    static Set<String> getDeviceIds(OpenRtb.BidRequest bidRequest) {
        Set<String> idSets = Sets.newHashSet();
        if (!bidRequest.hasDevice()) {
            return Collections.emptySet();
        }


        OpenRtb.BidRequest.Device device = bidRequest.getDevice();
        if ((device.hasExtension(MexOpenRtbExt.imei)) && notNull(device.getExtension(MexOpenRtbExt.imei))) {
            //imei
            idSets.add(device.getExtension(MexOpenRtbExt.imei));
        }
        if (device.hasDidmd5() && notNull(device.getDidmd5())) {
            //imei_md5
            idSets.add(device.getDidmd5());
        }

        if (device.hasDidsha1() && notNull(device.getDidsha1())) {
            // imei_sha1
            idSets.add(device.getDidsha1());
        }

        if (device.hasExtension(MexOpenRtbExt.idfa) && notNull(device.getExtension(MexOpenRtbExt.idfa))) {
            // idfa
            idSets.add(device.getExtension(MexOpenRtbExt.idfa));
        }

        if (device.hasExtension(MexOpenRtbExt.androidId) && notNull(device.getExtension(MexOpenRtbExt.androidId))) {
            //android_id
            idSets.add(device.getExtension(MexOpenRtbExt.androidId));
        }

        if (device.hasDpidmd5() && notNull(device.getDpidmd5())) {
            // android_id_md5
            idSets.add(device.getDpidmd5());
        }

        if (device.hasDpidsha1() && notNull(device.getDpidsha1())) {
            // android_id_sha1
            idSets.add(device.getDpidsha1());
        }

        if (device.hasExtension(MexOpenRtbExt.idfa) && notNull(device.getExtension(MexOpenRtbExt.idfa))) {
            // adid
            idSets.add(device.getExtension(MexOpenRtbExt.idfa));
        }

        return idSets;
    }

    private static boolean notNull(String id) {
        return !Strings.isNullOrEmpty(id);
    }
}
