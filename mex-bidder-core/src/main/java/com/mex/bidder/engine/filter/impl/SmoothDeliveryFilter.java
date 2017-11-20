package com.mex.bidder.engine.filter.impl;

import com.google.api.client.util.Lists;
import com.google.inject.Inject;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.AdListFilter;
import com.mex.bidder.protocol.Ad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * xuchuahao
 * on 2017/7/7.
 */
public class SmoothDeliveryFilter implements AdListFilter {

    private static final Logger logger = LoggerFactory.getLogger(SmoothDeliveryFilter.class);

    @Inject
    private MexDataContext mexDataContext;

    static final Random random = new Random();

    @Override
    public <B extends Ad> List<B> filter(List<B> adList, BidRequest bidRequest, BidResponse bidResponse) {

        if (adList.size() == 0) {
            return adList;
        }

        int rate = random.nextInt(100);
        // <groupid ,  ptr>  // 当前可投放，且标识为匀速投放的groupid
        Map<Integer, Integer> data = mexDataContext.getAdPtrData().getData();
        // <groupid , adList> // 当前可投放，且经过filter,根据groupid分组后的groupid
        HashMap<Integer, ArrayList<B>> collect = adList.stream().
                collect(Collectors.groupingBy(Ad::getAdGroupId, HashMap::new, Collectors.toCollection(ArrayList::new)));
        data.forEach((groupid, ptr) -> {
            eliminate(groupid, ptr, rate, collect, bidResponse);
        });
//        List<B> collect1 = collect.values().stream().map(a -> a.get(0)).collect(Collectors.toList());

        ArrayList<B> list = Lists.newArrayList();
        collect.values().forEach(li -> {
            list.addAll(li);
        });
        return list;
    }

    //剔除候选广告中包含匀速投放，且概率超过ptr的组
    private <B extends Ad> void eliminate(Integer groupid, Integer ptr, int rate, HashMap<Integer, ArrayList<B>> collect, BidResponse bidResponse) {
        //ptr = 0 ;投放活动开启，但是不在当前匀速投放时间范围内
        if (collect.containsKey(groupid) && (ptr == 0 || rate > ptr)) {
            // 候选集合中包含匀速投放的广告组，且概率大于ptr
            logger.info("eliminate groupid=" + groupid + ", rate=" + rate + ", ptr=" + ptr);
            ArrayList<B> adList = collect.remove(groupid);
            bidResponse.addFilterError(adList.get(0).getCode(), FilterErrors.FILTER_FAIL_BUDGET_FACING);
        } else {
            // do nothing
            logger.info("pass groupid=" + groupid + ", rate=" + rate + ", ptr=" + ptr);
        }
    }


}