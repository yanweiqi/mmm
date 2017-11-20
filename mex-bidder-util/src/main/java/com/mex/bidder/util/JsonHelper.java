package com.mex.bidder.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

/**
 * User: donghai
 * Date: 2016/11/22
 */
public class JsonHelper {


    public static String readFile(String fileName) {
        try (InputStream is = JsonHelper.class.getResourceAsStream( "/"+fileName);
             Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
