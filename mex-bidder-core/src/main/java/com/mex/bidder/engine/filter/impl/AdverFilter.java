package com.mex.bidder.engine.filter.impl;

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
public class AdverFilter implements SimpleAdFilter {

    @Inject
    private MexDataContext mexDataContext;

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {
        if (bidRequest.openRtb().getBcatCount() == 0){
            return false;
        }

        ProtocolStringList bcatList = bidRequest.openRtb().getBcatList();

        String adverCat2 = ad.getAdverCat2();

        AdxDict adxDictByExchange = mexDataContext.getAdxDictByExchange(bidRequest.getExchange());
        Map<String, String> adverCat = adxDictByExchange.getAdverCat();

        for (String bcat : bcatList){
            String mexCat = adverCat.get(bcat);
            if (adverCat2.contains(mexCat+";")){
                return false;
            } else {
//                bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_OS);
                return true;
            }
        }

        return false;
    }
}
