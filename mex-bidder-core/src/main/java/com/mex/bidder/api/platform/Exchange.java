package com.mex.bidder.api.platform;

import javax.annotation.Nullable;

/**
 * Adx渠道方
 */
public abstract class Exchange {
    /**
     * 标识，必需唯一，与请求的path一致。
     */
    private final String id;

    protected Exchange(String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }


    public abstract Object newNativeResponse();

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj;
    }

    @Override
    public String toString() {
        return id;
    }
}
