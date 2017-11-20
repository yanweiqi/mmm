package com.mex.bidder.engine.constants;

import com.google.api.client.util.Lists;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 广告类型
 * "0" 图片 "2" 原生广告 "4" 富媒体  "6" 视频
 * User: donghai
 * Date: 2016/12/4
 */
public enum MaterialType {
    BANNER("0"),
    NATIVE("2"),
    RICH_MEDIA("4"),
    VIDEO("6"),;

    public final String val;

    MaterialType(String val) {
        this.val = val;
    }

    static final Map<String, MaterialType> lookupTable;

    static {
        lookupTable = Arrays.stream(MaterialType.values()).collect(Collectors.toMap(MaterialType::getVal, materialType -> materialType));
    }

    public String getVal() {
        return this.val;
    }

    public static MaterialType lookup(String val) {
        return lookupTable.get(val);
    }

    public static List<String> toMax(List<MexOpenRtbExt.AdMaterialType> openRtb) {
        List<String> result = Lists.newArrayList();
        openRtb.forEach(adMaterialType -> {
            if (adMaterialType == MexOpenRtbExt.AdMaterialType.IMG_Material) {
                result.add(BANNER.getVal());
            } else if (adMaterialType == MexOpenRtbExt.AdMaterialType.NATIVE_Material) {
                result.add(NATIVE.getVal());
            } else if (adMaterialType == MexOpenRtbExt.AdMaterialType.VIDEO_Material) {
                result.add(VIDEO.getVal());
            } else if (adMaterialType == MexOpenRtbExt.AdMaterialType.HTML5_Material) {
                result.add(RICH_MEDIA.getVal());
            }
        });
        return result;
    }
}
