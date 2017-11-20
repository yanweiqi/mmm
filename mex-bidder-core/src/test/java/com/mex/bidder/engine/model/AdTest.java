package com.mex.bidder.engine.model;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

/**
 * User: donghai
 * Date: 2016/11/18
 */
public class AdTest {

    String data = "{\n" +
            "  \"file_url\": \"https://res.ad-mex.com/dspres/upload/20161107/e9c9cb5f-cc79-4708-a12f-842f338e3ee5.jpg\",\n" +
            "  \"filetype\": \"jpg\",\n" +
            "  \"country\": \"\",\n" +
            "  \"curl\": \"http://ad.doubleclick.net/ddm/trackclk/N5050.2207MEX/B10540012.140609209;dc_trk_aid=312827054;dc_trk_cid=75884096;dc_muid=[dc_muid]?http://alyres.ad-mex.com/landpages/nchx/\",\n" +
            "  \"app_white_list\": \"\",\n" +
            "  \"language\": \"\",\n" +
            "  \"device_type\": \"1;2\",\n" +
            "  \"bid_type\": \"RTB\",\n" +
            "  \"file_url_abroad\": \"https://res.ad-mex.com/dspres/upload/20161107/e9c9cb5f-cc79-4708-a12f-842f338e3ee5.jpg\",\n" +
            "  \"creative_id\": \"193-129-1039-9110\",\n" +
            "  \"frequency\": \"false\",\n" +
            "  \"frequency_imp_unit\": \"d\",\n" +
            "  \"cpa\": -1,\n" +
            "  \"media_type\": \"0\",\n" +
            "  \"price\": 6,\n" +
            "  \"cur_adv\": \"CNY\",\n" +
            "  \"cpc\": 6,\n" +
            "  \"cpc_rmb\": 6,\n" +
            "  \"cpc_usd\": 0.9231,\n" +
            "  \"model\": \"\",\n" +
            "  \"os_version_min\": \"-\",\n" +
            "  \"campaign_id\": 129,\n" +
            "  \"height\": 90,\n" +
            "  \"frequency_imp_number\": 8592,\n" +
            "  \"bidmode\": \"1\",\n" +
            "  \"wcat2\": \"\",\n" +
            "  \"os_version_max\": \"\",\n" +
            "  \"frequency_imp\": \"true\",\n" +
            "  \"bcreative_id\": 9110,\n" +
            "  \"manufacture\": \"\",\n" +
            "  \"target_os\": \"ios\",\n" +
            "  \"network_id\": \"025\",\n" +
            "  \"cat2\": \"[{\\\"002\\\":\\\"6\\\"},{\\\"003\\\":\\\"5402\\\"},{\\\"023\\\":\\\"71112\\\"},{\\\"019\\\":\\\"5402\\\"},{\\\"013\\\":\\\"210\\\"},{\\\"004\\\":\\\"19\\\"},{\\\"024\\\":\\\"2901\\\"},{\\\"025\\\":\\\"1\\\"},{\\\"乐视\\\":\\\"6\\\"},{\\\"031\\\":\\\"6\\\"}]\",\n" +
            "  \"devicegroup_type\": \"none\",\n" +
            "  \"ecpm\": -1,\n" +
            "  \"ctype\": \"1\",\n" +
            "  \"mvgeoids\": \"1156360100;\",\n" +
            "  \"price_type\": \"ecpm\",\n" +
            "  \"frequency_click\": \"false\",\n" +
            "  \"price_usd\": 6,\n" +
            "  \"price_rmb\": 6,\n" +
            "  \"code\": \"193-129-1039-9110\",\n" +
            "  \"adgroup_id\": 1039,\n" +
            "  \"ecpm_rmb\": -1,\n" +
            "  \"ecpm_usd\": -1,\n" +
            "  \"iurl_2\": \"\",\n" +
            "  \"iurl_1\": \"http://ad.doubleclick.net/ddm/trackimp/N5050.2207MEX/B10540012.140609209;dc_trk_aid=312827054;dc_trk_cid=75884096;dc_muid=[dc_muid];ord=[timestamp]?\",\n" +
            "  \"people_orient\": \"[]\",\n" +
            "  \"advertiser_id\": 193,\n" +
            "  \"display_type\": \"2\",\n" +
            "  \"material_type\": \"0\",\n" +
            "  \"wh\": \"728*90\",\n" +
            "  \"need_deviceId\": \"no\",\n" +
            "  \"adv_domain_name\": \"http://dealer.autohome.com.cn/127222\",\n" +
            "  \"paymode\": 1,\n" +
            "  \"creative_type\": \"2\",\n" +
            "  \"file_url_cn\": \"https://res.ad-mex.com/dspres/upload/20161107/e9c9cb5f-cc79-4708-a12f-842f338e3ee5.jpg\",\n" +
            "  \"connectiontype\": \"5;6;2\",\n" +
            "  \"exts\": {\n" +
            "    \"richmedia\": {}\n" +
            "  },\n" +
            "  \"carrier_types\": \"\",\n" +
            "  \"website\": \"http://dealer.autohome.com.cn/127222\",\n" +
            "  \"app_url\": \"http://alyres.ad-mex.com/landpages/nchx/?\",\n" +
            "  \"deviceid_flag\": \"no\",\n" +
            "  \"os_version\": \"\",\n" +
            "  \"deviceid_type\": \"unlimited\",\n" +
            "  \"lnglats\": [],\n" +
            "  \"cpa_usd\": -1,\n" +
            "  \"app_name\": \"一汽大众-南昌一路\",\n" +
            "  \"app_black_list\": \"\",\n" +
            "  \"carrier\": \"\",\n" +
            "  \"account_id\": 193,\n" +
            "  \"deviceid_file_value\": \"\",\n" +
            "  \"cpa_rmb\": -1,\n" +
            "  \"width\": 728,\n" +
            "  \"netid\": \"025\",\n" +
            "  \"imp\": {\n" +
            "    \"banner\": {\n" +
            "      \"w\": 728,\n" +
            "      \"h\": 90\n" +
            "    },\n" +
            "    \"bidfloor\": 6,\n" +
            "    \"bidfloorcur\": \"CNY\"\n" +
            "  },\n" +
            "  \"device\": {\n" +
            "    \"devicetype\": \"1;2\",\n" +
            "    \"os\": \"ios\",\n" +
            "    \"osv\": \"-\",\n" +
            "    \"connectiontype\": \"5;6;2\",\n" +
            "    \"ip\": \"1156360100;\",\n" +
            "    \"geo\": []\n" +
            "  },\n" +
            "  \"site\": {\n" +
            "    \"cat\": \"\"\n" +
            "  },\n" +
            "  \"city_level\": {}\n" +
            "}";

    @Test
    public void parse() {
       // Ad ad = JSON.parseObject(data, Ad.class);

       // System.out.println(JSON.toJSON(ad));
    }
}