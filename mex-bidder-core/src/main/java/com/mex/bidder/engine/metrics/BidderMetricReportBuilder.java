
package com.mex.bidder.engine.metrics;

import com.codahale.metrics.*;
import com.google.common.collect.ImmutableList;
import com.mex.bidder.util.Clock;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Builds a list of {@link BidderMetricReportRow} for each metric in a {@link MetricRegistry}.
 */
public class BidderMetricReportBuilder {

    private final Clock clock;
    private final String bidderName;
    private final
    @Nullable
    String region;

    public BidderMetricReportBuilder(Clock clock, String bidderName, @Nullable String region) {
        this.clock = clock;
        this.bidderName = bidderName;
        this.region = region;
    }

    /**
     * @return A list of {@link BidderMetricReportRow} for each metric in a {@link MetricRegistry}.
     */
    public ImmutableList<BidderMetricReportRow> build(MetricRegistry metricRegistry) {
        ImmutableList.Builder<BidderMetricReportRow> rows = ImmutableList.builder();
        for (Map.Entry<String, Metric> metricEntry : metricRegistry.getMetrics().entrySet()) {
            BidderMetricReportRow row = new BidderMetricReportRow();
            row.setTimestamp(clock.now().getMillis());
            row.setBidderName(bidderName);
            row.setRegion(region);
            row.setMetricName(metricEntry.getKey());

            Metric metric = metricEntry.getValue();
            if (metric instanceof Meter) {
                processMeter(row, (Meter) metric);
            } else if (metric instanceof Histogram) {
                processHistogram(row, (Histogram) metric);
            } else if (metric instanceof Timer) {
                processTimer(row, (Timer) metric);
            } else if (metric instanceof Counter) {
                processCounter(row, (Counter) metric);
            } else {
                continue;
            }
            rows.add(row);
        }

        return rows.build();
    }

    private BidderMetricReportRow processMeter(
            BidderMetricReportRow row, Meter meter) {
        row.setMetricType(format(MetricType.METER));
        row.setMeterCount(meter.getCount());
        row.setMeterMean(meter.getMeanRate());
        row.setMeter1Minute(meter.getOneMinuteRate());
        row.setMeter5Minutes(meter.getFiveMinuteRate());
        row.setMeter15Minutes(meter.getFifteenMinuteRate());
        return row;
    }

    private BidderMetricReportRow processHistogram(
            BidderMetricReportRow row, Histogram histogram) {
        Snapshot snapshot = histogram.getSnapshot();
        row.setMetricType(format(MetricType.HISTOGRAM));
        row.setHistogramCount(histogram.getCount());
        row.setHistogramMin(snapshot.getMin());
        row.setHistogramMax(snapshot.getMax());
        row.setHistogramStdDev(snapshot.getStdDev());
        row.setHistogramMedian(snapshot.getMedian());
        row.setHistogram75thPercentile(snapshot.get75thPercentile());
        row.setHistogram95thPercentile(snapshot.get95thPercentile());
        row.setHistogram98thPercentile(snapshot.get98thPercentile());
        row.setHistogram99thPercentile(snapshot.get99thPercentile());
        row.setHistogram999thPercentile(snapshot.get999thPercentile());
        return row;
    }

    private BidderMetricReportRow processTimer(
            BidderMetricReportRow row, Timer timer) {
        Snapshot snapshot = timer.getSnapshot();
        row.setMetricType(format(MetricType.TIMER));
        row.setTimerDurationMin(snapshot.getMin());
        row.setTimerDurationMax(snapshot.getMax());
        row.setTimerDurationMean(snapshot.getMean());
        row.setTimerDurationStdDev(snapshot.getStdDev());
        row.setTimerDurationMedian(snapshot.getMedian());
        row.setTimerDuration75thPercentile(snapshot.get75thPercentile());
        row.setTimerDuration95thPercentile(snapshot.get95thPercentile());
        row.setTimerDuration98thPercentile(snapshot.get98thPercentile());
        row.setTimerDuration99thPercentile(snapshot.get99thPercentile());
        row.setTimerDuration999thPercentile(snapshot.get999thPercentile());
        row.setTimerRateCount(timer.getCount());
        row.setTimerRateMean(timer.getMeanRate());
        row.setTimerRate1Minute(timer.getOneMinuteRate());
        row.setTimerRate5Minutes(timer.getFiveMinuteRate());
        row.setTimerRate15Minutes(timer.getFifteenMinuteRate());
        return row;
    }

    private BidderMetricReportRow processCounter(
            BidderMetricReportRow row, Counter counter) {
        row.setMetricType(format(MetricType.COUNTER));
        row.setCounterCount(counter.getCount());
        return row;
    }

    private String format(Enum<?> in) {
        return in.toString().toLowerCase();
    }
}
