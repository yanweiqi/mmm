package com.mex.bidder.engine.ip;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.mex.bidder.engine.model.IpBean;
import com.mex.bidder.engine.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ip地址查询服务
 * <p>
 * User: donghai
 * Date: 2016/11/20
 */
public class IpService {
    private static final Logger logger = LoggerFactory.getLogger(IpService.class);

    // ip位置库信息
    private final ImmutableMap<String, List<IpBean>> ipMapList;

    public IpService(ImmutableMap<String, List<IpBean>> ipMapList) {
        this.ipMapList = ipMapList;
    }

    public IpBean lookup(String ip) {
        if (Strings.isNullOrEmpty(ip)) {
            return IpBean.NULL;
        }

        IpBean ipBean = IpUtil.locate(ip, ipMapList);
        // 查不到地域时记录日志
        if (ipBean == IpBean.NULL) {
            logger.warn("locate ipBean failed ip=" + ip);
        }

        return ipBean;
    }

}
