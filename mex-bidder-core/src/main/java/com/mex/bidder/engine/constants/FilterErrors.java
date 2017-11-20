package com.mex.bidder.engine.constants;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public enum FilterErrors {
    FILTER_FAIL_EMPTY("20"),               // 20 creatives表为空
    FILTER_FAIL_Wh("21"),                  // 21 W*h 不匹配
    FILTER_FAIL_OS("22"),                  // 22 OS 不匹配
    FILTER_FAIL_UnPredictReq("23"),        // 23 请求格式错误
    FILTER_FAIL_NormFail("24"),            // 24 归一化失败
    FILTER_FAIL_AppIdB("25"),              // 25 AppId B名单
    FILTER_FAIL_AppIdW("26"),              // 26 AppId W名单
    FILTER_FAIL_Bcat("27"),                // 27 bcat 过滤
    FILTER_FAIL_DevType("28"),             // 28 设备类型
    FILTER_FAIL_DevLang("29"),             // 29 语言
    FILTER_FAIL_Carrier("30"),             // 30 Carrier
    FILTER_FAIL_Country("31"),             // 31 Country
    FILTER_FAIL_FreCtrl("32"),             // 32 频次
    FILTER_FAIL_CityHunt("33"),            // 33 城市定向
    FILTER_FAIL_NoDevId("34"),             // 34 缺设备id
    FILTER_FAIL_NetwkId("35"),             // 35 渠道定投不匹配
    FILTER_FAIL_ConType("36"),             // 36 连接类型不匹配
    FILTER_FAIL_Version("37"),             // 37 版本 不匹配
    FILTER_FAIL_Make("38"),                // 38 make 不匹配
    FILTER_FAIL_Model("39"),               // 39 model 不匹配
    FILTER_FAIL_NoUaIp("40"),              // 40 缺少ua或ip
    FILTER_FAIL_GlobalW("41"),             // 41 全局白名单
    FILTER_FAIL_GlobalB("42"),             // 42 全局黑名单
    FILTER_FAIL_DevIdW("43"),              // 43 设备Id 白名单
    FILTER_FAIL_DevIdB("44"),              // 44 设备Id 黑名单
    FILTER_FAIL_BdCid("45"),               // 45 审核不通过
    FILTER_FAIL_NoBudget("46"),            // 46 超出预算
    FILTER_FAIL_LatLung("47"),             // 47 经纬度定向
    FILTER_FAIL_LowPrice("48"),            // 48 出价过低
    FILTER_FAIL_BadFormat("49"),           // 49 请求格式错误，同23  todo
    FILTER_FAIL_PC("50"),                  // 50 PC 请求
    FILTER_FAIL_NATIVE_TITLE("51"),        // 51 原生标题过滤
    FILTER_FAIL_NATIVE_IMAGE("52"),        // 52 原生图片不符
    FILTER_FAIL_NATIVE_IMAGELOGO("53"),    // 53 原生LOGO尺寸不符
    FILTER_FAIL_NATIVE_IMAGEMAIN("54"),    // 54 原生main 尺寸不符
    FILTER_FAIL_NATIVE_IMAGEICON("55"),    // 55 原生 ICON尺寸不符
    FILTER_FAIL_NATIVE_DATA("56"),         // 56 原生data不符
    FILTER_FAIL_NATIVE_OTHER("57"),        // 57 原生其他
    FILTER_FAIL_BTYPE("58"),               // 58 banner type 过滤
    FILTER_FAIL_MeidaType("59"),           // 59 媒体定投不匹配mex的 wcat2
    FILTER_FAIL_APPMeidaType("60"),        // 60 不投app (media_type)
    FILTER_FAIL_WebMeidaType("61"),        // 61 不投web (media_type)
    FILTER_FAIL_NotRichMeida("62"),        // 62 请求不支持富媒体
    FILTER_FAIL_IDFA("63"),                // 63 devId 设备id格式校验
    FILTER_FAIL_DURATION("64"),            // 64 视频播放时间不匹配
    FILTER_FAIL_FILE_TYPE("65"),           // 65 视频文件类型不匹配
    FILTER_FAIL_NATIVE_TIEBA("66"),        // 66 for tieba
    FILTER_FAIL_WCAT("67"),                // 67 WCAT 为空,mopub 需要
    FILTER_FAIL_RedisConnError("68"),      // 68 redis 连接出错
    FILTER_FAIL_PAYMODE("69"),             // 69 paymode 模式不匹配
    FILTER_FAIL_BIDMODE("70"),             // 70 bidmode模N式不匹配
    FILTER_FAIL_RESPONSEMODE("71"),        // 71 response匹配规则出错
    FILTER_FAIL_HTTPS("72"),               // 72 HTTPS 不匹配
    FILTER_FAIL_APP_NAME("73"),             // APP NAME过滤
    FILTER_FAIL_GEO_POSITION("74"),         // 地理位置过滤
    FILTER_FAIL_CARRIER("75"),             //运营商过滤
    NATIVE_FAIL_EMPTY("76"),               //native 为空
    FILTER_FAIL_BUDGET_FACING("77"),        //匀速投放暂停中
    FILTER_FAIL_DEEPLINK("78"),            //deeplink不匹配
    FILTER_FAIL_DMP_NOT_FOUND("79"),        //DMP不匹配
    FILTER_FAIL_DMP_TIMEOUT("80")          //DMP超时
    ;


    public String val;

    FilterErrors(String value) {
        this.val = value;
    }
}
