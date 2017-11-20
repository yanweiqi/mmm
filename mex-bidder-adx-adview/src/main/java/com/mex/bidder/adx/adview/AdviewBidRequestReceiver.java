package com.mex.bidder.adx.adview;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.net.MediaType;
import com.google.openrtb.OpenRtb;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mex.bidder.api.bidding.BidController;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.interceptor.InterceptorAbortException;
import com.mex.bidder.api.interceptor.RequestReceiver;
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

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdviewBidRequestReceiver extends RequestReceiver<BidController> {

    private final Meter successResponseWithAdsMeter;
    private final Meter successResponseNoAdsMeter;

    private final LoggerService loggerService;
    private final AdviewOpenRtbMapper mapper;
    private final Clock clock;
    private final AdviewMacroProcessor macroProcessor;


    @Inject
    public AdviewBidRequestReceiver(
            MetricRegistry metricsRegistry,
            LoggerService loggerService,
            AdviewOpenRtbMapper mapper,
            AdviewMacroProcessor macroProcessor,
            BidController controller,
            Clock clock) {
        super(AdViewExchange.INSTANCE, metricsRegistry, controller);

        this.loggerService = loggerService;
        this.mapper = mapper;
        this.clock = clock;
        this.macroProcessor = macroProcessor;
        this.successResponseNoAdsMeter = buildMeter("success-response-no-ads");
        this.successResponseWithAdsMeter = buildMeter("success-response-with-ads");
    }

    @Override
    public void receive(RoutingContext ctx) {

        boolean unhandledException = true;
        Timer.Context timerContext = requestTimer().time();

        try {
            long start = clock.nanoTime();

            // Step 1. 将http请求映射到OpenRTB协议上
            BidRequest request = newRequest(ctx);
            BidResponse response = newResponse(ctx).build();
            setRequestId(request.openRtb().getId()); // log4j中加入请求ID, 后面要清除

            // Step 2. 在请求上运行业务逻辑链，异步返回
            Future<AsyncResult<Void>> handleBidRequestFutures = handleBidRequest(request, response);
            handleBidRequestFutures.setHandler(getAsyncResultHandler(ctx, start, request, response));

            unhandledException = false;
        } catch (InvalidProtocolBufferException e) {
            logger.error("Bad adview request: {}", e.toString());
            ctx.response().setStatusCode(400).end();
        } catch (Exception e) {
            logger.error("other error ", e);
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
                macroProcessor.process(request, response);

                // 映射到adview的相应对象
                String adviewResponse = mapper.toExchangeBidResponse(openrtbRequest, openrtbResponse.build());

                (Strings.isNullOrEmpty(adviewResponse)
                        ? successResponseNoAdsMeter
                        : successResponseWithAdsMeter
                ).mark();
                successResponseMeter().mark();


                // Step 4. 处理响应
                if (response.getResponseMode() == BidResponse.ResponseMode.NONE) {
                    HttpUtil.setStatusCode(ctx.response(), 204);
                    ctx.response().end();
                } else {
                    HttpUtil.setStatusOk(ctx.response());
                    HttpUtil.setMediaType(ctx.response(), MediaType.JSON_UTF_8);
                    ctx.response().end(adviewResponse);
                }

                long end = clock.nanoTime();
                int processingTimeMs = (int) ((end - start) / 1000000);

                //Step 5. 记录日志
                loggerService.sendRequestLog(request, response);
                requestLog(request);
                loggerService.sendResponseLog(response, processingTimeMs, request);
                responseLog(response, processingTimeMs);
                responseRawLog(adviewResponse, AdViewExchange.INSTANCE);
                logger.info("ProcessingTimeMs " + processingTimeMs + " ms");
            } else {
                logger.error("adview error ", res.cause());
                ctx.response().setStatusCode(500).end();
            }
            clearRequestId();
        };

        return handler;
    }

    private BidRequest newRequest(RoutingContext ctx) throws InvalidProtocolBufferException {

        @Nullable Buffer body = ctx.getBody();

        if (body == null) {
            throw new InvalidProtocolBufferException("adview parse bid request error");
        }
        String requestData = new String(body.getBytes());
        requestRawLog(requestData, AdViewExchange.INSTANCE);

        return BidRequest.newBuilder()
                .setHttpRequest(ctx.request())
                .setExchange(getExchange())
                .setNativeRequest(requestData)
                .setRequest(mapper.toOpenRtbBidRequest(requestData)).build();
    }

}
