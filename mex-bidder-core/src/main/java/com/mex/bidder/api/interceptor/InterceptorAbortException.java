package com.mex.bidder.api.interceptor;

public class InterceptorAbortException extends RuntimeException {
    public InterceptorAbortException() {
    }

    public InterceptorAbortException(String m) {
        super(m);
    }

    public InterceptorAbortException(String m, Throwable t) {
        super(m, t);
    }

    public InterceptorAbortException(Throwable t) {
        super(t);
    }
}
