package com.mex.bidder.engine.filter.impl;

import com.google.common.base.Stopwatch;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.api.bidding.BidResponse;
import com.mex.bidder.engine.constants.FilterErrors;
import com.mex.bidder.engine.filter.SimpleAdFilter;
import com.mex.bidder.protocol.Ad;
import com.mex.bidder.protocol.nativead.Data;
import com.mex.bidder.protocol.nativead.Image;
import com.mex.bidder.protocol.nativead.Native;
import com.mex.bidder.protocol.nativead.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 过滤原生广告
 * user: donghai
 * date: 2017/4/19
 */
public class NativeAdFilter implements SimpleAdFilter {
    private static final Logger logger = LoggerFactory.getLogger(NativeAdFilter.class);

    @Override
    public boolean filter(Ad ad, BidRequest bidRequest, BidResponse bidResponse) {
        if (!(ad instanceof Native)) {
            // 不是原生广告广告类型，直接跳过
            return false;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        Native nativeAd = (Native) ad;

        OpenRtb.BidRequest openRtbRequest = bidRequest.openRtb();
        OpenRtb.BidRequest.Imp imp = openRtbRequest.getImp(0);
        OpenRtb.NativeRequest nativeRequest = imp.getNative().getRequestNative();

        List<OpenRtb.NativeRequest.Asset> assetsList = nativeRequest.getAssetsList();

        boolean result = !(isTitleMatch(nativeAd, bidResponse, assetsList)
                && isMainImageMatch(nativeAd, bidResponse, assetsList)
                && isLogoImageMatch(nativeAd, bidResponse, assetsList)
                && isIconImageMatch(nativeAd, bidResponse, assetsList)
                && isDescMatch(nativeAd, bidResponse, assetsList));
        stopwatch.stop();
        logger.info("native filter time=" + stopwatch.toString());

        return result;
    }

    static boolean isMainImageMatch(Native nativeAd, BidResponse bidResponse, List<OpenRtb.NativeRequest.Asset> assetsList) {
        List<OpenRtb.NativeRequest.Asset.Image> reqImages = getReqMainImage(assetsList);
        List<Image> adImages = nativeAd.getMainImage();
        boolean result;
        if (reqImages.size() > 0) {
            result = isImageMatch(reqImages, adImages);
        } else {
            result = adImages.isEmpty();
        }
        if (!result) {
            bidResponse.addFilterError(nativeAd.getCode(), FilterErrors.FILTER_FAIL_NATIVE_IMAGEMAIN);
        }
        return result;
    }

    static boolean isLogoImageMatch(Native nativeAd, BidResponse bidResponse, List<OpenRtb.NativeRequest.Asset> assetsList) {
        List<OpenRtb.NativeRequest.Asset.Image> reqLogoImage = getReqLogoImage(assetsList);
        List<Image> logoImageList = nativeAd.getLogoImage();
        boolean result;
        if (reqLogoImage.size() > 0) {
            result = isImageMatch(reqLogoImage, logoImageList);
        } else {
            result = logoImageList.isEmpty();
        }
        if (!result) {
            bidResponse.addFilterError(nativeAd.getCode(), FilterErrors.FILTER_FAIL_NATIVE_IMAGELOGO);
        }
        return result;
    }

    static boolean isIconImageMatch(Native nativeAd, BidResponse bidResponse, List<OpenRtb.NativeRequest.Asset> assetsList) {
        List<OpenRtb.NativeRequest.Asset.Image> reqIconImage = getReqIconImage(assetsList);
        List<Image> iconImageList = nativeAd.getIconImage();
        boolean result;
        if (reqIconImage.size() > 0) {
            result = isImageMatch(reqIconImage, iconImageList);
        } else {
            result = iconImageList.isEmpty();
        }
        if (!result) {
            bidResponse.addFilterError(nativeAd.getCode(), FilterErrors.FILTER_FAIL_NATIVE_IMAGEICON);
        }
        return result;
    }


    private boolean isDescMatch(Native nativeAd, BidResponse bidResponse, List<OpenRtb.NativeRequest.Asset> assetsList) {
        // 请求中是否要描述
        Optional<OpenRtb.NativeRequest.Asset.Data> reqData = getData(assetsList);
        if (reqData.isPresent()) {
            Optional<Data> adData = nativeAd.getData();
            if (adData.isPresent()) {
                int reqDescLen = reqData.get().getLen();
                int adDescLen = adData.get().getValue().length();
                // 请求描述的长度大于广告描述的长度，正常投放
                if (reqDescLen >= adDescLen) {
                    return true;
                } else {
                    bidResponse.addFilterError(nativeAd.getCode(), FilterErrors.FILTER_FAIL_NATIVE_DATA);
                    return false;
                }
            } else {
                //请求中要求标题，但广告中不存在标题
                bidResponse.addFilterError(nativeAd.getCode(), FilterErrors.FILTER_FAIL_NATIVE_DATA);
                return false;
            }
        } else {
            // 请求中无desc,但广告中包括desc,不能投放
            Optional<Data> data = nativeAd.getData();
            data.ifPresent(title -> bidResponse.addFilterError(nativeAd.getCode(), FilterErrors.FILTER_FAIL_NATIVE_DATA));
            return !data.isPresent();
        }
    }

    private boolean isTitleMatch(Native nativeAd, BidResponse bidResponse, List<OpenRtb.NativeRequest.Asset> assetsList) {
        //请求中要求标题与广告标题比较
        Optional<OpenRtb.NativeRequest.Asset.Title> reqTitle = getTitle(assetsList);
        if (reqTitle.isPresent()) {
            Optional<Title> adTitle = nativeAd.getTitle();
            if (adTitle.isPresent()) {
                int reqTitleLen = reqTitle.get().getLen();
                int adTitleLen = adTitle.get().getText().length();
                logger.info("title reqTitle="+reqTitleLen+", adTitleLen="+adTitle.get().getText()+", adLen="+adTitleLen);
                // 请求标题的长度大于广告标题的长度，正常投放
                if (reqTitleLen >= adTitleLen) {
                    return true;
                } else {
                    bidResponse.addFilterError(nativeAd.getCode(), FilterErrors.FILTER_FAIL_NATIVE_TITLE);
                    return false;
                }
            } else {
                //请求中要求标题，但广告中不存在标题
                bidResponse.addFilterError(nativeAd.getCode(), FilterErrors.FILTER_FAIL_NATIVE_TITLE);
                return false;
            }
        } else {
            // 请求中无title,但广告中包括title,不能投放
            Optional<Title> adTitle = nativeAd.getTitle();
            if (adTitle.isPresent()){
                adTitle.ifPresent(title -> bidResponse.addFilterError(nativeAd.getCode(), FilterErrors.FILTER_FAIL_NATIVE_TITLE));
                return false;
            } else {
                return true;
            }
        }
    }

    static boolean isImageMatch(List<OpenRtb.NativeRequest.Asset.Image> reqImages, List<Image> adImages) {
        if (adImages.size() != reqImages.size()) {
            return false;
        } else {
            for (Image adImage : adImages) {
                for (OpenRtb.NativeRequest.Asset.Image reqImage : reqImages) {
                    if (adImage.getH() != reqImage.getH()
                            || adImage.getW() != reqImage.getW()) {
                        logger.info("adImage.getH()="+adImage.getH()+", reqImage.getH()="+reqImage.getH()
                        +", adImage.getW()="+adImage.getW()+", reqImage.getW()="+reqImage.getW());
                        return false;
                    }
                }
            }
            // 尺寸都正常
            return true;
        }
    }

    static Optional<OpenRtb.NativeRequest.Asset.Title> getTitle(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Optional.empty();
        } else {
            return assetsList.stream().filter(OpenRtb.NativeRequest.Asset::hasTitle)
                    .map(OpenRtb.NativeRequest.Asset::getTitle).findFirst();
        }
    }

