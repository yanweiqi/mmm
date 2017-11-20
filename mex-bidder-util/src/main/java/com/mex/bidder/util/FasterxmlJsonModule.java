package com.mex.bidder.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.inject.AbstractModule;

public class FasterxmlJsonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JsonFactory.class).toInstance(new JsonFactory());
    }
}
