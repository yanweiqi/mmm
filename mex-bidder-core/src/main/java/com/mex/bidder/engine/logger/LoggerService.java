package com.mex.bidder.engine.logger;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;

import java.util.Map;

/**
 * 日志服务
 * User: donghai
 * Date: 2016/11/22
 */
public interface LoggerService {

//    public void sendRequestLog(BidRequest request);

    public void sendRequestLog(BidRequest request,BidResponse response);

    public void sendResponseLog(BidResponse response, int processingTimeMs,BidRequest request);

}
