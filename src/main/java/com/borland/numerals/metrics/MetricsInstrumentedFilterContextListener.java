package com.borland.numerals.metrics;

import com.borland.numerals.App;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilterContextListener;

/**
 * Custom instrumented filter context listener class which directs parent class
 * to our metrics registry.
 * 
 * @author chrismborland
 */
public class MetricsInstrumentedFilterContextListener extends InstrumentedFilterContextListener {

    @Override
    protected MetricRegistry getMetricRegistry() {
        return App.metrics;
    }

}
