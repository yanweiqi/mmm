package com.mex.bidder.engine.ip;

import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.mex.bidder.engine.model.IpBean;
import com.mex.bidder.engine.util.IpUtil;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

/**
 * User: donghai
 * Date: 2016/11/23
 */
public class IpModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(IpModule.class);

    private JsonObject cnf;

    public IpModule(JsonObject cnf) {
        this.cnf = cnf;
    }

    @Override
    protected void configure() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        logger.info("start load ip file ");
        ImmutableMap<String, List<IpBean>> stringListImmutableMap = loadIpFile();
        bind(new TypeLiteral<ImmutableMap<String, List<IpBean>>>() {
        }).toInstance(stringListImmutableMap);

        bind(IpService.class).toInstance(new IpService(stringListImmutableMap));

        stopwatch.stop();
        logger.info("end load ip file, time=" + stopwatch);
    }

    protected ImmutableMap<String, List<IpBean>> loadIpFile() {
        String iplibPath = cnf.getString("ip-lib");

        Objects.requireNonNull(iplibPath, "cnf ip-lib can't be empty");

        logger.info("iplibPath = {}", iplibPath);
        // URL resource = Resources.getResource(iplibPath);

        File ipFile = new File(iplibPath);

        Map<String, List<IpBean>> iplibMap = Maps.newHashMap();

        BufferedReader ipReader = null;
        try {
            ipReader = Files.newReader(ipFile, Charset.forName("utf-8"));
            String line;

            while ((line = ipReader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                String[] geoArray = line.split(",");

                String startIpStr = geoArray[0];
                String endIpStr = geoArray[1];
                int cityId = Integer.parseInt(geoArray[2]);

                IpBean ipBean = new IpBean();
                ipBean.setBegin(IpUtil.toLong(startIpStr));
                ipBean.setEnd(IpUtil.toLong(endIpStr));
                ipBean.setCityId(cityId);

                String[] octets = startIpStr.split("\\.");
                List<IpBean> ipBeanList = iplibMap.computeIfAbsent(octets[0], k -> new ArrayList<>());
                ipBeanList.add(ipBean);
            }

            if (iplibMap.isEmpty()) {
                throw new RuntimeException("load ip list is empty.");
            }


            // 排序
            iplibMap.forEach((k, ipList) -> Collections.sort(ipList, IpUtil.IpComp));

            // 数据结构构建完毕
            return ImmutableMap.copyOf(iplibMap);

        } catch (Exception e) {
            // 服务初始化一旦失败，抛出运行时异常
            throw new RuntimeException("Load ip.txt occurs exception", e);
        } finally {
            Closeables.closeQuietly(ipReader);
        }
    }
}
