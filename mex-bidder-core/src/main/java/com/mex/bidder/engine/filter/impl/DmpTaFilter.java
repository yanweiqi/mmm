package com.mex.bidder.engine.filter.impl;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Lists;
import com.google.inject.Inject;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.constants.Constants;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.dmp.impl.GetuiDmpServiceImpl;
import com.mex.bidder.engine.filter.AsyncAdListFilter;
import com.mex.bidder.protocol.Ad;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 对接dmp人群定向
 * <p>
 * user: donghai
 * date: 2017/6/14
 */
public class DmpTaFilter implements AsyncAdListFilter {
    private static final Logger logger = LoggerFactory.getLogger(DmpTaFilter.class);

    private GetuiDmpServiceImpl dmpService;

    @Inject
    public DmpTaFilter(GetuiDmpServiceImpl dmpService) {
        this.dmpService = dmpService;
    }

    @Override
    public <B extends Ad> Future<List<B>> filter(List<B> adList, BidRequest bidRequest, BidResponse bidResponse) {

        Future<List<B>> result = Future.future();
        Map<String, List<B>> geTuiTaskIdToAdListMap = adList.stream()
                .collect(Collectors.groupingBy(Ad::getGetuiTaskId, HashMap::new, Collectors.toCollection(ArrayList::new)));
        if (geTuiTaskIdToAdListMap.size() == 0 || (geTuiTaskIdToAdListMap.containsKey(Constants.GETUI_NULL)
                && geTuiTaskIdToAdListMap.size() == 1)) {
            // 无DMP ta定向直接返回
            result.complete(adList);
            return result;
        } else {
            return dmpService.retrieve(bidRequest).compose(dmpDataHandler(bidResponse, result, geTuiTaskIdToAdListMap));
        }
    }

    private <B extends Ad> Function<AsyncResult<List<String>>, Future<List<B>>> dmpDataHandler(BidResponse bidResponse, Future<List<B>> result, Map<String, List<B>> geTuiTaskIdToAdListMap) {
        return res -> {
            List<B> resultAdList = Lists.newArrayList();

            if (res.succeeded()) {
                List<String> taskList = res.result();
                geTuiTaskIdToAdListMap.forEach((k, listAd) -> {
                    if (StringUtils.isNotEmpty(k) && taskList.contains(k)) {
                        resultAdList.addAll(listAd);
                    } else if (Constants.GETUI_NULL.equals(k)) {
                        resultAdList.addAll(listAd);
                    } else {
                        listAd.forEach(ad -> bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_DMP_NOT_FOUND));
                    }
                });
            } else {
                logger.warn("dmp query msg->" + res.cause().getMessage());
                geTuiTaskIdToAdListMap.forEach((k, listAd) -> {
                    if (Strings.isNullOrEmpty(k)) {
                        listAd.forEach(ad -> bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_DMP_TIMEOUT));
                    }
                });
                //查询的无对应TA组，过滤全部带有个推标签的广告
                if (geTuiTaskIdToAdListMap.get(Constants.GETUI_NULL) != null) {
                    resultAdList.addAll(geTuiTaskIdToAdListMap.get(Constants.GETUI_NULL));
                }
            }
            result.complete(resultAdList);
            return result;
        };
    }


}
