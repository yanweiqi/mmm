package com.mex.bidder.engine.builder.response;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.constants.MexCurrency;
import com.mex.bidder.engine.util.RtbHelper;
import com.mex.bidder.protocol.Ad;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * user: donghai
 * date: 2017/4/21
 */
public class MexTrackerHelper {
    // 通过查询串
    static final String QUERY_STRING = "requestid=${requestid}&adgroupid=${adgroupid}" +
            "&netid=${netid}&netname=${netname}" + "&devicetype=${devicetype}&os=${os}" +
            "&connectiontype=${connectiontype}" + "&material_id=${material_id}&adid=${adid}" +
            "&android_id=${android_id}&android_id_md5=${android_id_md5}&android_id_sha1=${android_id_sha1}" +
            "&imei=${imei}&imei_md5=${imei_md5}" + "&imei_sha1=${imei_sha1}&deviceID=${deviceID}" +
            "&mac=${mac}&mac_md5=${mac_md5}&mac_sha1=${mac_sha1}" + "&remote_addr=${remote_addr}" +
            "&cur_adv=${cur_adv}&cur_adx=${cur_adx}&adver_id=${adver_id}&campaign_id=${campaign_id}" +
            "&resptimestamp=${resptimestamp}" + "&height=${height}&width=${width}&make=${make}&model=${model}" +
            "&bundle=${bundle}&ip=${ip}&app_name=${app_name}&material_type=${material_type}&productid=${productid}" +
            "&taid=${taid}&tatype=${tatype}&tamid=${tamid}";

    public static String buildQueryString(BidRequest request, BidResponse response) {

        // TODO 整理返回的json中的key和uri中的key是否重复
        OpenRtb.BidRequest mexBidRequest = request.openRtb();
        Ad baseAd = response.getAdAndPricePair().getAd();
        OpenRtb.BidRequest.Device device = mexBidRequest.getDevice();

        String commonUrl;

        commonUrl = QUERY_STRING.replace("${requestid}", encodeUri(request.openRtb().getId()))
                .replace("${adgroupid}", encodeUri(baseAd.getAdGroupId() + ""))
                .replace("${netid}", encodeUri(baseAd.getAdxId()))
                .replace("${netname}", encodeUri(baseAd.getAdxName()))
                .replace("${devicetype}", encodeUri(device.hasDevicetype() ? device.getDevicetype().name() : "NA"))
                .replace("${os}", encodeUri(device.hasOs() ? device.getOs() : "NA"))
                .replace("${connectiontype}", encodeUri(device.hasConnectiontype() ? device.getConnectiontype().name() : "NA"))
                .replace("${material_id}", encodeUri(baseAd.getCreativeId() + ""))
                //iOS ADID(也叫 IDFA)或Android ADID
                .replace("${adid}", encodeUri(device.getExtension(MexOpenRtbExt.idfa)))
                .replace("${android_id}", encodeUri(device.getExtension(MexOpenRtbExt.androidId)))
                .replace("${android_id_md5}", encodeUri(device.getDpidmd5()))
                .replace("${android_id_sha1}", encodeUri(device.getDpidsha1()))
                // imei
                .replace("${imei}", encodeUri(device.getExtension(MexOpenRtbExt.imei)))
                .replace("${imei_md5}", encodeUri(device.getDidmd5()))
                .replace("${imei_sha1}", encodeUri(device.getDidsha1()))
                // mac
                .replace("${mac}", encodeUri(device.getExtension(MexOpenRtbExt.mac)))
                .replace("${mac_md5}", encodeUri(device.getMacmd5()))
                .replace("${mac_sha1}", encodeUri(device.getMacsha1()))
                .replace("${deviceID}", encodeUri(RtbHelper.getDeviceId(request)))

                .replace("${remote_addr}", encodeUri(device.getIp())) // ipv4
                .replace("${cur_adv}", encodeUri(MexCurrency.lookup(baseAd.getAdverCurrencyType()).toString()))
                .replace("${cur_adx}", encodeUri(MexCurrency.lookup(baseAd.getAdxCurrencyType()).toString()))
                .replace("${adver_id}", encodeUri(String.valueOf(baseAd.getAdverId())))
                .replace("${campaign_id}", encodeUri(String.valueOf(baseAd.getAdCampaignId())))
                .replace("${resptimestamp}", encodeUri(DateTime.now().toString("yyyyMMddHHmmssSSS")))
                .replace("${height}", encodeUri(baseAd.getHeight() + ""))
                .replace("${width}", encodeUri(baseAd.getWidth() + ""))
                .replace("${make}", encodeUri(mexBidRequest.getDevice().hasMake() ? mexBidRequest.getDevice().getMake() : "NA"))
                .replace("${model}", encodeUri(mexBidRequest.getDevice().hasModel() ? mexBidRequest.getDevice().getModel() : "NA"))
                .replace("${bundle}", encodeUri(mexBidRequest.getApp().getBundle()))
                .replace("${ip}", encodeUri(device.hasIp() ? device.getIp() : "NA"))
                .replace("${material_type}", response.getResponseMode().toString().toLowerCase())
                .replace("${app_name}", encodeUri(mexBidRequest.getApp().hasName() ? mexBidRequest.getApp().getName() : "NA"))
                .replace("${productid}",encodeUri(baseAd.getProductId()+""))

                .replace("${tamid}", encodeUri(String.valueOf(response.getBaseTa().getTaMappingId())))
                .replace("${taid}", encodeUri(String.valueOf(response.getBaseTa().getTaId())))
                .replace("${tatype}", encodeUri(response.getBaseTa().getTaType()))
        ;

        return commonUrl;
    }


    public static String encodeUri(String val) {
        try {
            if (Strings.isNullOrEmpty(val)) {
                // 为空就encode “NA”
                val = "NA";
            }
            val = URLEncoder.encode(val, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("url encode error, url=" + val, e);
        }
        return val;
    }


}
