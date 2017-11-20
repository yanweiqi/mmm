package com.mex.bidder.api.interceptor;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.api.platform.NoExchange;
import io.vertx.core.http.HttpServerRequest;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class UserRequest {
    private final Exchange exchange;
    private final HttpServerRequest httpRequest;

    protected UserRequest(Exchange exchange, HttpServerRequest httpRequest) {
        this.exchange = checkNotNull(exchange);
        this.httpRequest = checkNotNull(httpRequest);
    }

    public abstract Builder<?> toBuilder();

    /**
     * Returns the transport request.
     */
    public HttpServerRequest httpRequest() {
        return httpRequest;
    }

    /**
     * @return Exchange from which the bid request originated
     */
    public final Exchange getExchange() {
        return exchange;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("exchange", exchange);
    }

    @Override
    public final String toString() {
        return toStringHelper().toString();
    }

    /**
     * Builder for {@link UserRequest}.
     */
    public static abstract class Builder<B extends Builder<B>> {
        private Exchange exchange;
        private HttpServerRequest httpRequest;

        protected B self() {
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
        HttpServerRequest getHttpRequest() {
            return httpRequest;
        }

        public B setHttpRequest(@Nullable HttpServerRequest httpRequest) {
            this.httpRequest = httpRequest;
            return self();
        }

        protected final HttpServerRequest builtHttpRequest() {
            return httpRequest;
        }

        protected Exchange defaultExchange() {
            return NoExchange.INSTANCE;
        }

        public abstract UserRequest build();

        protected ToStringHelper toStringHelper() {
            return MoreObjects.toStringHelper(this).omitNullValues()
                    .add("exchange", exchange)
                    .add("httpRequest", httpRequest);
        }

        @Override
        public final String toString() {
            return toStringHelper().toString();
        }
    }
}
