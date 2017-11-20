package com.mex.bidder.engine.filter.impl;

import com.google.inject.Inject;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.BudgetPacingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 匀速投放的过滤功能
 * <p>
 * xuchuahao
 * on 2017/5/4.
 */
public class BudgetFacingFilter implements SimpleAdFilter {

    private static final Logger logger = LoggerFactory.getLogger(BudgetFacingFilter.class);

    @Inject
    private MexDataContext mexDataContext;

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {

        if (ad.isBudgetPacing()) {
            BudgetPacingData budgetPacingData = mexDataContext.getBudgetPacingData();
            if (budgetPacingData.isBudgetPause(ad.getAdGroupId()+"")) {
                // true 暂停
                bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_BUDGET_FACING);
                logger.info("adCampaignId=" + ad.getAdCampaignId() + ", adgroupid=" + ad.getAdGroupId()
                        + ", isBudgetPause=" + ad.isBudgetPause() + ", reqid=" + bidRequest.openRtb().getId());
                return true;
            }
        }
        return false;
    }
}
