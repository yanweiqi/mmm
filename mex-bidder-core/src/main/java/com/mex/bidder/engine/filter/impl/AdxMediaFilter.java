package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.openrtb.OpenRtb;
import com.google.protobuf.ProtocolStringList;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.AdxDict;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/26.
 */
public class AdxMediaFilter implements SimpleAdFilter {

    @Inject
    private MexDataContext mexDataContext;

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {
        String mediaCat = ad.getMediaCat();
        // 没有媒体定向
        if(Strings.isNullOrEmpty(mediaCat)){
            return false;
        }

        OpenRtb.BidRequest.App app = bidRequest.openRtb().getApp();
        ProtocolStringList catList = bidRequest.openRtb().getApp().getCatList();

        AdxDict adxDictByExchange = mexDataContext.getAdxDictByExchange(bidRequest.getExchange());
        Map<String, String> mediaCat1 = adxDictByExchange.getMediaCat();
        if(app.getCatCount() == 0){
            // 请求无app类型，过滤
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_MeidaType);
           return true;
        }

        for (String cat : catList){
            String ss = mediaCat1.get(cat);
            if (mediaCat.contains(ss+";")){
                return false;
            } else {
                bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_MeidaType);
                return true;
            }
        }



        return false;
    }
}
