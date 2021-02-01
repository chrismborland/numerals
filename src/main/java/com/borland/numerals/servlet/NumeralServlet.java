package com.borland.numerals.servlet;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.borland.numerals.App;
import com.borland.numerals.service.NumeralPair;
import com.borland.numerals.service.NumeralService;
import com.borland.numerals.service.impl.StandardFormNumeralServiceImpl;
import com.codahale.metrics.Histogram;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for converting numbers & ranges of numbers to numerals.
 * 
 * @author chrismborland
 */
public class NumeralServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(NumeralServlet.class);

    // servlet path.
    public static final String SERVLET_PATH = "/romannumeral";

    // metrics.s
    private final Histogram responseSizes = App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes");

    // available query string parameters.
    private static final String PARAM_QUERY = "query";
    private static final String PARAM_MIN = "min";
    private static final String PARAM_MAX = "max";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // gather the query parameters.
        String query = request.getParameter(PARAM_QUERY);
        String min = request.getParameter(PARAM_MIN);
        String max = request.getParameter(PARAM_MAX);
        // determine how to process.
        if (query != null) {
            handleSingle(request, response, query);
        } else if (min != null && max != null) {
            handleRange(request, response, min, max);
        } else {
            LOG.error("Unable to handle request.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Handles a request for a single number conversion. Returns a numeral pair JSON
     * object, if successful. If parameters are invalid, error JSON will be
     * returned.
     * 
     * @param request  Request.
     * @param response Response.
     * @param query    Query parameter with string of number to convert.
     * @throws IOException
     */
    private void handleSingle(HttpServletRequest request, HttpServletResponse response, String query)
            throws IOException {
        // attempt to convert the query params to integers.
        int number = getNumber(query);
        NumeralService numeralService = new StandardFormNumeralServiceImpl();
        // check for eligibility
        if (!numeralService.isEligible(number)) {
            // log the invalid request.
            LOG.error("Invalid parameter on conversion request. [query = {}]", query);
            // write error response.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // return early. do not continue processing.
            return;
        }
        // perform the actual conversion
        NumeralPair pair = numeralService.convertToNumeral(number);
        // verify we recieved pair.
        if (pair == null) {
            LOG.error("Error converting [{}] to numeral.  Check logs.", query);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // return early.  do not continue processing.
            return;
        }
        // write the converted pair to the response.
        ObjectMapper mapper = new ObjectMapper();
        sendResponse(response, mapper.writeValueAsString(pair));
    }

    /**
     * Handles a request for a range number conversion. Returns a JSON array of
     * numeral pair objects, if successful. If parameters are invalid, error JSON
     * will be returned.
     * 
     * @param request  Request.
     * @param response Response.
     * @param min      String representation of lower end range number.
     * @param max      String representation of higher end range number.
     * @throws IOException
     */
    private void handleRange(HttpServletRequest request, HttpServletResponse response, String min, String max)
            throws IOException {
        // attempt to convert the query params to integers.
        int minNum = getNumber(min);
        int maxNum = getNumber(max);
        NumeralService numeralService = new StandardFormNumeralServiceImpl();
        if (!numeralService.isRangeEligible(minNum, maxNum)) {
            // log the invalid request.
            LOG.error("Invalid parameter on conversion request. [min = {}, max = {}]", min, max);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // perform the actual conversion.
        Set<NumeralPair> pairs = numeralService.convertToNumeral(minNum, maxNum);
        // verify we recieved pairs.
        if (pairs == null || pairs.isEmpty()) {
            LOG.error("Error converting range [min = {}, max = {}] to numerals.  Check logs.", min, max);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        // write the converted pair to the response.
        ObjectMapper mapper = new ObjectMapper();
        sendResponse(response, mapper.writeValueAsString(pairs));
    }

    /**
     * Sends a response (200) with message.
     * 
     * @param response Response.
     * @param message  Message to send (typically JSON string).
     * @throws IOException
     */
    private void sendResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(message);
        // record the response size.
        responseSizes.update(message.length());
    }

    /**
     * Attempt to convert the <code>number</code> to an integer.
     * 
     * @param number String to convert.
     * @return Integer version of <code>number</code>, if possible. If not, -1;
     */
    private int getNumber(final String number) {
        if (number != null && number.length() > 0) {
            try {
                return Integer.parseInt(number);
            } catch (NumberFormatException nfe) {
                LOG.debug("Invalid number found.", nfe);
            }
        }
        return -1;
    }

}
