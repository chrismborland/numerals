package com.borland.numerals;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import com.borland.numerals.metrics.MetricsInstrumentedFilterContextListener;
import com.borland.numerals.monitoring.HealthCheckContextListener;
import com.borland.numerals.servlet.NumeralServlet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.HealthCheckServlet;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class.  Creates the Jetty server and registers numerals servlet.
 * 
 * @author chrismborland
 */
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    // create metrics registry.
    public static final MetricRegistry metrics = new MetricRegistry();

    // metric prefix.
    public static final String METRIX_PREFIX = "numerals";

    // port to start jetty server on.
    public static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            // configure basic logging (log4j)
            BasicConfigurator.configure();

            // create the jetty server.
            Server server = new Server();

            // create the server connector.
            ServerConnector connector = new ServerConnector(server);
            
            // set the connector port
            connector.setPort(PORT);
            
            // set the connectors.
            server.setConnectors(new Connector[] { connector });

            // create the servlet context handler.
            ServletContextHandler servletContextHandler = new ServletContextHandler();

            // register our roman numeral servlet.
            servletContextHandler.addServlet(NumeralServlet.class, NumeralServlet.SERVLET_PATH);

            // register health check servlet.
            servletContextHandler.addServlet(HealthCheckServlet.class, "/healthcheck");

            // add our health check context listener to hook in our health checks.
            servletContextHandler.addEventListener(new HealthCheckContextListener());

            // add our metrics intrumented context listener to record request/response metrics.
            FilterHolder metricsFilterHolder = servletContextHandler.addFilter(InstrumentedFilter.class, NumeralServlet.SERVLET_PATH, EnumSet.of(DispatcherType.REQUEST));
            metricsFilterHolder.setInitParameter("name-prefix", METRIX_PREFIX);
            servletContextHandler.addEventListener(new MetricsInstrumentedFilterContextListener());

            // set the servlet context hanler on the server.
            server.setHandler(servletContextHandler);

            // start the jetty server.
            server.start();

            // send metrics to JMX.
            final JmxReporter reporter = JmxReporter.forRegistry(metrics).build();
            reporter.start();

        } catch (Exception e) {
            // not much we can do here besides log the error.
            LOG.error("Exception starting Jetty.", e);
        }

    }

}