    static Optional<OpenRtb.NativeRequest.Asset.Data> getData(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Optional.empty();
        } else {
            return assetsList.stream().filter(OpenRtb.NativeRequest.Asset::hasData)
                    .map(OpenRtb.NativeRequest.Asset::getData).findFirst();
        }
    }

    static List<OpenRtb.NativeRequest.Asset.Image> getReqMainImage(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Collections.emptyList();
        } else {
            return assetsList.stream().filter(asset -> asset.hasImg()
                    && asset.getImg().getType() == OpenRtb.ImageAssetType.MAIN)
                    .map(OpenRtb.NativeRequest.Asset::getImg).collect(Collectors.toList());
        }
    }

    static List<OpenRtb.NativeRequest.Asset.Image> getReqIconImage(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Collections.emptyList();
        } else {
            return assetsList.stream().filter(asset -> asset.hasImg()
                    && asset.getImg().getType() == OpenRtb.ImageAssetType.ICON)
                    .map(OpenRtb.NativeRequest.Asset::getImg).collect(Collectors.toList());
        }
    }

    static List<OpenRtb.NativeRequest.Asset.Image> getReqLogoImage(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Collections.emptyList();
        } else {
            return assetsList.stream().filter(asset -> asset.hasImg()
                    && asset.getImg().getType() == OpenRtb.ImageAssetType.LOGO)
                    .map(OpenRtb.NativeRequest.Asset::getImg).collect(Collectors.toList());
        }
    }
}
