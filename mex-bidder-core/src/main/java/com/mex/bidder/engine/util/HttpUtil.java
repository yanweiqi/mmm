package com.mex.bidder.engine.util;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.mex.bidder.engine.constants.Constants;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * User: donghai
 * Date: 2016/11/20
 */
public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);


    public static HttpServerResponse setStatusCode(HttpServerResponse httpResponse, int statusCode) {
        httpResponse.setStatusCode(statusCode);
        return httpResponse;
    }

    public static HttpServerResponse setStatusOk(HttpServerResponse httpResponse) {
        setStatusCode(httpResponse, 200);
        return httpResponse;
    }

    public static HttpServerResponse setMediaType(HttpServerResponse httpResponse, MediaType mediaType) {
        httpResponse.putHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString());
        return httpResponse;
    }

    public static Map<String, List<String>> splitQuery(String uri) {
        if (Strings.isNullOrEmpty(uri)) {
            return Collections.emptyMap();
        }
        String[] pathAndQuery = uri.split("\\?");
        if (pathAndQuery.length == 1) {
            return Collections.emptyMap();
        }
        String query = pathAndQuery[1];

        return Arrays.stream(query.split("&"))
                .map(HttpUtil::splitQueryParameter)
                .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey,
                        LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    private static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        // 解码
        if (!Strings.isNullOrEmpty(value)) {
            try {
                value = URLDecoder.decode(value, "utf-8");
            } catch (UnsupportedEncodingException e) {
                value = "NA";
                logger.error("decode value=" + value, e);
            }
        }
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    /**
     * admaster:
     * 曝光和点击都需要替换
     * <p>
     * 秒针：
     * 只需要替换曝光
     * 如果字段有值（且不为空，不为NA）则替换该filed
     * 如果字段为空或者为NA，不能替换该filed，否则会影响第三方计数
     * <p>
     * 其他：
     * 返回原数据
     *
     * @param redirectUrl
     * @param paramsMap
     * @return
     */
    public static String redirectUrl(String redirectUrl, Map<String, List<String>> paramsMap) {

        // admaster
        if (redirectUrl.contains("admaster.com.cn")) {
            // os
            redirectUrl = urlReplaceAd(paramsMap, redirectUrl, Constants.AD_MACRO_OS, Constants.REQ_KEY_OS, parseParamOS(paramsMap));
            //ip
            redirectUrl = urlReplaceAd(paramsMap, redirectUrl, Constants.AD_MACRO_IP, Constants.REQ_KEY_IP, "NA");
            // idfa 原始值
            redirectUrl = urlReplaceAd(paramsMap, redirectUrl, Constants.AD_MACRO_IDFA, Constants.REQ_KEY_IDFA, "NA");
            // idfa md5
            redirectUrl = urlReplaceAd(paramsMap, redirectUrl, Constants.AD_MACRO_IDFAMD5, Constants.REQ_KEY_IDFA, parseParamIdfa(paramsMap));
            // addroidid
            redirectUrl = urlReplaceAd(paramsMap, redirectUrl, Constants.AD_MACRO_ANDROIDID, Constants.REQ_KEY_ANDROID_ID, parseParamAndroidId(paramsMap));
            // mac
            redirectUrl = urlReplaceAd(paramsMap, redirectUrl, Constants.AD_MACRO_MAC, Constants.REQ_KEY_MAC, parseParamMAC(paramsMap));

            // term
            redirectUrl = urlReplaceAd(paramsMap, redirectUrl, Constants.AD_MACRO_TERM, Constants.REQ_KEY_MODEL, "NA");
            // TS
            redirectUrl = redirectUrl.replace(Constants.AD_MACRO_TS, System.currentTimeMillis() + "");


            // imei
            if (redirectUrl.contains(Constants.AD_MACRO_IMEI)) {
                if (notNullOrEmpty(parseParamImei(paramsMap))) {
                    redirectUrl = redirectUrl.replace(Constants.AD_MACRO_IMEI, parseParamImei(paramsMap));
                } else {
                    //为空或者NA，用空串替换
                    redirectUrl = redirectUrl.replace(Constants.AD_MACRO_IMEI, "");
                }
            }

            return redirectUrl;
        } else if (redirectUrl.contains("miaozhen.com")) {
            // 秒针只替换曝光
            //os
            redirectUrl = urlReplaceMz(paramsMap, redirectUrl, Constants.MZ_MACRO_OS, Constants.REQ_KEY_OS, parseParamOS(paramsMap));
            //ip
            redirectUrl = urlReplaceMz(paramsMap, redirectUrl, Constants.MZ_MACRO_IP, Constants.REQ_KEY_IP, "NA");
            //idfa
            redirectUrl = urlReplaceMz(paramsMap, redirectUrl, Constants.MZ_MACRO_IDFA, Constants.REQ_KEY_IDFA, "NA");
            // addroidid AndroidID取MD5摘要
            redirectUrl = urlReplaceMz(paramsMap, redirectUrl, Constants.MZ_MACRO_ANDROIDID, Constants.REQ_KEY_ANDROID_ID_MD5, parseParamAndroidId(paramsMap));
            //androidid1 AndroidID原始值
            redirectUrl = urlReplaceMz(paramsMap, redirectUrl, Constants.MZ_MACRO_ANDROIDID1, Constants.REQ_KEY_ANDROID_ID, "NA");
            //MAC：去除分隔符”:”的大写MAC地址取MD5摘要
            redirectUrl = urlReplaceMz(paramsMap, redirectUrl, Constants.MZ_MACRO_MAC, Constants.REQ_KEY_MAC, parseParamMAC(paramsMap));
            //MAC1：保留分隔符”:”的大写MAC地址取MD5摘要
            redirectUrl = urlReplaceMz(paramsMap, redirectUrl, Constants.MZ_MACRO_MAC1, Constants.REQ_KEY_MAC1, "NA");

            // app name
            redirectUrl = urlReplaceMz(paramsMap, redirectUrl, Constants.MZ_MACRO_APP, Constants.REQ_KEY_APPNAME, "NA");
            //imei
            if (redirectUrl.contains(Constants.MZ_MACRO_IMEI)) {
                if (notNullOrEmpty(parseParamImei(paramsMap))) {
                    redirectUrl = redirectUrl.replace(Constants.MZ_MACRO_IMEI, parseParamImei(paramsMap));
                }
            }
        } else if (redirectUrl.contains("gridsumdissector.com")) {
            // 国双监测
            // OS
            redirectUrl = urlReplaceGs(paramsMap, redirectUrl, Constants.GS_MACRO_OS, Constants.REQ_KEY_OS, parseParamOS(paramsMap));
            // ip
            redirectUrl = urlReplaceGs(paramsMap, redirectUrl, Constants.GS_MACRO_IP, Constants.REQ_KEY_IP, "NA");
            // IMEI
            redirectUrl = urlReplaceGs(paramsMap, redirectUrl, Constants.GS_MACRO_IMEI, Constants.REQ_KEY_IMEI_MD5, "NA");
            // MAC 去除分隔符 ":"，（保持大 写）取 md5sum 摘要
            redirectUrl = urlReplaceGs(paramsMap, redirectUrl, Constants.GS_MACRO_MAC, Constants.REQ_KEY_MAC, parseParamMAC(paramsMap));
            // MAC1 保留分隔符 ":"，（保持大 写）取 md5sum 摘 要
            redirectUrl = urlReplaceGs(paramsMap, redirectUrl, Constants.GS_MACRO_MAC1, Constants.REQ_KEY_MAC1, parseParamMAC1(paramsMap));
            // IDFA
            redirectUrl = urlReplaceGs(paramsMap, redirectUrl, Constants.GS_MACRO_IDFA, Constants.REQ_KEY_IDFA, "NA");
            // AndroidID ，md5 加 密
            redirectUrl = urlReplaceGs(paramsMap, redirectUrl, Constants.GS_MACRO_AndroidID, Constants.REQ_KEY_ANDROID_ID_MD5, parseParamAndroidId(paramsMap));
            // 用户终端的 AndroidID，保留原始值
            redirectUrl = urlReplaceGs(paramsMap, redirectUrl, Constants.GS_MACRO_AndroidID1, Constants.REQ_KEY_ANDROID_ID, "NA");
            // UDID， md5 加密
            //
            //IP
            redirectUrl = urlReplaceGs(paramsMap, redirectUrl, Constants.GS_MACRO_IP, Constants.REQ_KEY_IP, "NA");

            // TS 客户端触发监测的时间
            redirectUrl = redirectUrl.replace(Constants.GS_MACRO_TS, System.currentTimeMillis() + "");


        } else {
            //如果不是秒针，直接return
            return redirectUrl;
        }
        return redirectUrl;
    }

    /**
     * url宏替换 国双监测
     *
     * @param paramsMap   参数列表
     * @param redirectUrl 需要替换的url
     * @param gsMacroKey  国双的url中需要替换的key
     * @param reqKey      paramMap中对应的key
     * @param reqVal      paramMap中对应的val
     * @return
     */
    private static String urlReplaceGs(Map<String, List<String>> paramsMap, String redirectUrl,
                                       String gsMacroKey, String reqKey, String reqVal) {

        if (redirectUrl.contains(gsMacroKey)) {
            if (notNullOrEmpty(parseParamMap(paramsMap, reqKey))) {
                // paramsMap 中有值（不为空不为NA）
                if ("NA".equals(reqVal)) {
                    redirectUrl = redirectUrl.replace(gsMacroKey, parseParamMap(paramsMap, reqKey));
                } else {
                    redirectUrl = redirectUrl.replace(gsMacroKey, reqVal);
                }
            }
        }
        // 如果连接不包含这个检测的key或者渠道的传该字段的值为空或者NA，则返回原值
        return redirectUrl;
    }


    /**
     * url宏替换  miaozhen
     *
     * @param paramsMap   参数列表
     * @param redirectUrl 需要替换的url
     * @param mzMacroKey  秒针的url中需要替换的key
     * @param reqKey      paramMap中对应的key
     * @param reqVal      paramMap中对应的val
     * @return
     */
    private static String urlReplaceMz(Map<String, List<String>> paramsMap, String redirectUrl,
                                       String mzMacroKey, String reqKey, String reqVal) {
        if (redirectUrl.contains(mzMacroKey)) {
            if (notNullOrEmpty(parseParamMap(paramsMap, reqKey))) {
                //paramsMap 中有值（不为空且不为NA）
                if ("NA".equals(reqVal)) {
                    redirectUrl = redirectUrl.replace(mzMacroKey, parseParamMap(paramsMap, reqKey));
                } else {
                    redirectUrl = redirectUrl.replace(mzMacroKey, reqVal);
                }
            }
        }

        // 如果连接不包含这个检测的key或者渠道的传该字段的值为空或者NA，则返回原值
        return redirectUrl;
    }

    /**
     * url宏替换 admater,如果空串，则该宏用空串替换
     *
     * @param paramsMap   参数列表
     * @param redirectUrl 需要替换的url
     * @param adMacroKey  秒针的url中需要替换的key
     * @param reqKey      paramMap中对应的key
     * @param reqVal      paramMap中对应的val
     * @return
     */
    private static String urlReplaceAd(Map<String, List<String>> paramsMap, String redirectUrl,
                                       String adMacroKey, String reqKey, String reqVal) {
        if (redirectUrl.contains(adMacroKey)) {
            if (notNullOrEmpty(parseParamMap(paramsMap, reqKey))) {
                if ("NA".equals(reqVal)) {
                    // 渠道的对应的字段有值，则替换
                    redirectUrl = redirectUrl.replace(adMacroKey, parseParamMap(paramsMap, reqKey));
                } else {
                    // 用处理过的值替换
                    redirectUrl = redirectUrl.replace(adMacroKey, reqVal);
                }
            } else {
                // 渠道请求的参数中该字段对应的值为空串或者“NA”,用空串替换
                redirectUrl = redirectUrl.replace(adMacroKey, "");
            }
        }
        return redirectUrl;
    }

    /**
     * mac 大写去除“：”分隔符的md5摘要
     *
     * @param paramsMap
     * @return
     */
    private static String parseParamMAC(Map<String, List<String>> paramsMap) {
        String mac = parseParamMap(paramsMap, "mac");
        if ("NA".equals(mac)) {
            return "NA";
        } else {
            mac = mac.replace(":", "").toUpperCase();
            return MD5Utils.MD5(mac);
        }
    }


    /**
     * mac1 保留分隔符 ":"，（保持大 写）取 md5sum 摘 要
     *
     * @param paramsMap
     * @return
     */
    private static String parseParamMAC1(Map<String, List<String>> paramsMap) {
        String macMd5 = parseParamMap(paramsMap, "mac_md5");
        if ("NA".equals(macMd5)) {
            String mac = parseParamMap(paramsMap, "mac");
            if ("NA".equals(mac)) {
                return "NA";
            } else {
               return MD5Utils.MD5(mac.toUpperCase());
            }
        } else {
            return macMd5;
        }
    }

    /**
     * 空串或者“NA”
     *
     * @param param
     * @return
     */
    private static boolean notNullOrEmpty(String param) {
        if (Strings.isNullOrEmpty(param) || "NA".equals(param)) {
            return false;
        }
        return true;
    }

    private static String parseParamMap(Map<String, List<String>> paramsMap, String key) {
        List<String> stringList = paramsMap.get(key);
        if (null != stringList && stringList.size() > 0) {
            return stringList.get(0);
        }
        return "";
    }

    /**
     * checkList 检查是否为空
     * null 返回 false
     * empty 返回 false
     * NA 返回 false
     *
     * @param list
     * @return
     */
    private static boolean checkList(List<String> list) {
        if (Objects.isNull(list)) {
            return false;
        } else if (list.isEmpty()) {
            return false;
        } else {
            String li = list.get(0);
            if ("NA".equals(li)) {
                return false;
            } else {
                return true;
            }
        }
    }

    private static String parseParamImei(Map<String, List<String>> paramsMap) {

        if (null != paramsMap.get(Constants.REQ_KEY_IMEI_MD5) && paramsMap.get(Constants.REQ_KEY_IMEI_MD5).size() > 0) {
            if (notNullOrEmpty(paramsMap.get(Constants.REQ_KEY_IMEI_MD5).get(0))) {
                return paramsMap.get(Constants.REQ_KEY_IMEI_MD5).get(0);
            }
        }
        if (null != paramsMap.get(Constants.REQ_KEY_IMEI) && paramsMap.get(Constants.REQ_KEY_IMEI).size() > 0) {
            if (notNullOrEmpty(paramsMap.get(Constants.REQ_KEY_IMEI).get(0))) {
                return MD5Utils.MD5(paramsMap.get(Constants.REQ_KEY_IMEI).get(0));
            }
        }
      /*  if (null != paramsMap.get(Constants.REQ_KEY_IMEI_SHA1) && paramsMap.get(Constants.REQ_KEY_IMEI_SHA1).size() > 0) {
            if (notNullOrEmpty(paramsMap.get(Constants.REQ_KEY_IMEI_SHA1).get(0))) {
                return paramsMap.get(Constants.REQ_KEY_IMEI_SHA1).get(0);
            }
        }*/

        return "NA";
    }


    private static String parseParamAndroidId(Map<String, List<String>> paramsMap) {
        List<String> android_id_md5 = paramsMap.get("android_id_md5");

        if (checkList(android_id_md5)) {
            return android_id_md5.get(0);
        } else {
            List<String> android_id = paramsMap.get("android_id");
            if (checkList(android_id)) {
                return MD5Utils.MD5(android_id.get(0));
            } else {
                return "NA";
            }
        }

    }

    /**
     * 取原始的idfa，然后md5加密
     *
     * @param paramsMap
     * @return
     */
    private static String parseParamIdfa(Map<String, List<String>> paramsMap) {
        List<String> adid = paramsMap.get("adid");

        if (checkList(adid)) {
            String s = adid.get(0);
            return MD5Utils.MD5(s);
        }
        return "NA";
    }

    // TODO 关联字典表

    /**
     * 1位数字,取0~3。
     * 0=Android，1=iOS，2=Windows Phone，3=其他
     *
     * @param paramsMap
     * @return
     */
    private static String parseParamOS(Map<String, List<String>> paramsMap) {
        List<String> osLi = paramsMap.get("os");
        if (null != osLi && osLi.size() > 0) {
            String os = osLi.get(0);
            if ("android".equals(os.toLowerCase())) {
                return "0";
            } else if ("ios".equals(os.toLowerCase())) {
                return "1";
            } else if (os.toLowerCase().contains("windows")) {
                return "2";
            } else {
                return "3";
            }
        }
        return "3";
    }

}
