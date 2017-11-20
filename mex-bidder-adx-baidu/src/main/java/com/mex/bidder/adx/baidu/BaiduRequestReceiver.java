package com.mex.bidder.adx.baidu;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Stopwatch;
import com.google.common.net.MediaType;
import com.google.openrtb.OpenRtb;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mex.bidder.api.bidding.BidController;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidRequestReceiver;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.interceptor.InterceptorAbortException;
import com.mex.bidder.api.mapper.MapperException;
import com.mex.bidder.engine.logger.LoggerService;
import com.mex.bidder.engine.util.HttpUtil;
import com.mex.bidder.util.Clock;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Baidu 请求接收处理
 * <p>
 * User: donghai
 * Date: 2016/11/15
 */
@Singleton
public class BaiduRequestReceiver extends BidRequestReceiver<BidRequest, BidResponse> {
    private static final Logger logger = LoggerFactory.getLogger(BaiduRequestReceiver.class);

    private final Meter successResponseWithAdsMeter;
    private final Meter successResponseNoAdsMeter;

    private final Clock clock;
    private BaiduOpenRtbMapper mapper;
    private BaiduMacroProcessor snippetProcessor;
    private LoggerService loggerService;

    @Inject
    public BaiduRequestReceiver(
            MetricRegistry metricRegistry,
            BaiduOpenRtbMapper mapper,
            BaiduMacroProcessor snippetProcessor,
            BidController controller,
            LoggerService loggerService,
            Clock clock) {
        super(BaiduExchange.INSTANCE, metricRegistry, controller);

        this.clock = clock;
        this.mapper = mapper;
        this.snippetProcessor = snippetProcessor;
        this.loggerService = loggerService;
        this.successResponseNoAdsMeter = buildMeter("success-response-no-ads");
        this.successResponseWithAdsMeter = buildMeter("success-response-with-ads");
    }

    @Override
    public void receive(RoutingContext ctx) {
        boolean unhandledException = true;
        Timer.Context timerContext = requestTimer().time();

        try {
            long start = clock.nanoTime();

            BidRequest request = newRequest(ctx);
            BidResponse response = newResponse(ctx).build();
            setRequestId(request.openRtb().getId());
            Future<AsyncResult<Void>> handleBidRequestFutures = handleBidRequest(request, response);
            handleBidRequestFutures.setHandler(getAsyncResultHandler(ctx, start, request, response));

        } catch (InvalidProtocolBufferException e) {
            logger.error("Bad Baidu request: {}", e.toString());
            ctx.response().setStatusCode(400).end();
        } catch (Exception e) {
            logger.error("error ", e);
            ctx.response().setStatusCode(500).end();
        } finally {
            if (unhandledException) {
                interceptorOtherMeter().mark();
            }
            timerContext.close();
        }
    }

    private Future<AsyncResult<Void>> handleBidRequest(BidRequest request, BidResponse response) {
        Future<AsyncResult<Void>> result = Future.future();
        try {
            // 业务处理
            controller().onRequest(request, response, rs -> {
                result.complete(Future.succeededFuture());
            });
        } catch (InterceptorAbortException e) {
            logger.error("InterceptorAbortException thrown", e);
            interceptorAbortMeter().mark();
            result.complete(Future.failedFuture(e));
        } catch (MapperException e) {
            logger.error(e.toString());
            result.complete(Future.failedFuture(e));
        }

        return result;
    }

    private Handler<AsyncResult<AsyncResult<Void>>> getAsyncResultHandler(RoutingContext ctx, long start, BidRequest request, BidResponse response) {
        Handler<AsyncResult<AsyncResult<Void>>> handler = res -> {
            if (res.succeeded()) {

                OpenRtb.BidRequest openrtbRequest = request.openRtb();
                OpenRtb.BidResponse.Builder openrtbResponse = response.openRtb();
                if (!openrtbResponse.hasId()) {
                    openrtbResponse.setId(openrtbRequest.getId());
                }

                // 宏处理
                snippetProcessor.process(request, response);

                // 映射到Baidu的相应对象
                BaiduRtb.BidResponse.Builder baiduResponse = mapper.toExchangeBidResponse(openrtbRequest, openrtbResponse.build());

                (baiduResponse.getAdCount() == 0
                        ? successResponseNoAdsMeter
                        : successResponseWithAdsMeter
                ).mark();

                successResponseMeter().mark();


                BaiduRtb.BidResponse baiduBidResponse = baiduResponse.build();

                HttpUtil.setStatusOk(ctx.response());
                HttpUtil.setMediaType(ctx.response(), MediaType.PROTOBUF);
                ctx.response().end(Buffer.buffer(baiduBidResponse.toByteArray()));
                long end = clock.nanoTime();
                int processingTimeMs = (int) ((end - start) / 1000000);
                //Step 5. 记录响应日志  TODO
                // 记录请求日志
                loggerService.sendRequestLog(request, response);
                requestLog(request);

                loggerService.sendResponseLog(response, processingTimeMs, request);
                responseLog(response, processingTimeMs);
                logger.info("ProcessingTimeMs " + processingTimeMs + " ms");
            } else {
                logger.error("baidu error ", res.cause());
                ctx.response().setStatusCode(500).end();
            }
            clearRequestId();
        };

        return handler;
    }


    private BidRequest newRequest(RoutingContext ctx) throws InvalidProtocolBufferException {

        @Nullable Buffer body = ctx.getBody();

        if (body == null) {
            throw new InvalidProtocolBufferException("Baidu parse bid request error");
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        byte[] requestData = body.getBytes();
        BaiduRtb.BidRequest bidRequest = BaiduRtb.BidRequest.parseFrom(requestData);
        stopwatch.stop();
        logger.info("Baidu parseFrom bidequest time " + stopwatch);

        return BidRequest.newBuilder()
                .setHttpRequest(ctx.request())
                .setExchange(getExchange())
                .setNativeRequest(bidRequest)
                .setRequest(mapper.toOpenRtbBidRequest(bidRequest)).build();
    }

}
