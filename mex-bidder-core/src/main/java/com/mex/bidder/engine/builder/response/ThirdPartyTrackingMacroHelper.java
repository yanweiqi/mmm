package com.mex.bidder.engine.builder.response;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.engine.util.HttpUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理第三方监测宏替换
 * <p>
 * user: donghai
 * date: 2017/4/21
 */
public class ThirdPartyTrackingMacroHelper {
    public static MexResponseBuilder.ThirdPartyTrackerUrl replaceMacro(List<String> impTrackingUrls,
                                                                       List<String> clickTrackingUrls,
                                                                       Map<String, List<String>> paramMap,
                                                                       BidRequest request) {
        List<String> impUrlsWithMacro = impTrackingUrls.stream().map(url -> addMacro(url, paramMap, request)).collect(Collectors.toList());
        List<String> clickUrlsWithMacro = clickTrackingUrls.stream().map(url -> addMacro(url, paramMap, request)).collect(Collectors.toList());
        return MexResponseBuilder.ThirdPartyTrackerUrl.build(impUrlsWithMacro, clickUrlsWithMacro);
    }

    private static String addMacro(String url, Map<String, List<String>> paramMap, BidRequest request) {
        return HttpUtil.redirectUrl(url, paramMap);
    }
}
