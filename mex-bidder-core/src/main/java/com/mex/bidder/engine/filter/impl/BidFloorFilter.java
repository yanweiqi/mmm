package com.mex.bidder.engine.filter.impl;

import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.engine.util.BidPrice;
import com.mex.bidder.protocol.AdxData;
import com.mex.bidder.protocol.AdxDict;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 出价过低过滤
 * 能到这一步，必须有展示
 * <p>
 * User: donghai
 * Date: 2016/11/25
 */
public class BidFloorFilter implements SimpleAdFilter {

    @Inject
    private MexDataContext mexDataContext;

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {
        AdxData adxData = mexDataContext.getAdxDataByExchange(bidRequest.getExchange());
        AdxDict adxDict = mexDataContext.getAdxDictByExchange(bidRequest.getExchange());

        BigDecimal adBidPrice = BidPrice.calcPrice(ad, adxDict);
        BigDecimal bidfloor = getBidfloor(bidRequest);

        if (bidfloor.compareTo(adBidPrice) > 0) {
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_LowPrice);
            return true;
        } else {
            return false;
        }
    }

    private BigDecimal getBidfloor(BidRequest bidRequest) {
        OpenRtb.BidRequest.Imp imp = bidRequest.openRtb().getImp(0);
        if (imp.hasBidfloor()) {
            return BigDecimal.valueOf(imp.getBidfloor());
        } else {
            return BigDecimal.ZERO;
        }
    }
}
