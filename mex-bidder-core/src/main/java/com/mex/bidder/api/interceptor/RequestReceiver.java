package com.mex.bidder.api.interceptor;

import com.codahale.metrics.*;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.http.HttpReceiver;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.util.RtbHelper;
import io.vertx.ext.web.RoutingContext;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 基本请求接收器，子类实现具体adx的请求接收处理
 */
public abstract class RequestReceiver<C extends InterceptorController<?, ?>>
        implements HttpReceiver {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    private static final Logger bidRequestLogger = Logger.getLogger("BidRequestLog");
    private static final Logger bidResponseLogger = Logger.getLogger("BidResponseLog");
    protected static final Logger bidRequestRawLogger = Logger.getLogger("BidRequestRawLog");
    public static final Logger bidResponseRawLogger = Logger.getLogger("BidResponseRawLog");

    private final C controller;
    private final MetricRegistry metricRegistry;
    private final Exchange exchange;
    private final Meter successResponseMeter;
    private final Meter interceptorAbortMeter;
    private final Meter interceptorOtherMeter;
    private final Timer requestTimer;

    protected RequestReceiver(Exchange exchange, MetricRegistry metricRegistry, C controller) {
        this.exchange = exchange;
        this.controller = controller;
        this.metricRegistry = metricRegistry;

        successResponseMeter = buildMeter(exchange.getId() + "-success-response");
        interceptorAbortMeter = buildMeter(exchange.getId() + "-interceptor-abort-exceptions");
        interceptorOtherMeter = buildMeter(exchange.getId() + "-interceptor-exceptions");
        requestTimer = buildTimer(exchange.getId() + "-request-timer");
    }

    /**
     * Create a {@link Timer} for this receiver.
     */
    protected Timer buildTimer(String name) {
        return metricRegistry.register(MetricRegistry.name(getClass(), name), new Timer());
    }

    /**
     * Create a {@link Meter} for this receiver.
     */
    protected Meter buildMeter(String name) {
        return metricRegistry.register(MetricRegistry.name(getClass(), name), new Meter());
    }

    protected Histogram buildHistogram(String name) {
        return metricRegistry.register(
                MetricRegistry.name(getClass(), name), new Histogram(new UniformReservoir()));
    }


    protected final C controller() {
        return controller;
    }

    protected final MetricRegistry metricRegistry() {
        return metricRegistry;
    }

    protected Exchange getExchange() {
        return exchange;
    }

    protected final Meter successResponseMeter() {
        return successResponseMeter;
    }

    protected final Meter interceptorAbortMeter() {
        return interceptorAbortMeter;
    }

    protected final Meter interceptorOtherMeter() {
        return interceptorOtherMeter;
    }

    protected final Timer requestTimer() {
        return requestTimer;
    }

    protected final void setRequestId(String id) {
        MDC.put("requestId", "<" + id + "> ");
    }

    protected final void clearRequestId() {
        MDC.remove("requestId");
    }


    protected final void requestLog(BidRequest request) {
        String id = request.getExchange().getId();
        OpenRtb.BidRequest bidRequest = request.openRtb();
        bidRequestLogger.info(id + "||" + RtbHelper.openRtbBidRequestToJson(bidRequest));
    }


    protected void responseLog(BidResponse response, int time) {
        String id = response.getExchange().getId();
        OpenRtb.BidResponse build = response.openRtb().build();
        bidResponseLogger.info(id + "||" + time + "||" + RtbHelper.openRtbBidResponseToJson(build));
    }


    protected final void requestRawLog(String request, Exchange exchange) {
        String replace = request.replace("\r\n", "").replace(" ", "");
        bidRequestRawLogger.info(exchange.getId() + "||" + replace);
    }

    protected final void responseRawLog(String response, Exchange exchange) {
        bidResponseRawLogger.info(exchange.getId() + "||" + response);
    }

    protected BidResponse.Builder newResponse(RoutingContext ctx) {
        return BidResponse.newBuilder()
                .setExchange(getExchange())
                .setHttpResponse(ctx.response());
    }

}
