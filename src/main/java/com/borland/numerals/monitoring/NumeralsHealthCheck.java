package com.borland.numerals.monitoring;

import javax.servlet.http.HttpServletResponse;

import com.borland.numerals.App;
import com.borland.numerals.servlet.NumeralServlet;
import com.codahale.metrics.health.HealthCheck;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Health check for numerals service. Pings the service to check for status.
 * 
 * @author chrismborland
 */
public class NumeralsHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        CloseableHttpClient httpClient = null;
        try {
            // make a GET to numeral servlet to verify status.
            httpClient = HttpClients.createMinimal();
            // ideally this would be configured elsewhere.
            HttpGet request = new HttpGet("http://localhost:" + Integer.toString(App.PORT) + NumeralServlet.SERVLET_PATH);
            CloseableHttpResponse response = httpClient.execute(request);
            if (response != null && response.getStatusLine() != null) {
                if (HttpServletResponse.SC_OK == response.getStatusLine().getStatusCode()) {
                    // 200 status. healthy.
                    return Result.healthy();
                } else {
                    // non-200 status. unhealthy.
                    return Result.unhealthy("Invalid response code.");
                }
            }
            return Result.unhealthy("Could not establish connection.");
        } catch (Exception e) {
            return Result.unhealthy(e);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

}
