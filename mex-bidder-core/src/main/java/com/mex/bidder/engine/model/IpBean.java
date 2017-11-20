package com.mex.bidder.engine.model;

/**
 * User: donghai
 * Date: 2016/11/17
 */
public class IpBean {
    public static final IpBean NULL = new IpBean();

    /**
     * ip段起始值
     */
    private long begin = 0L;

    /**
     * ip段结束值
     */
    private long end = 0L;

    /**
     * 国家id
     */
    private int countryId;

    /**
     * 省份id
     */
    private int provinceId;

    /**
     * 城市id
     */
    private int cityId;

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
