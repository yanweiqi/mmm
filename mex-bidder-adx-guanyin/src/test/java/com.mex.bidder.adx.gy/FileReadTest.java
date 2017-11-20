package com.mex.bidder.adx.gy;

import com.mex.bidder.util.JsonHelper;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * xuchuanao
 * on 2017/1/13.
 */
public class FileReadTest {

    public static String readFile(String fileName) {
        try (InputStream is = JsonHelper.class.getResourceAsStream( "/"+fileName);
             Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        String s = readFile("aa.json");
        System.out.println(s);

    }

    /*static {
        String data = readFile("gy_req_data.json");
        JsonObject json = new JsonObject(data);
        GyOpenRtb.BidRequest.Builder builder = GyOpenRtb.BidRequest.newBuilder();
        builder.setId(json.getString("id"));


    }*/
}
