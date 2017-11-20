package com.mex.bidder.adx.iflytek;

import java.util.List;

/**
 * Created by lanwj on 2016/11/28.
 */
public class Banner_ad {

    private Integer  mtype;

    private String title;

    private String desc;

    private String image_url;
    private String html;
    private String landing;
    private int w;
    private int h;
    private List<String> impress;

    private List<String> click;

    private String package_name;

    private String deeplink;

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public Integer getMtype() {
        return mtype;
    }

    public void setMtype(Integer mtype) {
        this.mtype = mtype;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getLanding() {
        return landing;
    }

    public void setLanding(String landing) {
        this.landing = landing;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public List<String> getImpress() {
        return impress;
    }

    public void setImpress(List<String> impress) {
        this.impress = impress;
    }

    public List<String> getClick() {
        return click;
    }

    public void setClick(List<String> click) {
        this.click = click;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }
}
