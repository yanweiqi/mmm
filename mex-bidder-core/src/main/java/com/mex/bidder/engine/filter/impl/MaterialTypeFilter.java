package com.mex.bidder.engine.filter.impl;

import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.api.openrtb.MexOpenRtbExt;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.constants.MaterialType;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;

import java.util.List;

/**
 * Todo 按素材类型分类
 * User: donghai
 * Date: 2016/12/4
 */
public class MaterialTypeFilter implements SimpleAdFilter {
    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {
        // 正常请求必须有一类对应的素材类型
        List<MexOpenRtbExt.AdMaterialType> extensionList = bidRequest.openRtb().getExtension(MexOpenRtbExt.adMaterialType);
        List<String> toMaxMaterialTypes = MaterialType.toMax(extensionList);
        String material_type = ad.getMaterialType();

        if (toMaxMaterialTypes.contains(material_type)) {
            return false;
        } else {
            bidResponse.addFilterError(ad.getCode(), FilterErrors.FILTER_FAIL_UnPredictReq);
            return true;
        }
    }
}
