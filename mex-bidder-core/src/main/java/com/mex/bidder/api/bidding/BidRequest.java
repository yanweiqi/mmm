package com.mex.bidder.api.bidding;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video;
import com.google.openrtb.util.OpenRtbUtils;
import com.google.openrtb.util.ProtoUtils;
import com.google.protobuf.MessageLite;
import com.google.protobuf.TextFormat;
import com.mex.bidder.api.interceptor.UserRequest;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.model.IpBean;
import io.vertx.core.http.HttpServerRequest;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;


public class BidRequest extends UserRequest {
    private final Object nativeRequest;
    private final OpenRtb.BidRequest request;
    private IpBean ipBean;

    protected BidRequest(
            Exchange exchange, HttpServerRequest httpRequest,
            @Nullable Object nativeRequest, @Nullable OpenRtb.BidRequest request) {

        super(exchange, httpRequest);
        this.nativeRequest = nativeRequest;
        this.request = request;
    }

    public IpBean getIpBean() {
        return ipBean;
    }

    public void setIpBean(IpBean ipBean) {
        this.ipBean = ipBean;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return new Builder()
                .setExchange(getExchange())
                .setHttpRequest(httpRequest())
                .setNativeRequest(nativeRequest)
                .setRequest(request);
    }

    /**
     * @return Exchange specific bid request. The type will depend on {@link #getExchange()}
     * Some exchanges may provided this information, others may return {@code null} when
     * there's no value in providing the native request (e.g. if the exchange's native protocol
     * is OpenRTB-over-protobuf, the native request would be identical to {@link #openRtb()}).
     */
    public final
    @Nullable
    <T> T nativeRequest() {
        @SuppressWarnings("unchecked")
        T ret = (T) nativeRequest;
        return ret;
    }

    /**
     * @return The delegate OpenRTB bid request.  This may be {@code null} if the exchange does not
     * support OpenRTB, not even via mapping (or the mapping is disabled).  That should be an
     * uncommon scenario so this method is not Nullable, but if null it will throw an exception.
     * @throws IllegalStateException if the OpenRTB request is not available.
     */
    public final OpenRtb.BidRequest openRtb() {
        checkState(request != null, "OpenRTB request is not available");
        return request;
    }

    /**
     * Iterates all {@link Imp}s.
     *
     * @return All OpenRTB {@link Imp}s.
     */
    public final List<Imp> imps() {
        return openRtb().getImpList();
    }

    /**
     * Filters {@link Imp}s.
     *
     * @return All {@link Imp}s that pass a predicate.
     */
    public final Iterable<Imp> impsWith(Predicate<Imp> predicate) {
        return OpenRtbUtils.impsWith(openRtb(), predicate);
    }

    /**
     * Finds an {@link Imp} by ID.
     *
     * @return The {@link Imp}s that has the given id, or {@code null} if not found.
     */
    public final
    @Nullable
    Imp impWithId(final String id) {
        return OpenRtbUtils.impWithId(openRtb(), id);
    }

    /**
     * Iterate {@link Imp} that contain a {@link Banner}.
     *
     * @return All {@link Imp}s with a {@link Banner}s.
     */
    public final Iterable<Imp> bannerImps() {
        return bannerImpsWith(imp -> true);
    }


    public final Iterable<Imp> bannerImpsWith(Predicate<Imp> predicate) {
        return OpenRtbUtils.impsWith(openRtb(), predicate);
    }

    public final
    @Nullable
    Imp bannerImpWithId(@Nullable String impId, String bannerId) {
        return OpenRtbUtils.bannerImpWithId(openRtb(), impId, bannerId);
    }

    public final Iterable<Imp> videoImps() {
        return videoImpsWith(imp -> true);
    }

    /**
     * Filter {@link Imp}s that contain a {@link Video}.
     *
     * @param predicate Filters {@link Imp}s; will be invoked
     *                  exactly once and only on {@link Imp}s that contain a {@link Video}
     * @return All {@link Imp}s that pass the predicate.
     */
    public final Iterable<Imp> videoImpsWith(Predicate<Imp> predicate) {
        return OpenRtbUtils.impsWith(openRtb(), predicate);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        ToStringHelper tsr = super.toStringHelper();

        if (request != null) {
            tsr.add("request",
                    TextFormat.shortDebugString(ProtoUtils.filter(request, true, ProtoUtils.NOT_EXTENSION)));
        }

        return tsr;
    }

    /**
     * Builder for {@link UserRequest}.
     */
    public static class Builder extends UserRequest.Builder<Builder> {
        private Object nativeRequest;
        private OpenRtb.BidRequest.Builder request;

        protected Builder() {
        }

        public BidRequest.Builder setNativeRequest(@Nullable Object nativeRequest) {
            this.nativeRequest = nativeRequest;
            return self();
        }

        public
        @Nullable
        Object getNativeRequest() {
            return nativeRequest;
        }

        /**
         * Similar to {@link #getNativeRequest()}, but returns the "built"
         * object (the native request property remains unchanged).
         */
        protected Object builtNativeRequest() {
            // Supports protobuf builders; override as necessary for other builders.
            return nativeRequest instanceof MessageLite.Builder
                    ? ProtoUtils.built((MessageLite.Builder) nativeRequest)
                    : nativeRequest;
        }

        /**
         * Similar to {@link #getNativeRequest()}, but only allowed if the native request
         * was set (throws {@link NullPointerException} otherwise), and always returns a builder
         * (if the native request was set as a built object, it will be converted to builder).
         */
        protected Object nativeBuilder() {
            if (checkNotNull(nativeRequest) instanceof MessageLite) {
                nativeRequest = ProtoUtils.builder((MessageLite) nativeRequest);
            }
            return nativeRequest;
        }

        public BidRequest.Builder setRequest(@Nullable OpenRtb.BidRequestOrBuilder request) {
            this.request = ProtoUtils.builder(request);
            return self();
        }

        public
        @Nullable
        OpenRtb.BidRequest.Builder getRequest() {
            return request;
        }

        @Override
        public BidRequest build() {
            return new BidRequest(
                    MoreObjects.firstNonNull(getExchange(), defaultExchange()),
                    builtHttpRequest(),
                    builtNativeRequest(),
                    request == null ? null : request.build());
        }
    }
}
