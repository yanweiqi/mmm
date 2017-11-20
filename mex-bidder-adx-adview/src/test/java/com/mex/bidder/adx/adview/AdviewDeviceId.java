package com.mex.bidder.adx.adview;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.util.RtbHelper;
import com.mex.bidder.util.JsonHelper;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

/**
 * xuchuahao
 * on 2017/6/29.
 */
public class AdviewDeviceId {

    public AdviewOpenRtbMapper mapper;

    @Before
    public void setUp() {
        mapper = new AdviewOpenRtbMapper();
    }

    @Test
    public void getDeviceId() {
        String requestData = JsonHelper.readFile("adview-deviceid.json");
        BidRequest.Builder builder = BidRequest.newBuilder();
        Exchange exchange = new Exchange("") {
            @Override
            public Object newNativeResponse() {
                return null;
            }
        };

        BidRequest bidRequest = builder
                .setHttpRequest(new MyHttpServerRequest())
                .setExchange(exchange)
                .setNativeRequest(requestData)
                .setRequest(mapper.toOpenRtbBidRequest(requestData)).build();
        String deviceId = RtbHelper.getDeviceId(bidRequest);
        System.out.println("deviceid=" + deviceId);

    }

    public static class MyHttpServerRequest implements HttpServerRequest{

        @Override
        public HttpServerRequest exceptionHandler(Handler<Throwable> handler) {
            return null;
        }

        @Override
        public HttpServerRequest handler(Handler<Buffer> handler) {
            return null;
        }

        @Override
        public HttpServerRequest pause() {
            return null;
        }

        @Override
        public HttpServerRequest resume() {
            return null;
        }

        @Override
        public HttpServerRequest endHandler(Handler<Void> handler) {
            return null;
        }

        @Override
        public HttpVersion version() {
            return null;
        }

        @Override
        public HttpMethod method() {
            return null;
        }

        @Override
        public String rawMethod() {
            return null;
        }

        @Override
        public boolean isSSL() {
            return false;
        }

        @Override
        public @Nullable String scheme() {
            return null;
        }

        @Override
        public String uri() {
            return null;
        }

        @Override
        public @Nullable String path() {
            return null;
        }

        @Override
        public @Nullable String query() {
            return null;
        }

        @Override
        public @Nullable String host() {
            return null;
        }

        @Override
        public HttpServerResponse response() {
            return null;
        }

        @Override
        public MultiMap headers() {
            return null;
        }

        @Override
        public @Nullable String getHeader(String s) {
            return null;
        }

        @Override
        public String getHeader(CharSequence charSequence) {
            return null;
        }

        @Override
        public MultiMap params() {
            return null;
        }

        @Override
        public @Nullable String getParam(String s) {
            return null;
        }

        @Override
        public SocketAddress remoteAddress() {
            return null;
        }

        @Override
        public SocketAddress localAddress() {
            return null;
        }

        @Override
        public X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException {
            return new X509Certificate[0];
        }

        @Override
        public String absoluteURI() {
            return null;
        }

        @Override
        public NetSocket netSocket() {
            return null;
        }

        @Override
        public HttpServerRequest setExpectMultipart(boolean b) {
            return null;
        }

        @Override
        public boolean isExpectMultipart() {
            return false;
        }

        @Override
        public HttpServerRequest uploadHandler(@Nullable Handler<HttpServerFileUpload> handler) {
            return null;
        }

        @Override
        public MultiMap formAttributes() {
            return null;
        }

        @Override
        public @Nullable String getFormAttribute(String s) {
            return null;
        }

        @Override
        public ServerWebSocket upgrade() {
            return null;
        }

        @Override
        public boolean isEnded() {
            return false;
        }

        @Override
        public HttpServerRequest customFrameHandler(Handler<HttpFrame> handler) {
            return null;
        }

        @Override
        public HttpConnection connection() {
            return null;
        }
    }
}
