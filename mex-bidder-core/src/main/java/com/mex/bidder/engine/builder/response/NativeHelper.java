package com.mex.bidder.engine.builder.response;

import com.google.common.collect.Lists;
import com.google.openrtb.OpenRtb;
import com.mex.bidder.api.bidding.BidRequest;
import com.mex.bidder.protocol.nativead.Data;
import com.mex.bidder.protocol.nativead.Image;
import com.mex.bidder.protocol.nativead.Native;
import com.mex.bidder.protocol.nativead.Title;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * user: donghai
 * date: 2017/4/27
 */
public class NativeHelper {
    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";


    public static List<OpenRtb.NativeResponse.Asset> buildNativePbAssets(BidRequest request, Native nativeAd,
                                                                         boolean needSecure) {

        List<OpenRtb.NativeResponse.Asset> pbAssets = Lists.newArrayList();
        List<OpenRtb.NativeRequest.Asset> reqAsetsList = request.openRtb().getImp(0)
                .getNative().getRequestNative().getAssetsList();

        // 标题
        Optional<OpenRtb.NativeRequest.Asset> reqTitle = NativeHelper.getTitle(reqAsetsList);
        if (reqTitle.isPresent()) {
            Optional<Title> adTitle = nativeAd.getTitle();
            adTitle.ifPresent(title -> pbAssets.add(OpenRtb.NativeResponse.Asset.newBuilder()
                    .setId(reqTitle.get().getId())
                    .setTitle(OpenRtb.NativeResponse.Asset.Title.newBuilder()
                            .setText(title.getText()))
                    .build()));
        }

        List<OpenRtb.NativeRequest.Asset> reqImages = NativeHelper.getReqMainImage(reqAsetsList);
        if (reqImages.size() > 0) {
            List<Image> mainImageList = nativeAd.getMainImage();
            int i = 0;
            for (Image image : mainImageList) {
                pbAssets.add(OpenRtb.NativeResponse.Asset.newBuilder()
                        .setId(reqImages.get(i++).getId())
                        .setImg(OpenRtb.NativeResponse.Asset.Image.newBuilder(
                                OpenRtb.NativeResponse.Asset.Image.newBuilder()
                                        .setH(image.getH()).setW(image.getW())
                                        .setUrl((needSecure ? HTTPS : HTTP) + formatUrl(image.getUrl()))
                                        .build())).build());
            }
        }

        List<OpenRtb.NativeRequest.Asset> reqIconImage = NativeHelper.getReqIconImage(reqAsetsList);
        if (reqIconImage.size() > 0) {
            List<Image> mainImageList = nativeAd.getIconImage();
            int i = 0;
            for (Image image : mainImageList) {
                pbAssets.add(OpenRtb.NativeResponse.Asset.newBuilder()
                        .setId(reqIconImage.get(i++).getId())
                        .setImg(OpenRtb.NativeResponse.Asset.Image.newBuilder(
                                OpenRtb.NativeResponse.Asset.Image.newBuilder()
                                        .setH(image.getH()).setW(image.getW())
                                        .setUrl((needSecure ? HTTPS : HTTP) + formatUrl(image.getUrl()))
                                        .build())).build()
                );
            }
        }

        Optional<OpenRtb.NativeRequest.Asset> reqData = NativeHelper.getData(reqAsetsList);
        if (reqData.isPresent()) {
            Optional<Data> adData = nativeAd.getData();
            pbAssets.add(OpenRtb.NativeResponse.Asset.newBuilder()
                    .setId(reqData.get().getId())
                    .setData(OpenRtb.NativeResponse.Asset.Data.newBuilder(
                            OpenRtb.NativeResponse.Asset.Data.newBuilder()
                                    .setLabel(adData.get().getLabel())
                                    .setValue(adData.get().getValue())
                                    .build()
                    )).build()
            );
        }

        return pbAssets;
    }


    public static Optional<OpenRtb.NativeRequest.Asset> getTitle(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Optional.empty();
        } else {
            return assetsList.stream().filter(OpenRtb.NativeRequest.Asset::hasTitle).findFirst();
        }
    }

    public static Optional<OpenRtb.NativeRequest.Asset> getData(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Optional.empty();
        } else {
            return assetsList.stream().filter(OpenRtb.NativeRequest.Asset::hasData).findFirst();
        }
    }

    public static List<OpenRtb.NativeRequest.Asset> getReqMainImage(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Collections.emptyList();
        } else {
            return assetsList.stream().filter(asset -> asset.hasImg()
                    && asset.getImg().getType() == OpenRtb.ImageAssetType.MAIN)
                    .collect(Collectors.toList());
        }
    }

    public static List<OpenRtb.NativeRequest.Asset> getReqIconImage(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Collections.emptyList();
        } else {
            return assetsList.stream().filter(asset -> asset.hasImg()
                    && asset.getImg().getType() == OpenRtb.ImageAssetType.ICON)
                    .collect(Collectors.toList());
        }
    }

    public static List<OpenRtb.NativeRequest.Asset> getReqLogoImage(List<OpenRtb.NativeRequest.Asset> assetsList) {
        if (assetsList == null) {
            return Collections.emptyList();
        } else {
            return assetsList.stream().filter(asset -> asset.hasImg()
                    && asset.getImg().getType() == OpenRtb.ImageAssetType.LOGO)
                    .collect(Collectors.toList());
        }
    }

    public  static String formatUrl(String url) {
        if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
            return url.replace(HTTP, "").replace(HTTPS, "");
        }
        return url;
    }
}