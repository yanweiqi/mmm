package com.mex.bidder.engine.logger.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Maps;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.protocol.AdxData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * user: donghai
 * date: 2016/12/27
 */
public class LogUtil {


    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static Map<String, Object> buildCommonLog(BidRequest request, MexDataContext mexDataContext, String type) {
        AdxData adxDataByExchange = mexDataContext.getAdxDataByExchange(request.getExchange());

        String adxCode = adxDataByExchange.getAdxCode();
        Map<String, Object> logMap = Maps.newHashMap();
        logMap.put(Constants.LOG_KEY_LOGTIME, DateTime.now().toString(DATE_FORMAT));
        logMap.put(Constants.LOG_KEY_LOGTYPE, type);
        logMap.put(Constants.LOG_KEY_NETID, adxCode);
        logMap.put(Constants.LOG_KEY_NETNAME, adxDataByExchange.getCodeName());

        return logMap;
    }

    public static Map<String, Object> buildRequestLog(Map<String, Object> logMap,
                                                      BidRequest request, MexDataContext mexDataContext) {
        OpenRtb.BidRequest bidRequest = request.openRtb();

        logMap.put(Constants.LOG_KEY_APP_NAME, checkNull(bidRequest.getApp().getName()));
        logMap.put(Constants.LOG_KEY_SITE_NAME, checkNull(bidRequest.getSite().getName()));

        String materialType = parseMaterialType(bidRequest, "banner");
        logMap.put(Constants.LOG_KEY_MATERIAL_TYPE, materialType);
        logMap.put(Constants.LOG_KEY_WIDTH, parseWidth(bidRequest, materialType));
        logMap.put(Constants.LOG_KEY_HEIGHT, parseHeight(bidRequest, materialType));
        logMap.put(Constants.LOG_KEY_MAKE, checkNull(bidRequest.getDevice().getMake()));
        logMap.put(Constants.LOG_KEY_MODEL, checkNull(bidRequest.getDevice().getModel()));
        logMap.put(Constants.LOG_KEY_BIDFLOOR, bidRequest.getImp(0).getBidfloor());
        logMap.put(Constants.LOG_KEY_BIDFLOORCUR, bidRequest.getImp(0).getBidfloorcur());
        logMap.put(Constants.LOG_KEY_BUNDLE, checkNull(bidRequest.getApp().getBundle()));

        buildDevice(logMap, bidRequest);

        return logMap;
    }


    public static Map<String, Object> buildResponseLog(Map<String, Object> logMap, BidRequest request,
                                                       MexDataContext mexDataContext) {

        buildRequestLog(logMap, request, mexDataContext);

        return logMap;

    }

    private static void buildDevice(Map<String, Object> reqMap, OpenRtb.BidRequest bidRequest) {
        if (bidRequest.hasDevice()) {
            OpenRtb.BidRequest.Device device = bidRequest.getDevice();
            reqMap.put(Constants.LOG_KEY_DEVICETYPE, device.hasDevicetype() ? device.getDevicetype().name() : "NA");
            reqMap.put(Constants.LOG_KEY_OS, device.hasOs() ? device.getOs() : "");
            reqMap.put(Constants.LOG_KEY_CONNECTIONTYPE, device.hasDevicetype() ? device.getConnectiontype().name() : "NA");
            reqMap.put(Constants.LOG_KEY_IP, device.hasIp() ? device.getIp() : "NA");

            Map<String, String> deviceMap = new HashMap<>(7);
            deviceMap.put(Constants.LOG_KEY_ADID, device.hasExtension(MexOpenRtbExt.idfa) ? device.getExtension(MexOpenRtbExt.idfa) : "NA");
            deviceMap.put(Constants.LOG_KEY_IDFA, device.hasExtension(MexOpenRtbExt.idfa) ? device.getExtension(MexOpenRtbExt.idfa) : "NA");
            deviceMap.put(Constants.LOG_KEY_ANDROID_ID, device.hasExtension(MexOpenRtbExt.androidId) ? device.getExtension(MexOpenRtbExt.androidId) : "NA");
            deviceMap.put(Constants.LOG_KEY_ANDROID_ID_MD5, device.hasDpidmd5() ? device.getDpidmd5() : "NA");
            deviceMap.put(Constants.LOG_KEY_ANDROID_ID_SHA1, device.hasDpidsha1() ? device.getDpidsha1() : "NA");
            deviceMap.put(Constants.LOG_KEY_IMEI, device.hasExtension(MexOpenRtbExt.imei) ? device.getExtension(MexOpenRtbExt.imei) : "NA");
            deviceMap.put(Constants.LOG_KEY_IMEI_MD5, device.hasDidmd5() ? device.getDidmd5() : "NA");
            deviceMap.put(Constants.LOG_KEY_IMEI_SHA1, device.hasDidsha1() ? device.getDidsha1() : "NA");
            reqMap.put(Constants.LOG_KEY_DEVICEID, deviceMap);
        }
    }

    private static String checkNull(String info) {
        if (Strings.isNullOrEmpty(info)) {
            return "NA";
        }
        return info;
    }

    private static String parseMaterialType(OpenRtb.BidRequest bidRequest, String def) {
        OpenRtb.BidRequest.Imp imp = bidRequest.getImp(0);
        if (imp.hasNative() && imp.getNative().hasRequestNative()) {
            return "native";
        } else if (imp.hasVideo() && 0 != imp.getVideo().getW()) {
            return "video";
        } else if (imp.hasBanner() && 0 != imp.getBanner().getW()) {
            return "banner";
        }
        return def;
    }

    private static String parseWidth(OpenRtb.BidRequest bidRequest, String materialType) {
        OpenRtb.BidRequest.Imp imp = bidRequest.getImp(0);
        if ("banner".equals(materialType)) {
            return imp.getBanner().getW() + "";
        }
        if ("video".equals(materialType)) {
            return imp.getVideo().getW() + "";
        }
        return "NA";
    }

    private static String parseHeight(OpenRtb.BidRequest bidRequest, String materialType) {
        OpenRtb.BidRequest.Imp imp = bidRequest.getImp(0);
        if ("banner".equals(materialType)) {
            return imp.getBanner().getH() + "";
        }
        if ("video".equals(materialType)) {
            return imp.getVideo().getH() + "";
        }
        return "NA";
    }



    /**
     * 遍历json格式数据
     *
     * @param json
     * @return
     */
    public static Object traveseJson(Object json) {

        if (json == null) {
            return null;
        }
        if (json instanceof JSONObject) {//json 是一个map
            //创建一个json对象
            JSONObject jsonObj = new JSONObject();
            //将json转换为JsonObject对象
            JSONObject jsonStr = (JSONObject) json;
            //迭代器迭代 map集合所有的keys

            Iterator it = jsonStr.keySet().iterator();
            while (it.hasNext()) {
                //获取map的key
                String key = (String) it.next();
                //得到value的值
                Object value = jsonStr.get(key);
                //System.out.println(value);
                //递归遍历
                jsonObj.put(key, traveseJson(value));

            }
            return jsonObj;

        } else if (json instanceof JSONArray) {// if  json 是 数组
            JSONArray jsonAry = new JSONArray();
            JSONArray jsonStr = (JSONArray) json;
            //获取Array 的长度
            int length = jsonStr.size();
            for (int i = 0; i < length; i++) {

                jsonAry.add(traveseJson(jsonStr.get(i)));
            }

            return jsonAry;

        } else {//其他类型

            return json;
        }

    }
}
