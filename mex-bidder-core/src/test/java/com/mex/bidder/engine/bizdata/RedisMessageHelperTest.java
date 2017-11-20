package com.mex.bidder.engine.bizdata;

import com.mex.bidder.protocol.Ad;
import io.vertx.core.json.Json;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: donghai
 * Date: 2016/11/18
 */
public class RedisMessageHelperTest {
    Map<String, String> idToNameMap = Json.decodeValue("{\"022\":\"adsautohome\",\"025\":\"adsiflytek\",\"004\":\"adsview\",\"027\":\"adspcautoapp\",\"028\":\"adspcautowap\",\"029\":\"adstencent\",\"018\":\"adszp\",\"030\":\"adssina\"}", HashMap.class);

    String creativeData = "{\"file_url\":\"https://res.ad-mex.com/dspres/upload/20161114/2c7af06a-0218-4b21-b8d1-3a74cfc42e93.jpg\",\"filetype\":\"jpg\",\"country\":\"\",\"curl\":\"http://ad.doubleclick.net/ddm/trackclk/N5050.2207MEX/B10539542.140618609;dc_trk_aid=312838307;dc_trk_cid=75882742;dc_muid=[dc_muid]?http://alyres.ad-mex.com/landpages/xmmz/\",\"app_white_list\":\"\",\"language\":\"\",\"device_type\":\"1;2\",\"file_url_abroad\":\"https://res.ad-mex.com/dspres/upload/20161114/2c7af06a-0218-4b21-b8d1-3a74cfc42e93.jpg\",\"creative_id\":\"196-124-1087-9334\",\"frequency\":\"false\",\"frequency_imp_unit\":\"d\",\"cpa\":-1,\"media_type\":\"0\",\"price\":25,\"cur_adv\":\"CNY\",\"cpc\":25,\"cpc_rmb\":25,\"cpc_usd\":3.8462,\"model\":\"\",\"os_version_min\":\"-\",\"campaign_id\":124,\"height\":500,\"frequency_imp_number\":2109,\"bidmode\":\"1\",\"wcat2\":\"\",\"os_version_max\":\"\",\"frequency_imp\":\"true\",\"bcreative_id\":9334,\"manufacture\":\"\",\"target_os\":\"Android\",\"network_id\":\"018\",\"cat2\":\"[{\\\"002\\\":\\\"6\\\"},{\\\"003\\\":\\\"5402\\\"},{\\\"023\\\":\\\"71112\\\"},{\\\"019\\\":\\\"5402\\\"},{\\\"013\\\":\\\"210\\\"},{\\\"004\\\":\\\"19\\\"},{\\\"024\\\":\\\"2901\\\"},{\\\"025\\\":\\\"1\\\"},{\\\"乐视\\\":\\\"6\\\"},{\\\"031\\\":\\\"6\\\"}]\",\"devicegroup_type\":\"none\",\"ecpm\":-1,\"ctype\":\"1\",\"mvgeoids\":\"1156350200;\",\"price_type\":\"ecpm\",\"frequency_click\":\"false\",\"price_usd\":25,\"price_rmb\":25,\"code\":\"196-124-1087-9334\",\"adgroup_id\":1087,\"ecpm_rmb\":-1,\"ecpm_usd\":-1,\"iurl_2\":\"\",\"iurl_1\":\"http://ad.doubleclick.net/ddm/trackimp/N5050.2207MEX/B10539542.140618609;dc_trk_aid=312838307;dc_trk_cid=75882742;dc_muid=${bidid};ord=[timestamp]?\",\"people_orient\":\"[]\",\"advertiser_id\":196,\"display_type\":\"2\",\"material_type\":\"0\",\"wh\":\"600*500\",\"need_deviceId\":\"no\",\"adv_domain_name\":\"http://vw.faw-vw.com/zh/cn.html\",\"paymode\":1,\"creative_type\":\"2\",\"file_url_cn\":\"https://res.ad-mex.com/dspres/upload/20161114/2c7af06a-0218-4b21-b8d1-3a74cfc42e93.jpg\",\"connectiontype\":\"5;6;2\",\"exts\":{\"richmedia\":{}},\"carrier_types\":\"\",\"website\":\"http://vw.faw-vw.com/zh/cn.html\",\"app_url\":\"http://alyres.ad-mex.com/landpages/xmmz/?\",\"deviceid_flag\":\"no\",\"os_version\":\"\",\"deviceid_type\":\"unlimited\",\"lnglats\":[],\"cpa_usd\":-1,\"app_name\":\"一汽大众-厦门明至\",\"app_black_list\":\"\",\"carrier\":\"\",\"account_id\":196,\"deviceid_file_value\":\"\",\"cpa_rmb\":-1,\"width\":600,\"netid\":\"018\",\"imp\":{\"banner\":{\"w\":600,\"h\":500},\"bidfloor\":25,\"bidfloorcur\":\"CNY\"},\"device\":{\"devicetype\":\"1;2\",\"os\":\"Android\",\"osv\":\"-\",\"connectiontype\":\"5;6;2\",\"ip\":\"1156350200;\",\"geo\":[]},\"site\":{\"cat\":\"\"},\"city_level\":{}}\n" +
            "{\"file_url\":\"https://res.ad-mex.com/dspres/upload/20161114/4acff889-18e1-449e-89a2-a1750b668b8b.jpg\",\"filetype\":\"jpg\",\"country\":\"\",\"curl\":\"http://ad.doubleclick.net/ddm/trackclk/N5050.2207MEX/B10539542.140618609;dc_trk_aid=312838307;dc_trk_cid=75882742;dc_muid=[dc_muid]?http://alyres.ad-mex.com/landpages/xmmz/\",\"app_white_list\":\"\",\"language\":\"\",\"device_type\":\"1;2\",\"file_url_abroad\":\"https://res.ad-mex.com/dspres/upload/20161114/4acff889-18e1-449e-89a2-a1750b668b8b.jpg\",\"creative_id\":\"196-124-1087-9335\",\"frequency\":\"false\",\"frequency_imp_unit\":\"d\",\"cpa\":-1,\"media_type\":\"0\",\"price\":25,\"cur_adv\":\"CNY\",\"cpc\":25,\"cpc_rmb\":25,\"cpc_usd\":3.8462,\"model\":\"\",\"os_version_min\":\"-\",\"campaign_id\":124,\"height\":250,\"frequency_imp_number\":2109,\"bidmode\":\"1\",\"wcat2\":\"\",\"os_version_max\":\"\",\"frequency_imp\":\"true\",\"bcreative_id\":9335,\"manufacture\":\"\",\"target_os\":\"Android\",\"network_id\":\"018\",\"cat2\":\"[{\\\"002\\\":\\\"6\\\"},{\\\"003\\\":\\\"5402\\\"},{\\\"023\\\":\\\"71112\\\"},{\\\"019\\\":\\\"5402\\\"},{\\\"013\\\":\\\"210\\\"},{\\\"004\\\":\\\"19\\\"},{\\\"024\\\":\\\"2901\\\"},{\\\"025\\\":\\\"1\\\"},{\\\"乐视\\\":\\\"6\\\"},{\\\"031\\\":\\\"6\\\"}]\",\"devicegroup_type\":\"none\",\"ecpm\":-1,\"ctype\":\"1\",\"mvgeoids\":\"1156350200;\",\"price_type\":\"ecpm\",\"frequency_click\":\"false\",\"price_usd\":25,\"price_rmb\":25,\"code\":\"196-124-1087-9335\",\"adgroup_id\":1087,\"ecpm_rmb\":-1,\"ecpm_usd\":-1,\"iurl_2\":\"\",\"iurl_1\":\"http://ad.doubleclick.net/ddm/trackimp/N5050.2207MEX/B10539542.140618609;dc_trk_aid=312838307;dc_trk_cid=75882742;dc_muid=${bidid};ord=[timestamp]?\",\"people_orient\":\"[]\",\"advertiser_id\":196,\"display_type\":\"2\",\"material_type\":\"0\",\"wh\":\"300*250\",\"need_deviceId\":\"no\",\"adv_domain_name\":\"http://vw.faw-vw.com/zh/cn.html\",\"paymode\":1,\"creative_type\":\"2\",\"file_url_cn\":\"https://res.ad-mex.com/dspres/upload/20161114/4acff889-18e1-449e-89a2-a1750b668b8b.jpg\",\"connectiontype\":\"5;6;2\",\"exts\":{\"richmedia\":{}},\"carrier_types\":\"\",\"website\":\"http://vw.faw-vw.com/zh/cn.html\",\"app_url\":\"http://alyres.ad-mex.com/landpages/xmmz/?\",\"deviceid_flag\":\"no\",\"os_version\":\"\",\"deviceid_type\":\"unlimited\",\"lnglats\":[],\"cpa_usd\":-1,\"app_name\":\"一汽大众-厦门明至\",\"app_black_list\":\"\",\"carrier\":\"\",\"account_id\":196,\"deviceid_file_value\":\"\",\"cpa_rmb\":-1,\"width\":300,\"netid\":\"018\",\"imp\":{\"banner\":{\"w\":300,\"h\":250},\"bidfloor\":25,\"bidfloorcur\":\"CNY\"},\"device\":{\"devicetype\":\"1;2\",\"os\":\"Android\",\"osv\":\"-\",\"connectiontype\":\"5;6;2\",\"ip\":\"1156350200;\",\"geo\":[]},\"site\":{\"cat\":\"\"},\"city_level\":{}}\n" +
            "3";

    @Test
    public void parseCreative() throws Exception {
        //Map<String, List<Ad>> stringMapMap = RedisMessageHelper.parseCreative(creativeData, idToNameMap);

       // System.out.println(stringMapMap.size());

    }

    @Test
    public void parseCreative2() throws Exception {
        //Map<String, List<Ad>> stringMapMap = RedisMessageHelper.parseCreative("{\"file_url\":\"\",\"netid\":\"022\"}\n3", idToNameMap);
        //Assert.assertTrue(stringMapMap.size() == 1);

        //stringMapMap = RedisMessageHelper.parseCreative("3", idToNameMap);
        //Assert.assertTrue(stringMapMap.size() == 0);

    }

}