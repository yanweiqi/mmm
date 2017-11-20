package com.mex.bidder.engine.filter;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mex.bidder.engine.filter.impl.*;

/**
 * 广告过滤定向
 * <p>
 * User: donghai
 * Date: 2016/11/20
 */
public class FilterModule extends AbstractModule {

    @Override
    protected void configure() {


        Multibinder<SimpleAdFilter> filterBinder = Multibinder.newSetBinder(binder(), SimpleAdFilter.class);
        filterBinder.addBinding().to(OsFitler.class);
        filterBinder.addBinding().to(CarrierFilter.class);
        filterBinder.addBinding().to(NetworkTypeFilter.class);
        filterBinder.addBinding().to(DeviceTypeFilter.class);
        filterBinder.addBinding().to(GeoCityFilter.class);
        filterBinder.addBinding().to(BidFloorFilter.class);
        filterBinder.addBinding().to(AdxMediaFilter.class);
        filterBinder.addBinding().to(HttpsFilter.class);
        filterBinder.addBinding().to(DeviceIdFilter.class);
        filterBinder.addBinding().to(GeoPositionFilter.class);
        filterBinder.addBinding().to(AppFilter.class);
        filterBinder.addBinding().to(NativeAdFilter.class);
//        filterBinder.addBinding().to(BudgetFacingFilter.class);
        filterBinder.addBinding().to(DeeplinkFilter.class);


        // 频次控制依赖redis DMP 依赖Http
        Multibinder<AsyncAdListFilter> asyncfilterBinder = Multibinder.newSetBinder(binder(), AsyncAdListFilter.class);
        asyncfilterBinder.addBinding().to(AsyncFrequencyCappingFilter.class);
        asyncfilterBinder.addBinding().to(DmpTaFilter.class);

        Multibinder<AdListFilter> adListFilterbinder = Multibinder.newSetBinder(binder(), AdListFilter.class);
        adListFilterbinder.addBinding().to(SmoothDeliveryFilter.class);
    }
}
