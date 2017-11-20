package com.mex.bidder.adx.gy;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Stopwatch;
import com.google.common.net.MediaType;
import com.google.openrtb.OpenRtb;
import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.protobuf.format.JsonFormat;
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
 * gy 请求接收处理
 * <p>
 * User: donghai
 * Date: 2016/11/15
 */
@Singleton
public class GyRequestReceiver extends BidRequestReceiver<BidRequest, BidResponse> {
    private static final Logger logger = LoggerFactory.getLogger(GyRequestReceiver.class);

    private final Meter successResponseWithAdsMeter;
    private final Meter successResponseNoAdsMeter;

    private final Clock clock;
    private GyOpenRtbMapper mapper;
    private GyMacroProcessor snippetProcessor;
    private LoggerService loggerService;

    @Inject
    public GyRequestReceiver(
            MetricRegistry metricRegistry,
            GyOpenRtbMapper mapper,
            GyMacroProcessor snippetProcessor,
            BidController controller,
            LoggerService loggerService,
            Clock clock) {
        super(GyExchange.INSTANCE, metricRegistry, controller);

//        GyAdxOpenRTB.registerAllExtensions(registry);
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
            logger.error("Bad gy request: {}", e.toString());
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

                // 映射到Gy的相应对象
                GyOpenRtb.BidResponse.Builder gyResponse = mapper.toExchangeBidResponse(openrtbRequest, openrtbResponse.build());


                (gyResponse.getSeatbidCount() == 0
                        ? successResponseNoAdsMeter
                        : successResponseWithAdsMeter
                ).mark();
                successResponseMeter().mark();


                GyOpenRtb.BidResponse gyBidResponse = gyResponse.build();

                HttpUtil.setStatusOk(ctx.response());
                HttpUtil.setMediaType(ctx.response(), MediaType.PROTOBUF);
                ctx.response().end(Buffer.buffer(gyBidResponse.toByteArray()));

                long end = clock.nanoTime();
                int processingTimeMs = (int) ((end - start) / 1000000);

                // 记录响应日志
                loggerService.sendRequestLog(request, response);
                requestLog(request);
                loggerService.sendResponseLog(response, processingTimeMs, request);
                responseLog(response, processingTimeMs);
                responseRawLog(JsonFormat.printToString(gyBidResponse), GyExchange.INSTANCE);
                logger.info("ProcessingTimeMs " + processingTimeMs + " ms");
            } else {
                logger.error("gy error ", res.cause());
                ctx.response().setStatusCode(500).end();
            }
            clearRequestId();
        };

        return handler;
    }


    private BidRequest newRequest(RoutingContext ctx) throws InvalidProtocolBufferException {

        @Nullable Buffer body = ctx.getBody();

        if (body == null) {
            throw new InvalidProtocolBufferException("gy parse bid request error");
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        GyOpenRtb.BidRequest gyBidRequest = GyOpenRtb.BidRequest.parseFrom(body.getBytes());
        // 打印原始日志
        requestRawLog(JsonFormat.printToString(gyBidRequest), GyExchange.INSTANCE);

        logger.info("id=" + gyBidRequest.getId());
        stopwatch.stop();
        logger.info("Gy parseFrom bidequest time " + stopwatch);

        BidRequest bidRequest = BidRequest.newBuilder()
                .setHttpRequest(ctx.request())
                .setExchange(getExchange())
                .setNativeRequest(gyBidRequest)
                .setRequest(mapper.toOpenRtbBidRequest(gyBidRequest)).build();


        return bidRequest;
    }

}
