package com.mex.bidder.api.platform;

import com.google.inject.Binder;
import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public final class NoExchange extends Exchange {
    public static final NoExchange INSTANCE = new NoExchange();

    private NoExchange() {
        super(No.NAME);
    }

    @Override
    public Object newNativeResponse() {
        throw new UnsupportedOperationException();
    }

    /**
     * NoExchange-specific object.
     */
    @BindingAnnotation
    @Target({TYPE, FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    public static @interface No {
        String NAME = "no-exchange";
    }

    /**
     * Bindings for {@link NoExchange}.
     */
    public static class Module implements com.google.inject.Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(Exchange.class).toInstance(INSTANCE);
        }
    }
}
