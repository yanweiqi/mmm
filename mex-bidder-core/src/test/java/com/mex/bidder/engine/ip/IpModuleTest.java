package com.mex.bidder.engine.ip;

import com.google.common.collect.ImmutableMap;
import com.mex.bidder.engine.model.IpBean;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.List;

/**
 * User: donghai
 * Date: 2016/11/23
 */
public class IpModuleTest {
    @Test
    public void configure() throws Exception {

    }

    @Test
    public void loadIpFile() throws Exception {

        JsonObject cnf = new JsonObject();
        cnf.put("ip-lib", "d:/ip-lib.csv");
        ImmutableMap<String, List<IpBean>> ipMap = new IpModule(cnf).loadIpFile();
        System.out.println(ipMap.size());

        IpService ipService = new IpService(ipMap);

        IpBean lookup = ipService.lookup("119.57.32.71");
        System.out.println(lookup.getCityId());
        // 1156110000 北京
    }

}