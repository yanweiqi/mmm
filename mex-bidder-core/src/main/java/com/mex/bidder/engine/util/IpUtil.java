package com.mex.bidder.engine.util;

import com.google.common.collect.ImmutableMap;
import com.mex.bidder.engine.model.IpBean;

import java.util.Comparator;
import java.util.List;

/**
 * IP相关的工具方法
 * <p>
 * User: donghai
 * Date: 2016/11/17
 */
public class IpUtil {
    /**
     * ip封装bean排序比较器
     * <p/>
     * 说明: 用于排序解析后的IP文件内容[升序]
     */
    public static Comparator<IpBean> IpComp = (p1, p2) -> {
        if (p1.getBegin() < p2.getBegin()) {
            return -1;
        } else if (p1.getBegin() == p2.getBegin()) {
            if (p1.getEnd() < p2.getEnd()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    };

    public static long toLong(String ip) {
        String[] octets = ip.trim().split("\\.");
        return toLong(octets);
    }

    public static long toLong(String[] octets) {
        long result = 0;
        for (String octet : octets) {
            result <<= 8;
            result |= Integer.parseInt(octet) & 0xff;
        }
        return result;
    }

    public static IpBean locate(String ip, ImmutableMap<String, List<IpBean>> ipMap) {
        String[] octets = ip.trim().split("\\.");

        if (ipMap.containsKey(octets[0])) {
            long ipLong = toLong(octets);

            // 通过前缀定位到列表再二分查找
            List<IpBean> ipBeanList = ipMap.get(octets[0]);
            return binarySearch(ipBeanList, ipLong);
        }

        return IpBean.NULL;
    }

    /**
     * 二分查找IP
     *
     * @param ipBeanList
     * @param ipValue
     * @return
     */
    private static IpBean binarySearch(List<IpBean> ipBeanList, long ipValue) {
        int low = 0;
        int high = ipBeanList.size() - 1;

        IpBean foundIpBean = IpBean.NULL;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            IpBean midVal = ipBeanList.get(mid);

            if (ipValue >= midVal.getBegin() && ipValue <= midVal.getEnd()) {
                foundIpBean = midVal;
                break;
            } else if (ipValue < midVal.getBegin()) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return foundIpBean;
    }
}
