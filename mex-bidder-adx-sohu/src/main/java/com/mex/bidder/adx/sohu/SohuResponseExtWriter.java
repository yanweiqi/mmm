package com.mex.bidder.adx.sohu;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.openrtb.json.OpenRtbJsonExtWriter;

import java.io.IOException;

/**
 * xuchuahao
 * on 2017/3/29.
 */
public class SohuResponseExtWriter {

    public static class ImpTracker extends OpenRtbJsonExtWriter<String>{

        public ImpTracker(){
            super("imptrackers",false);
        }

        @Override
        protected void write(String value, JsonGenerator gen) throws IOException {
            gen.writeString(value);
        }
    }

    public static class ClickTracker extends OpenRtbJsonExtWriter<String>{

        public ClickTracker(){
            super("clktrackers",false);
        }

        @Override
        protected void write(String value, JsonGenerator gen) throws IOException {
            gen.writeString(value);
        }
    }

    public static class CURL extends OpenRtbJsonExtWriter<String>{

        public CURL(){
            super("clkurl",false);
        }

        @Override
        protected void write(String value, JsonGenerator gen) throws IOException {
            gen.writeString(value);
        }
    }
}
