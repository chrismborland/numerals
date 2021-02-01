package com.borland.numerals.monitoring;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.borland.numerals.App;
import com.codahale.metrics.health.HealthCheck.Result;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test class for NumeralsHealthCheck.
 * 
 * @author chrimsborland
 */
public class NumeralsHealthCheckTest extends Mockito {

    /**
     * Verify healthy response for health check. This method spins up the embedded
     * Jetty server and calls health check to verify.
     */
    @Test
    public void healthy() {
        // start app.
        App.main(null);
        // verify health check.
        NumeralsHealthCheck healthCheck = new NumeralsHealthCheck();
        Result result = healthCheck.execute();
        assertTrue(result.isHealthy());
    }

    /**
     * Verify unhealthy response for health check.
     */
    @Test
    public void unhealthyException() {
        // verify health check.
        NumeralsHealthCheck healthCheck = new NumeralsHealthCheck();
        Result result = healthCheck.execute();
        assertFalse(result.isHealthy());
    }

}
