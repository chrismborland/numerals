package com.borland.numerals.monitoring;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

/**
 * Servlet for checking the status of health checks.
 * 
 * @author chrismborland
 */
public class HealthCheckContextListener extends HealthCheckServlet.ContextListener {

    // health check registry.
    public static HealthCheckRegistry healthMetrics = new HealthCheckRegistry();

    static {
        // numeral servlet health check.
        healthMetrics.register("numerals-servlet", new NumeralsHealthCheck());
    }

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return healthMetrics;
    }

}
