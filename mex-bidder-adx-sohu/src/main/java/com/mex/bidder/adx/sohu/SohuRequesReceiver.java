package com.mex.bidder.adx.sohu;

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

/**
 * xuchuahao
 * on 2017/3/20.
 */
public class SohuRequesReceiver extends BidRequestReceiver<BidRequest, BidResponse> {

    private static final Logger logger = LoggerFactory.getLogger(SohuRequesReceiver.class);

    private final Meter successResponseWithAdsMeter;
    private final Meter successResponseNoAdsMeter;

    private final Clock clock;
    private SohuOpenRtbMapper mapper;
    private SohuMacroProcessor sohuMacroProcessor;
    private LoggerService loggerService;


    @Inject
    public SohuRequesReceiver(MetricRegistry metricRegistry,
                              BidController controller,
                              Clock clock,
                              SohuOpenRtbMapper mapper,
                              SohuMacroProcessor sohuMacroProcessor,
                              LoggerService loggerService) {
        super(SohuExchange.INSTANCE, metricRegistry, controller);
        this.clock = clock;
        this.mapper = mapper;
        this.sohuMacroProcessor = sohuMacroProcessor;
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

            @Nullable String isDebug = ctx.request().getParam("debug");

            BidRequest request = newRequest(ctx);
            BidResponse response = newResponse(ctx).build();
            setRequestId(request.openRtb().getId());
            Future<AsyncResult<Void>> future = handleBidRequest(request, response);
            future.setHandler(getAsyncResultHandler(ctx, start, request, response));
        } catch (InvalidProtocolBufferException e) {
            logger.error("Bad sohu request: {}", e.toString());
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
                sohuMacroProcessor.process(request, response);

                // 映射到Gy的相应对象
                SohuOpenRtb.Response.Builder responseBuilder = mapper.toExchangeBidResponse(openrtbRequest, openrtbResponse.build());
                SohuOpenRtb.Response sohuBidResponse = responseBuilder.build();

                (sohuBidResponse.getSeatbidCount() == 0
                        ? successResponseNoAdsMeter
                        : successResponseWithAdsMeter
                ).mark();

                successResponseMeter().mark();

                HttpUtil.setStatusOk(ctx.response());
                HttpUtil.setMediaType(ctx.response(), MediaType.PROTOBUF);
                ctx.response().end(Buffer.buffer(sohuBidResponse.toByteArray()));

                long end = clock.nanoTime();
                int processingTimeMs = (int) ((end - start) / 1000000);
                // 记录响应日志
                loggerService.sendRequestLog(request, response);
                requestLog(request);
                loggerService.sendResponseLog(response, processingTimeMs, request);
                responseLog(response, processingTimeMs);
                responseRawLog(JsonFormat.printToString(sohuBidResponse), SohuExchange.INSTANCE);
                logger.info("ProcessingTimeMs " + processingTimeMs + " ms");
            } else {
                logger.error("sohu error ", res.cause());
                ctx.response().setStatusCode(500).end();
            }
            clearRequestId();
        };
        return handler;
    }


    protected BidRequest newRequest(RoutingContext ctx) throws InvalidProtocolBufferException {

        @Nullable Buffer body = ctx.getBody();

        if (body == null) {
            throw new InvalidProtocolBufferException("Sohu parse bid request error");
        }

        Stopwatch stopwatch = Stopwatch.createStarted();

        SohuOpenRtb.Request sohuBidRequest = SohuOpenRtb.Request.parseFrom(body.getBytes());
        // 打印原始日志
        requestRawLog(JsonFormat.printToString(sohuBidRequest), SohuExchange.INSTANCE);

        logger.info("id=" + sohuBidRequest.getBidid());
        stopwatch.stop();
        logger.info("Sohu parseFrom bidequest time " + stopwatch);

        BidRequest bidRequest = BidRequest.newBuilder()
                .setHttpRequest(ctx.request())
                .setExchange(getExchange())
                .setNativeRequest(sohuBidRequest)
                .setRequest(mapper.toOpenRtbBidRequest(sohuBidRequest)).build();

        return bidRequest;
    }
}
