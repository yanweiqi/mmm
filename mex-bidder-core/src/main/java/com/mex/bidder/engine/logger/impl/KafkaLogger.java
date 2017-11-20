package com.mex.bidder.engine.logger.impl;

import com.alibaba.fastjson.JSON;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Maps;
import com.google.inject.Inject;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.config.SysConf;
import com.mex.bidder.engine.bizdata.MexDataContext;
import com.mex.bidder.engine.constants.Constants;
import com.mex.bidder.engine.kafka.producer.KafkaPublisher;
import com.mex.bidder.engine.logger.LoggerService;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.engine.util.RtbHelper;
import com.mex.bidder.protocol.Ad;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * user: donghai
 * date: 2016/12/27
 */
public class KafkaLogger implements LoggerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaLogger.class);

    private final String rtbLogTopic;
    private final String newBidReqTopic;
    private final String newBidResTopic;

    @Inject
    private KafkaPublisher kafkaPublisher;

    @Inject
    public KafkaLogger(@SysConf JsonObject cnf) {
        rtbLogTopic = cnf.getString(Constants.KAFKA_KEY_RTB_TOPIC);
        JsonObject newProducerTopicsCnf = cnf.getJsonObject("new-producer-topics");

        newBidReqTopic = newProducerTopicsCnf.getString("bid-request-topic");
        Objects.requireNonNull(newBidReqTopic, "bid-request-topic can't be null");

        newBidResTopic = newProducerTopicsCnf.getString("bid-response-topic");
        Objects.requireNonNull(newBidResTopic, "bid-response-topic can't be null");

        Preconditions.checkArgument(rtbLogTopic != null, "topic can't be null");
    }

    @Override
    public void sendRequestLog(BidRequest request, BidResponse response) {
        OpenRtb.BidRequest bidRequest = request.openRtb();
        String id = bidRequest.getId();

        String mexBidRequestJson = RtbHelper.openRtbBidRequestToJson(bidRequest);
        kafkaPublisher.send(newBidReqTopic, id, mexBidRequestJson);
    }

    @Override
    public void sendResponseLog(BidResponse response, int processingTimeMs, BidRequest request) {

        OpenRtb.BidRequest bidRequest = request.openRtb();
        String bidRequestId = bidRequest.getId();

        String mexBidResponseJson = RtbHelper.openRtbBidResponseToJson(response.openRtb().build());
        kafkaPublisher.send(newBidResTopic, bidRequestId, mexBidResponseJson);


    }
}
