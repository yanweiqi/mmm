package com.mex.bidder.engine.logger.impl;

import com.alibaba.fastjson.JSON;
import com.google.api.client.util.Maps;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.json.OpenRtbJsonWriter;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.json.MexOpenRtbJsonFactory;
import com.mex.bidder.engine.logger.LoggerService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * log4j实现的logger
 * User: donghai
 * Date: 2016/11/22
 */
public class Log4jLogger implements LoggerService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Log4jLogger.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger bidRequestLogger = Logger.getLogger("BidRequestLog");
    private static final Logger bidResponseLogger = Logger.getLogger("BidResponseLog");

    private OpenRtbJsonWriter openRtbJsonWriter;

    public Log4jLogger() {
        MexOpenRtbJsonFactory mexOpenRtbJsonFactory = MexOpenRtbJsonFactory.create();
        openRtbJsonWriter = mexOpenRtbJsonFactory.newWriter();
    }


  /*  @Override
    public void sendRequestLog(BidRequest request) {

        Map<String, Object> logMap = Maps.newHashMap();
        logMap.put("log", "request");
        logMap.put("time", DateTime.now().toString(DATE_FORMAT));
        logMap.put("netid", request.getExchange().getId());

        String bidRequestJson = "";
        try {
            bidRequestJson = openRtbJsonWriter.writeBidRequest(request.openRtb());
        } catch (IOException e) {
            logger.error(request.getExchange().getId() + " write to json error.", e);
        }

        logMap.put("raw", JSON.parseObject(bidRequestJson));

        bidRequestLogger.info(JSON.toJSONString(logMap));
    }*/

    @Override
    public void sendRequestLog(BidRequest request, BidResponse response) {

    }

    @Override
    public void sendResponseLog(BidResponse response, int processingTimeMs, BidRequest  request) {

        OpenRtb.BidResponse bidResponse = response.openRtb().build();

        Map<String, Object> logMap = Maps.newHashMap();
        logMap.put("log", "request");
        logMap.put("time", DateTime.now().toString(DATE_FORMAT));
        logMap.put("netid", response.getExchange().getId());
        logMap.put("ptime", processingTimeMs);

        bidResponseLogger.info(JSON.toJSONString(logMap));
    }
}
