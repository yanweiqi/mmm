package com.mex.bidder.adx.zplay;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Stopwatch;
import com.google.common.net.MediaType;
import com.google.openrtb.OpenRtb;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mex.bidder.api.bidding.BidController;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidRequestReceiver;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.interceptor.InterceptorAbortException;
import com.mex.bidder.api.mapper.MapperException;
import com.mex.bidder.engine.ParseException;
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
 * zplay 请求接收处理
 * <p>
 * User: donghai
 * Date: 2016/11/15
 */
@Singleton
public class ZplayRequestReceiver extends BidRequestReceiver<BidRequest, BidResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ZplayRequestReceiver.class);

    private static final ExtensionRegistry registry = ExtensionRegistry.newInstance();
    private final Meter successResponseWithAdsMeter;
    private final Meter successResponseNoAdsMeter;

    private final Clock clock;
    private ZplayOpenRtbJsonMapper mapper;
    private ZplayMacroProcessor snippetProcessor;
    private LoggerService loggerService;


    @Inject
    public ZplayRequestReceiver(
            MetricRegistry metricRegistry,
            ZplayOpenRtbJsonMapper mapper,
            ZplayMacroProcessor snippetProcessor,
            BidController controller,
            LoggerService loggerService,
            Clock clock) {
        super(ZplayExchange.INSTANCE, metricRegistry, controller);

        ZadxExt.registerAllExtensions(registry);
        this.clock = clock;
        this.mapper = mapper;
        this.snippetProcessor = snippetProcessor;
        this.loggerService = loggerService;
        this.successResponseNoAdsMeter = buildMeter("success-response-no-ads");
        this.successResponseWithAdsMeter = buildMeter("success-response-with-ads");
    }

    @Override
    public void receive(RoutingContext ctx) {
        Timer.Context timerContext = requestTimer().time();

        try {
            logger.info(" in zplay receive........................................");
            long start = clock.nanoTime();
            BidRequest request = newRequest(ctx);
            BidResponse response = newResponse(ctx).build();
            setRequestId(request.openRtb().getId());
            Future<AsyncResult<Void>> handleBidRequestFutures = handleBidRequest(request, response);
            handleBidRequestFutures.setHandler(getAsyncResultHandler(ctx, start, request, response));
            logger.info(" out zplay receive........................................");
        } catch (ParseException e) {
            logger.error("Bad zplay request: {}", e.toString());
            ctx.response().setStatusCode(400).end();
        } catch (Exception e) {
            logger.error("zply error ", e);
            ctx.response().setStatusCode(500).end();
        } finally {
            timerContext.close();
        }
    }

        private Handler<AsyncResult<AsyncResult<Void>>> getAsyncResultHandler(RoutingContext ctx, long start, BidRequest request, BidResponse response) {
        return res -> {
            if (res.succeeded()) {
                logger.info("1........................................" + response.getResponseMode());
                OpenRtb.BidRequest openrtbRequest = request.openRtb();
                OpenRtb.BidResponse.Builder openrtbResponse = response.openRtb();
                if (!openrtbResponse.hasId()) {
                    openrtbResponse.setId(openrtbRequest.getId());
                }

                // 宏处理
                logger.info(" in zplay snippetProcessor........................................" + response.getResponseMode());
                snippetProcessor.process(request, response);

                // 映射到Zplay的相应对象
                String zplayResponse = mapper.toExchangeBidResponse(openrtbRequest, openrtbResponse.build());
                (response.getResponseMode() != BidResponse.ResponseMode.NONE
                        ? successResponseWithAdsMeter
                        : successResponseNoAdsMeter
                ).mark();

                successResponseMeter().mark();

                HttpUtil.setStatusOk(ctx.response());
                HttpUtil.setMediaType(ctx.response(), MediaType.PROTOBUF);
                ctx.response().end(Buffer.buffer(zplayResponse));

                long end = clock.nanoTime();
                int processingTimeMs = (int) ((end - start) / 1000000);

                // 记录请求日志
                loggerService.sendRequestLog(request, response);
                requestLog(request);
                //Step 5. 记录响应日志
                loggerService.sendResponseLog(response, processingTimeMs, request);
                responseLog(response, processingTimeMs);
                responseRawLog(zplayResponse, ZplayExchange.INSTANCE);
                logger.info("ProcessingTimeMs " + processingTimeMs + " ms");
                logger.info(" out zplay receive........................................");
            } else {
                logger.error("zply error ", res.cause());
                ctx.response().setStatusCode(500).end();
            }
            clearRequestId();
        };
    }


    private Future<AsyncResult<Void>> handleBidRequest(BidRequest request, BidResponse response) {
        Future<AsyncResult<Void>> result = Future.future();

        OpenRtb.BidRequest zplayRequest = request.openRtb();

        if (zplayRequest.getExtension(ZadxExt.isPing)) {
            result.complete(Future.succeededFuture());
            return result;
        }
        try {
            logger.info(" in zplay handleBidRequest........................................");
            controller().onRequest(request, response, rs -> {
                logger.info(" in zplay controller callback........................................");
                result.complete(Future.succeededFuture());
            });
        } catch (InterceptorAbortException e) {
            logger.error("InterceptorAbortException thrown", e);
            interceptorAbortMeter().mark();
            result.complete(Future.failedFuture(e));
        } catch (MapperException e) {
            result.complete(Future.failedFuture(e));
            logger.error(e.toString());
        }

        return result;
    }

    private BidRequest newRequest(RoutingContext ctx) throws InvalidProtocolBufferException {

        @Nullable Buffer body = ctx.getBody();
        if (body == null) {
            throw new ParseException("zplay parse bid request error");
        }
        String requestData = new String(body.getBytes());

        requestRawLog(requestData, ZplayExchange.INSTANCE);
        Stopwatch stopwatch = Stopwatch.createStarted();
        stopwatch.stop();
        logger.info("Zplay parseFrom bidequest time " + stopwatch);

        return BidRequest.newBuilder()
                .setHttpRequest(ctx.request())
                .setExchange(getExchange())
                .setNativeRequest(requestData)
                .setRequest(mapper.toOpenRtbBidRequest(requestData)).build();
    }

}
