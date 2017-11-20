package com.mex.bidder.engine.constants;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * User: donghai
 * Date: 2016/11/23
 */
public class Constants {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final String LOG_TYPE_REQUEST = "request";
    public static final String LOG_TYPE_RESPONSE = "response";

    //----------------------exchange--ID--------------------------
    public static final String SOHU_ID = "adssohu";
    public static final String BAIDU_ID = "adsbd";
    public static final String ADVIEW_ID = "adsview";
    public static final String GY_ID = "adsgy";
    public static final String ZPLAY_ID = "adszp";
    public static final String IFLYTEK_ID = "adsiflytek";
    public static final String MEITU_ID = "adsmeitu";



    public static final String EB_QPS_VERTICLE = "eb.qps.verticle";

    public static final int ONE_YUN = 1 * 10 * 10;

    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";


    public static final String KAFKA_KEY_RTB_TOPIC = "kafka-rtb-topic";

    //-----------------------渠道宏替换-------------------------------
    public static final String MACRO_PRICE_GY = "&price=%%AUCTION_PRICE%%";
    public static final String MACRO_PRICE_IFLYTEK = "&price=${AUCTION_PRICE}";
    public static final String MACRO_PRICE_ADVIEW = "&price=%%WIN_PRICE%%";
    public static final String MACRO_PRICE_ZPLAY = "&price={AUCTION_BID_PRICE}";
    public static final String MACRO_PRICE_SOHU = "&price=%%WINPRICE%%";
    public static final String MACRO_PRICE_MEITU = "&price=%%WINNING_PRICE%%";


    //--------------------返回日志的参数---------------------------
    public static final String LOG_KEY_LOGTIME = "logtime";
    public static final String LOG_KEY_LOGTYPE = "logtype";
    public static final String LOG_KEY_NETID = "netid";
    public static final String LOG_KEY_NETNAME = "netname";
    public static final String LOG_KEY_APP_NAME = "app_name";
    public static final String LOG_KEY_SITE_NAME = "site_name";
    public static final String LOG_KEY_MATERIAL_TYPE = "material_type";

    public static final String LOG_KEY_WIDTH = "width";
    public static final String LOG_KEY_HEIGHT = "height";
    public static final String LOG_KEY_MAKE = "make";
    public static final String LOG_KEY_MODEL = "model";
    public static final String LOG_KEY_BIDFLOOR = "bidfloor";
    public static final String LOG_KEY_BIDFLOORCUR = "bidfloorcur";
    public static final String LOG_KEY_BUNDLE = "bundle";
    public static final String LOG_KEY_ADVER_ID = "adver_id";
    public static final String LOG_KEY_CAMPAIGN_ID = "campaign_id";
    public static final String LOG_KEY_ADGROUPID = "adgroupid";
    public static final String LOG_KEY_ADGROUPNAME = "adgroupname";
    public static final String LOG_KEY_MATERIAL_ID = "material_id";
    public static final String LOG_KEY_NBRCODE = "nbrcode";
    public static final String LOG_KEY_PRICE = "price";
    public static final String LOG_KEY_ADVER_PRICE = "adver_price";
    public static final String LOG_KEY_DEVICETYPE = "devicetype";
    public static final String LOG_KEY_OS = "os";
    public static final String LOG_KEY_CONNECTIONTYPE = "connectiontype";
    public static final String LOG_KEY_IP = "ip";
    public static final String LOG_KEY_ADID = "adid";
    public static final String LOG_KEY_IDFA = "idfa";
    public static final String LOG_KEY_ANDROID_ID = "android_id";
    public static final String LOG_KEY_ANDROID_ID_MD5 = "android_id_md5";
    public static final String LOG_KEY_ANDROID_ID_SHA1 = "android_id_sha1";
    public static final String LOG_KEY_IMEI = "imei";
    public static final String LOG_KEY_IMEI_MD5 = "imei_md5";
    public static final String LOG_KEY_IMEI_SHA1 = "imei_sha1";
    public static final String LOG_KEY_DEVICEID = "deviceId";

