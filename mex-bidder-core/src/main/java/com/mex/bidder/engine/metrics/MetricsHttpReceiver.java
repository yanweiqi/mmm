
package com.mex.bidder.engine.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.net.MediaType;
import com.mex.bidder.api.http.HttpReceiver;
import com.mex.bidder.engine.util.HttpUtil;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 处理监测请求
 */
public class MetricsHttpReceiver implements HttpReceiver, Handler<RoutingContext> {
    private static final Logger logger = LoggerFactory.getLogger(MetricsHttpReceiver.class);

    private final MetricRegistry registry;
    private final ObjectMapper mapperSamplesOff;
    private final ObjectMapper mapperSamplesOn;
    private final PrettyPrinter prettyPrinter;

    public static final String Key = "mex@123";

    @Inject
    public MetricsHttpReceiver(
            MetricRegistry registry,
            JsonFactory factory) {

        this.registry = checkNotNull(registry);
        this.mapperSamplesOff = new ObjectMapper(factory)
                .registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, false));
        this.mapperSamplesOn = new ObjectMapper(factory)
                .registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, true));
        this.prettyPrinter = new MetricsPrettyPrinter();
    }

    @Override
    public void receive(RoutingContext ctx) {
        boolean pretty = Boolean.parseBoolean(ctx.request().getParam("pretty"));
        boolean showSamples = Boolean.parseBoolean(ctx.request().getParam("samples"));

        // 查看监控要密钥 TODO 配置化
        String secretKey = ctx.request().getParam("secret");
        if (!Key.equals(secretKey)) {
            HttpUtil.setStatusOk(ctx.response()).end("empty");
            return;
        }

        try {
            ObjectMapper mapper = showSamples ? mapperSamplesOn : mapperSamplesOff;
            ObjectWriter writer = pretty ? mapper.writer(prettyPrinter) : mapper.writer();
            String valueAsString = writer.writeValueAsString(registry);

            HttpUtil.setStatusOk(ctx.response());
            HttpUtil.setMediaType(ctx.response(), MediaType.JSON_UTF_8)
                    .putHeader("Cache-Control", "must-revalidate,no-cache,no-store")
                    .end(valueAsString);
        } catch (IOException e) {
            logger.error("metrics data error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(RoutingContext event) {
        receive(event);
    }

    private static class MetricsPrettyPrinter extends DefaultPrettyPrinter {
        public MetricsPrettyPrinter() {
            super(DEFAULT_ROOT_VALUE_SEPARATOR);
        }

        public MetricsPrettyPrinter(MetricsPrettyPrinter pp) {
            super(pp);
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new MetricsPrettyPrinter(this);
        }

        @Override
        public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
            jg.writeRaw(": ");
        }

        @Override
        public void writeEndArray(JsonGenerator jg, int nrOfValues) throws IOException {
            if (!_arrayIndenter.isInline()) {
                --_nesting;
            }
            if (nrOfValues > 0) {
                _arrayIndenter.writeIndentation(jg, _nesting);
            }
            jg.writeRaw(']');
        }
    }
}
