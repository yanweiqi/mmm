/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mex.bidder.engine.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 系统指标监测模块
 */
public class MetricsModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(MetricsModule.class);

    @Override
    protected void configure() {
        MetricRegistry metricRegistry = new MetricRegistry();

        metricRegistry.registerAll(new GarbageCollectorMetricSet());
        metricRegistry.registerAll(new ThreadStatesGaugeSet());
        metricRegistry.registerAll(new MemoryUsageGaugeSet());
        bind(MetricRegistry.class).toInstance(metricRegistry);
    }

}
