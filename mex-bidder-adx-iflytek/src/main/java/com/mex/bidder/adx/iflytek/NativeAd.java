package com.mex.bidder.adx.iflytek;

import java.util.List;

/**
 * 原生
 * user: donghai
 * date: 2017/4/11
 */
public class NativeAd {

    //   instl = 7  一图一文 图片通过img传
    //   instl = 8  一图两文 图片通过img传
    //   instl = 12 一图    图片通过img_urls传
    //   instl = 13 三图一文 图片通过img_urls传
    private int imageCount;
    private String title;
    private String desc;
    private String landing;
    private List<String> img_urls;
    private List<String> imptrackers;
    private List<String> clicktrackers;
    private String deeplink;

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
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


    public String getLanding() {
        return landing;
    }

    public void setLanding(String landing) {
        this.landing = landing;
    }

    public List<String> getImg_urls() {
        return img_urls;
    }

    public void setImg_urls(List<String> img_urls) {
        this.img_urls = img_urls;
    }

    public List<String> getImptrackers() {
        return imptrackers;
    }

    public void setImptrackers(List<String> imptrackers) {
        this.imptrackers = imptrackers;
    }

    public List<String> getClicktrackers() {
        return clicktrackers;
    }

    public void setClicktrackers(List<String> clicktrackers) {
        this.clicktrackers = clicktrackers;
    }
}
