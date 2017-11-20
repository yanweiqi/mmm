package com.mex.bidder.api.http;

import com.mex.bidder.api.platform.Exchange;

/**
 * Exchange 渠道URL映射到Receiver的关系
 */
public class ExchangeRoute {

    private final HttpReceiver httpReceiver;
    private final Exchange exchange;

    public ExchangeRoute(Exchange exchange, HttpReceiver httpReceiver) {
        this.httpReceiver = httpReceiver;
        this.exchange = exchange;
    }

    public static ExchangeRoute create(Exchange exchange, HttpReceiver httpReceiver) {
        return new ExchangeRoute(exchange, httpReceiver);
    }

    public HttpReceiver getHttpReceiver() {
        return httpReceiver;
    }

    public String getPath() {
        return exchange.getId();
    }

    public Exchange getExchange(){
        return exchange;
    }
}