    //----------------------国双macro------------------------
    public static final String GS_MACRO_OS = "__OS__";
    public static final String GS_MACRO_IMEI = "__IMEI__";
    public static final String GS_MACRO_MAC = "__MAC__";
    public static final String GS_MACRO_MAC1 = "__MAC1__";
    public static final String GS_MACRO_IDFA = "__IDFA__";
    public static final String GS_MACRO_AAID = "__AAID__";
    public static final String GS_MACRO_OpenUDID = "__OpenUDID__";
    public static final String GS_MACRO_AndroidID = "__AndroidID__";
    public static final String GS_MACRO_AndroidID1 = "__AndroidID1__";
    public static final String GS_MACRO_UDID = "__UDID__";
    public static final String GS_MACRO_ODIN = "__ODIN__";
    public static final String GS_MACRO_DUID = "__DUID__";
    public static final String GS_MACRO_IP = "__IP__";
    public static final String GS_MACRO_UA = "__UA__";
    public static final String GS_MACRO_TS = "__TS__";


    //----------------------秒针macro------------------------
    public static final String MZ_MACRO_OS = "__OS__";
    public static final String MZ_MACRO_IP = "__IP__";
    public static final String MZ_MACRO_IDFA = "__IDFA__";
    public static final String MZ_MACRO_OPENUDID = "__OPENUDID__";
    public static final String MZ_MACRO_IMEI = "__IMEI__";
    public static final String MZ_MACRO_ANDROIDID = "__ANDROIDID__";
    public static final String MZ_MACRO_ANDROIDID1 = "__ANDROIDID1__";
    public static final String MZ_MACRO_MAC = "__MAC__";
    public static final String MZ_MACRO_MAC1 = "__MAC1__";
    public static final String MZ_MACRO_DUID = "__DUID__";
    public static final String MZ_MACRO_APP = "__APP__";


    ////------------------------admaster-------------------------------------
    public static final String AD_MACRO_OS = "__OS__";
    public static final String AD_MACRO_IMEI = "__IMEI__";
    public static final String AD_MACRO_ANDROIDID = "__AndroidID__";
    public static final String AD_MACRO_DUID = "__DUID__";
    public static final String AD_MACRO_MAC = "__MAC__";
    public static final String AD_MACRO_OUID = "__OUID__";
    public static final String AD_MACRO_IDFA = "__IDFA__";
    public static final String AD_MACRO_IDFAMD5 = "__IDFAmd5__";
    public static final String AD_MACRO_IP = "__IP__";
    public static final String AD_MACRO_TS = "__TS__";
    public static final String AD_MACRO_TERM = "__TERM__";// TODO
    public static final String AD_MACRO_LBS = "__LBS__";
    public static final String AD_MACRO_APP = "__APP__";


    ///----------------------渠道请求字段的key----------------------------

    public static final String REQ_KEY_IP = "ip";
    public static final String REQ_KEY_IDFA = "adid";
    public static final String REQ_KEY_ANDROID_ID_MD5 = "android_id_md5";
    public static final String REQ_KEY_ANDROID_ID = "android_id";
    public static final String REQ_KEY_ANDROID_ID_SHA1 = "android_id_sha1";

    public static final String REQ_KEY_OS = "os";
    public static final String REQ_KEY_IMEI = "imei";
    public static final String REQ_KEY_IMEI_MD5 = "imei_md5";
    public static final String REQ_KEY_IMEI_SHA1 = "imei_sha1";
    public static final String REQ_KEY_MAC = "mac_md5";
    public static final String REQ_KEY_MAC1 = "mac_md5";
    public static final String REQ_KEY_MODEL = "model";
    public static final String REQ_KEY_APPNAME = "app_name";
    public static final String REQ_KEY_TS = "ts";


    //-------------
    public static final String MAP_IMP = "imp";
    public static final String MAP_CLK = "clk";
    public static final String MAP_WIN = "win";
    public static final String MAP_LP = "landingpage";
    public static final String MAP_MAT = "material";


}
