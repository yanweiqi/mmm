package com.mex.bidder.api.interceptor;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.api.platform.NoExchange;
import io.vertx.core.http.HttpServerResponse;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Bidder response to the exchange / user-agent.
 *
 * @see Interceptor
 */
public abstract class UserResponse<R extends UserResponse<?>> {
    private final Exchange exchange;
    private final HttpServerResponse httpResponse;
    private final Map<String, Object> metadata = new LinkedHashMap<>();

    protected UserResponse(Exchange exchange, HttpServerResponse httpResponseBuilder) {
        this.exchange = checkNotNull(exchange);
        this.httpResponse = checkNotNull(httpResponseBuilder);
    }

    @SuppressWarnings("unchecked")
    protected final R self() {
        return (R) this;
    }

    public abstract Builder<?> toBuilder();


    public Exchange getExchange() {
        return exchange;
    }


    public final HttpServerResponse httpResponse() {
        return httpResponse;
    }


    public final Map<String, Object> metadata() {
        return metadata;
    }

    public R putMetadata(String key, Object value) {
        metadata.put(key, value);
        return self();
    }

    public R putAllMetadata(Map<String, Object> metadata) {
        this.metadata.putAll(metadata);
        return self();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("exchange", exchange)
                .add("metadata", metadata);
    }

    @Override
    public final String toString() {
        return toStringHelper().toString();
    }

    /**
     * Builder for {@link UserResponse}.
     */
    public static abstract class Builder<B extends Builder<B>> {
        private Exchange exchange;
        private HttpServerResponse httpResponse;

        protected final B self() {
            @SuppressWarnings("unchecked")
            B self = (B) this;
            return self;
        }

        public
        @Nullable
        Exchange getExchange() {
            return exchange;
        }

        public B setExchange(@Nullable Exchange exchange) {
            this.exchange = exchange;
            return self();
        }

        public
        @Nullable
        HttpServerResponse getHttpResponse() {
            return httpResponse;
        }

        public B setHttpResponse(@Nullable HttpServerResponse httpResponse) {
            this.httpResponse = httpResponse;
            return self();
        }

        protected Exchange defaultExchange() {
            return NoExchange.INSTANCE;
        }

        protected int defaultStatusCode() {
            return 200;
            //return HttpStatus.SC_OK;
        }

        public abstract UserResponse<?> build();

        protected ToStringHelper toStringHelper() {
            return MoreObjects.toStringHelper(this).omitNullValues()
                    .add("exchange", exchange)
                    .add("httpResponse", httpResponse);
        }

        @Override
        public final String toString() {
            return toStringHelper().toString();
        }
    }
}
