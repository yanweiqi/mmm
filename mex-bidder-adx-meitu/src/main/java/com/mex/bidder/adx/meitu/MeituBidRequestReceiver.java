package com.mex.bidder.adx.meitu;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Stopwatch;
import com.google.common.net.MediaType;
import com.google.inject.Inject;
import com.google.openrtb.OpenRtb;
import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.protobuf.format.JsonFormat;
import com.meitu.openrtb.MeituOpenRtb;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * xuchuahao
 * on 2017/6/12.
 */
@Singleton
public class MeituBidRequestReceiver extends RequestReceiver<BidController> {
    private static final Logger logger = LoggerFactory.getLogger(MeituBidRequestReceiver.class);

    private final Meter successResponseWithAdsMeter;
    private final Meter successResponseNoAdsMeter;
    private final Clock clock;
    private final MeituOpenRtbMapper mapper;
    private final MeituMacroProcessor macroProcessor;
    private final LoggerService loggerService;

    @Inject
    protected MeituBidRequestReceiver(
            MetricRegistry metricRegistry,
            LoggerService loggerService,
            Clock clock, MeituOpenRtbMapper mapper,
            MeituMacroProcessor macroProcessor,
            BidController controller) {
        super(MeituExchange.INSTANCE, metricRegistry, controller);
        this.clock = clock;
        this.mapper = mapper;
        this.macroProcessor = macroProcessor;
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

            // Step 1. 将http请求映射到OpenRTB协议上
            BidRequest request = newRequest(ctx);
            BidResponse response = newResponse(ctx).build();
            setRequestId(request.openRtb().getId());

            // Step 2. 在请求上运行业务逻辑链，异步返回
            Future<AsyncResult<Void>> handleBidRequestFutures = handleBidRequest(request, response);
            handleBidRequestFutures.setHandler(getAsyncResultHandler(ctx, start, request, response));

        } catch (InvalidProtocolBufferException e) {
            logger.error("Bad meitu request: {}", e.toString());
            ctx.response().setStatusCode(400).end();
        } catch (Exception e) {
            logger.error("error ", e);
            ctx.response().setStatusCode(500).end();
        } finally {
            if (unhandledException) {
                interceptorOtherMeter().mark();
            }
            timerContext.close();
            clearRequestId();
        }
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
                MeituOpenRtb.BidResponse.Builder meituResponse = mapper.toExchangeBidResponse(openrtbRequest, openrtbResponse.build());


                (meituResponse.getSeatbidCount() == 0
                        ? successResponseNoAdsMeter
                        : successResponseWithAdsMeter
                ).mark();
                successResponseMeter().mark();


               /* // Step 4. 处理响应
                if (response.getResponseMode() == BidResponse.ResponseMode.NONE) {
                    HttpUtil.setStatusCode(ctx.response(), 204);
                    ctx.response().end();
                } else {
                    HttpUtil.setStatusOk(ctx.response());
                    HttpUtil.setMediaType(ctx.response(), MediaType.JSON_UTF_8);
                    ctx.response().end(adviewResponse);
                }
*/
                // Step 4. 处理响应
                HttpUtil.setStatusOk(ctx.response());
                HttpUtil.setMediaType(ctx.response(), MediaType.JSON_UTF_8);
                ctx.response().end(Buffer.buffer(meituResponse.build().toByteArray()));

                long end = clock.nanoTime();
                int processingTimeMs = (int) ((end - start) / 1000000);

                //Step 5. 记录日志
                loggerService.sendRequestLog(request, response);
                requestLog(request);
                loggerService.sendResponseLog(response, processingTimeMs, request);
                responseLog(response, processingTimeMs);
                responseRawLog(JsonFormat.printToString(meituResponse.build()), MeituExchange.INSTANCE);
                logger.info("ProcessingTimeMs " + processingTimeMs + " ms");
            } else {
                logger.error("adview error ", res.cause());
                ctx.response().setStatusCode(500).end();
            }
            clearRequestId();
        };

        return handler;
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


    /*private MeituOpenRtb.BidResponse.Builder handleBidRequest(BidRequest request, BidResponse response) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            controller().onRequest(request, response);
            dontSetCookies(response);

            OpenRtb.BidResponse.Builder openrtbResponse = response.openRtb();
            if (!openrtbResponse.hasId()) {
                openrtbResponse.setId(request.openRtb().getId());
            }
            // 宏处理
            macroProcessor.process(request, response);

            // 映射到meitu的相应对象
            MeituOpenRtb.BidResponse.Builder meituResponse = mapper.toExchangeBidResponse(request.openRtb(), response.openRtb().build());

            (meituResponse.getSeatbidCount() == 0
                    ? successResponseNoAdsMeter
                    : successResponseWithAdsMeter
            ).mark();
            successResponseMeter().mark();

            stopwatch.stop();
            logger.info("handle bid request time {}", stopwatch);
            return meituResponse;

        } catch (InterceptorAbortException e) {
            logger.error("InterceptorAbortException thrown", e);
            interceptorAbortMeter().mark();
            return MeituOpenRtb.BidResponse.newBuilder();
        } catch (MapperException e) {
            logger.error(e.toString());
            return MeituOpenRtb.BidResponse.newBuilder();
        }
    }*/


    private BidRequest newRequest(RoutingContext ctx) throws InvalidProtocolBufferException {
        @Nullable Buffer body = ctx.getBody();
        if (body == null) {
            throw new InvalidProtocolBufferException("meitu parse bid request error");
        }
        MeituOpenRtb.BidRequest meituBidRequest = MeituOpenRtb.BidRequest.parseFrom(body.getBytes());
        OpenRtb.BidRequest.Builder builder = mapper.toOpenRtbBidRequest(meituBidRequest);

        return BidRequest.newBuilder()
                .setHttpRequest(ctx.request())
                .setExchange(getExchange())
                .setNativeRequest(meituBidRequest)
                .setRequest(builder).build();
    }
}
