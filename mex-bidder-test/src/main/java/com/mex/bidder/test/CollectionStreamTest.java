package com.mex.bidder.test;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * xuchuahao
 * on 2017/4/27.
 */
public class CollectionStreamTest {

    public static void main(String[] args) {
        CollectionStreamTest cs = new CollectionStreamTest();
        cs.streamTest();
    }

    public void streamTest(){
        ArrayList<String> strings = Lists.newArrayList("1", "2", "1", null, "3", null);
        Stream<String> distinct = strings.stream().distinct();
        List<String> collect = distinct.collect(Collectors.toList());
        System.out.println(collect);

    }
}
