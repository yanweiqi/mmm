package com.mex.bidder.api.platform;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@BindingAnnotation
@Target({TYPE})
@Retention(RUNTIME)
public @interface CompatibleExchanges {

    /**
     * The list of compatible exchanges.
     */
    String[] value();
}
