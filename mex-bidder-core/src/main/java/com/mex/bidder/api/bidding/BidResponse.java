package com.mex.bidder.api.bidding;


import com.google.api.client.util.Maps;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.BidOrBuilder;
import com.google.openrtb.OpenRtb.BidResponse.SeatBidOrBuilder;
import com.google.openrtb.util.OpenRtbUtils;
import com.google.openrtb.util.ProtoUtils;
import com.google.protobuf.TextFormat;
import com.mex.bidder.api.interceptor.UserResponse;
import com.mex.bidder.api.platform.Exchange;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.model.AdAndPricePair;
import com.mex.bidder.protocol.BaseTa;
import io.vertx.core.http.HttpServerResponse;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public class BidResponse extends UserResponse<BidResponse> {
    private OpenRtb.BidResponse.Builder response;
    private Object nativeResponse;
    private ResponseMode responseMode = ResponseMode.NONE;

    // 广告过滤中失败的原因 {Ad.code, FilterErrors}
    private Map<String, String> filterErrorMap = Maps.newHashMap();

    private AdAndPricePair adAndPricePair = AdAndPricePair.EMPTY;

    private BaseTa baseTa = BaseTa.EMPTY;

    protected BidResponse(Exchange exchange, HttpServerResponse httpResponseBuilder) {
        super(exchange, httpResponseBuilder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return newBuilder()
                .setExchange(getExchange())
                .setHttpResponse(httpResponse());
    }

    /**
     * @return The delegate OpenRTB bid response builder.
     * @throws IllegalStateException if the response builder was already set to native mode.
     */
    public final OpenRtb.BidResponse.Builder openRtb() {
        if (response == null) {
            if (nativeResponse == null) {
                response = OpenRtb.BidResponse.newBuilder();
            } else {
                throw new IllegalStateException("Native builder exists, cannot use openRtb()");
            }
        }
        return response;
    }

    public final <T> T nativeResponse() {
        if (nativeResponse == null) {
            if (response == null) {
                nativeResponse = getExchange().newNativeResponse();
            } else {
                throw new IllegalStateException("OpenRTB builder exists, cannot use nativeResponse()");
            }
        }
        @SuppressWarnings("unchecked")
        T ret = (T) nativeResponse;
        return ret;
    }

    /**
     * 当前响应是哪种类型，Banner, video, native
     */
    public final ResponseMode getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(ResponseMode responseMode) {
        this.responseMode = responseMode;
    }

    public final SeatBid.Builder seatBid(String seat) {
        return OpenRtbUtils.seatBid(openRtb(), seat);
    }


    public final SeatBid.Builder seatBid() {
        return OpenRtbUtils.seatBid(openRtb());
    }


    public final BidResponse addBid(BidOrBuilder bid) {
        SeatBid.Builder seatBid = seatBid();
        if (bid instanceof Bid) {
            seatBid.addBid((Bid) bid);
        } else {
            seatBid.addBid((Bid.Builder) bid);
        }
        return self();
    }


    public final BidResponse addBid(String seat, BidOrBuilder bid) {
        SeatBid.Builder seatBid = seatBid(seat);
        if (bid instanceof Bid) {
            seatBid.addBid((Bid) bid);
        } else {
            seatBid.addBid((Bid.Builder) bid);
        }
        return self();
    }


    public final Iterable<Bid.Builder> bids() {
        return OpenRtbUtils.bids(openRtb());
    }


    public final List<Bid.Builder> bids(@Nullable String seat) {
        return OpenRtbUtils.bids(openRtb(), seat);
    }

    public final
    @Nullable
    Bid.Builder bidWithId(String id) {
        return OpenRtbUtils.bidWithId(openRtb(), id);
    }


    public final
    @Nullable
    Bid.Builder bidWithId(@Nullable String seat, String id) {
        return OpenRtbUtils.bidWithId(openRtb(), seat, id);
    }

    public final
    @Nullable
    Bid.Builder bidWithAdid(String adid) {
        checkNotNull(adid);

        for (SeatBidOrBuilder seatbid : openRtb().getSeatbidOrBuilderList()) {
            for (BidOrBuilder bid : seatbid.getBidOrBuilderList()) {
                if (adid.equals(bid.getAdid())) {
                    return ProtoUtils.builder(bid);
                }
            }
        }
        return null;
    }


    public final
    @Nullable
    Bid.Builder bidWithAdid(@Nullable String seat, String adid) {
        checkNotNull(adid);

        for (SeatBidOrBuilder seatbid : openRtb().getSeatbidOrBuilderList()) {
            if (seatbid.hasSeat() ? seatbid.getSeat().equals(seat) : seat == null) {
                for (BidOrBuilder bid : seatbid.getBidOrBuilderList()) {
                    if (adid.equals(bid.getAdid())) {
                        return ProtoUtils.builder(bid);
                    }
                }
                return null;
            }
        }
        return null;
    }

    public final Iterable<Bid.Builder> bidsWith(Predicate<Bid.Builder> filter) {
        return OpenRtbUtils.bidsWith(openRtb(), "", filter);
    }


    public final Iterable<Bid.Builder> bidsWith(
            @Nullable String seat, Predicate<Bid.Builder> filter) {
        return OpenRtbUtils.bidsWith(openRtb(), seat, filter);
    }


    public final boolean updateBids(Function<Bid.Builder, Boolean> updater) {
        return OpenRtbUtils.updateBids(openRtb(), updater);
    }


    public final boolean updateBids(@Nullable String seat, Function<Bid.Builder, Boolean> updater) {
        return OpenRtbUtils.updateBids(openRtb(), seat, updater);
    }


    public final boolean filterBids(Predicate<Bid.Builder> filter) {
        return OpenRtbUtils.removeBids(openRtb(), filter);
    }


    public final boolean filterBids(@Nullable String seat, Predicate<Bid.Builder> filter) {
        return OpenRtbUtils.removeBids(openRtb(), seat, filter);
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("response", response == null ? null : TextFormat.shortDebugString(response));
    }

    /**
     * Builder for {@link BidResponse}.
     */
    public static class Builder extends UserResponse.Builder<Builder> {
        protected Builder() {
        }

        @Override
        public BidResponse build() {
            return new BidResponse(
                    MoreObjects.firstNonNull(getExchange(), defaultExchange()),
                    getHttpResponse());
        }
    }


    public static enum ResponseMode {
        NONE,

        BANNER,

        VIDEO,

        NATIVE,
    }


    public Map<String, String> getFilterErrorMap() {
        return filterErrorMap;
    }

    public void addFilterError(String adCode, FilterErrors errorCode) {
        filterErrorMap.put(adCode, errorCode.val);
    }

    public AdAndPricePair getAdAndPricePair() {
        return adAndPricePair;
    }

    public void setAdAndPricePair(AdAndPricePair adAndPricePair) {
        this.adAndPricePair = adAndPricePair;
    }

    public BaseTa getBaseTa() {
        return baseTa;
    }

    public void setBaseTa(BaseTa baseTa) {
        this.baseTa = baseTa;
    }
}
