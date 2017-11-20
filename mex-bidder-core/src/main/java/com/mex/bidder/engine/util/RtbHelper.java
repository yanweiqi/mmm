package com.mex.bidder.engine.util;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.OpenRtbJsonFactory;
import com.google.openrtb.json.OpenRtbJsonWriter;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.api.openrtb.json.MexOpenRtbJsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * User: donghai
 * Date: 2016/11/17
 */
public class RtbHelper {
    private static final Logger logger = LoggerFactory.getLogger(RtbHelper.class);

    public static final String pakageName;//MexOpenRtb的包名，用于去除原始json在打印的时候key会自动带上包名


    public static final String NO_SIZE = "";
    private static final OpenRtbJsonFactory jsonFactory = MexOpenRtbJsonFactory.create();
    public static final OpenRtbJsonWriter openRtbJsonWriter = jsonFactory.newWriter();

    static {
        pakageName = MexOpenRtbExt.getDescriptor().getPackage() + ".";
    }


    public static String getBannerSize(OpenRtb.BidRequest.Imp.Banner banner) {

        if (banner.hasH() && banner.hasW()) {
            return banner.getW() + "*" + banner.getH();
        } else {
            return NO_SIZE;
        }
    }

    public static String getVideoSize(OpenRtb.BidRequest.Imp.Video video) {

        if (video.hasH() && video.hasW()) {
            return video.getW() + "*" + video.getH();
        } else {
            return NO_SIZE;
        }
    }

    public static String getIp(OpenRtb.BidRequest bidRequest) {
        return (bidRequest.getImpCount() > 0 && bidRequest.hasDevice()) ? bidRequest.getDevice().getIp() : "";
    }

    public static boolean hasBanner(OpenRtb.BidRequest bidRequest) {
        return bidRequest.getImpCount() > 0 && bidRequest.getImp(0).hasBanner();
    }

    public static boolean hasVideo(OpenRtb.BidRequest bidRequest) {
        return bidRequest.getImpCount() > 0 && bidRequest.getImp(0).hasVideo();
    }

    public static boolean hasNative(OpenRtb.BidRequest bidRequest) {
        return bidRequest.getImpCount() > 0 && bidRequest.getImp(0).hasNative();
    }

    public static String openRtbBidRequestToJson(OpenRtb.BidRequest bidRequest) {
        try {
            return openRtbJsonWriter.writeBidRequest(bidRequest);
        } catch (IOException e) {
            logger.error("bidRequest request serialize error", e);
            return "";
        }
    }

    public static String openRtbBidResponseToJson(OpenRtb.BidResponse response) {
        try {
            return openRtbJsonWriter.writeBidResponse(response);
        } catch (IOException e) {
            logger.error("bidRequest request serialize error", e);
            return "";
        }
    }

    public static String getDeviceId(BidRequest bidRequest) {
        OpenRtb.BidRequest.Device device = bidRequest.openRtb().getDevice();
        if ((device.hasExtension(MexOpenRtbExt.imei)) && notNull(device.getExtension(MexOpenRtbExt.imei))) {
            //imei
            return device.getExtension(MexOpenRtbExt.imei);
        } else if (device.hasDidmd5() && notNull(device.getDidmd5())) {
            //imei_md5
            return device.getDidmd5();
        } else if (device.hasDidsha1() && notNull(device.getDidsha1())) {
            // imei_sha1
            return device.getDidsha1();
        } else if (device.hasExtension(MexOpenRtbExt.idfa) && notNull(device.getExtension(MexOpenRtbExt.idfa))) {
            // idfa
            return device.getExtension(MexOpenRtbExt.idfa);
        } else if (device.hasExtension(MexOpenRtbExt.androidId) && notNull(device.getExtension(MexOpenRtbExt.androidId))) {
            //android_id
            return device.getExtension(MexOpenRtbExt.androidId);
        } else if (device.hasDpidmd5() && notNull(device.getDpidmd5())) {
            // android_id_md5 or idfa md5
            return device.getDpidmd5();
        } else if (device.hasDpidsha1() && notNull(device.getDpidsha1())) {
            // android_id_sha1 or idfa sha1
            return device.getDpidsha1();
        }
        // 新增mac地址
        else if (device.hasExtension(MexOpenRtbExt.mac) && notNull(device.getExtension(MexOpenRtbExt.mac))) {
            // mac
            return device.getExtension(MexOpenRtbExt.mac);
        }
        // macmd5
        else if (device.hasMacmd5() && notNull(device.getMacmd5())) {
            return device.getMacmd5();
        }
        // macsha1
        else if (device.hasMacsha1() && notNull(device.getMacsha1())) {
            return device.getMacsha1();
        }
        return "NA";

    }

    public static void md5DeviceId(OpenRtb.BidRequest.Device.Builder device) {

        if (!device.hasDidmd5()) {
            String imei = device.getExtension(MexOpenRtbExt.imei);
            if (notNull(imei)) {
                device.setDidmd5(MD5Utils.MD5(imei));
            }
        }

        if (!device.hasMacmd5()) {
            String mac = device.getExtension(MexOpenRtbExt.mac);
            if (notNull(mac)) {
                device.setMacmd5(MD5Utils.MD5(mac));
            }
        }

        // androidid或者 idfa
        if (!device.hasDpidmd5()) {
            String idfa = device.getExtension(MexOpenRtbExt.idfa);
            String androidId = device.getExtension(MexOpenRtbExt.androidId);
            if (notNull(androidId)) {
                device.setDpidmd5(MD5Utils.MD5(androidId));
            }
            if (notNull(idfa)) {
                device.setDpidmd5(MD5Utils.MD5(idfa));
            }
        }


    }


    private static boolean notNull(String id) {
        return !Strings.isNullOrEmpty(id);
    }

    private static String formatKey(String originalKey) {

        if (originalKey.contains(pakageName)) {
            return originalKey.split(pakageName)[1];
        }
        return originalKey;
    }

}
