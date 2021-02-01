package com.borland.numerals.servlet;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.borland.numerals.App;
import com.borland.numerals.service.NumeralPair;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test class for NumeralServlet
 * 
 * @author chrismborland
 */
public class NumeralServletTest extends Mockito {

    /**
     * Clear the metrics before each call.
     */
    @Before
    public void clearMetrics() {
        App.metrics.remove(App.METRIX_PREFIX + ".successResponseSizes");
        App.metrics.remove(App.METRIX_PREFIX + ".singleConversions");
        App.metrics.remove(App.METRIX_PREFIX + ".rangeConversions");
    }

    /**
     * Valid single conversion request. Checks the numeral pair, response status &
     * metrics.
     * 
     * @throws Exception
     */
    @Test
    public void validSingle() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("query")).thenReturn("1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        writer.flush();

        ObjectMapper mapper = new ObjectMapper();
        NumeralPair pair = mapper.readValue(stringWriter.toString(), NumeralPair.class);

        // verify values are correct.
        assertEquals(pair.getNumeral(), "I");
        assertEquals(pair.getNumber(), new Integer("1"));

        // verify response status correct.
        verify(response).setStatus(HttpServletResponse.SC_OK);

        // verify metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 1);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".singleConversions").getCount(), 1);

    }

    /**
     * Valid single conversion with a subtractive notation. Checks the numeral pair,
     * response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void validSingleSubtractive() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("query")).thenReturn("2784");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        writer.flush();

        ObjectMapper mapper = new ObjectMapper();
        NumeralPair pair = mapper.readValue(stringWriter.toString(), NumeralPair.class);

        // verify values are correct.
        assertEquals(pair.getNumeral(), "MMDCCLXXXIV");
        assertEquals(pair.getNumber(), new Integer("2784"));

        // verify response status correct.
        verify(response).setStatus(HttpServletResponse.SC_OK);

        // verify metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 1);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".singleConversions").getCount(), 1);

    }

    /**
     * Valid range conversion. Checks the set of numeral pair, response status &
     * metrics.
     * 
     * @throws Exception
     */
    @Test
    public void validRange() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("min")).thenReturn("1");
        when(request.getParameter("max")).thenReturn("2");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        writer.flush();

        ObjectMapper mapper = new ObjectMapper();
        NumeralPair[] pairs = mapper.readValue(stringWriter.toString(), NumeralPair[].class);

        // verify values are correct.
        assertEquals(pairs[0].getNumeral(), "I");
        assertEquals(pairs[0].getNumber(), new Integer("1"));
        assertEquals(pairs[1].getNumeral(), "II");
        assertEquals(pairs[1].getNumber(), new Integer("2"));

        // verify response status correct.
        verify(response).setStatus(HttpServletResponse.SC_OK);

        // verify metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 1);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".rangeConversions").getCount(), 1);

    }

    /**
     * Valid range conversion containing a number with subtractive notation. Checks
     * the numeral pair, response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void validRangeWithSubtractive() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("min")).thenReturn("898");
        when(request.getParameter("max")).thenReturn("902");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        writer.flush();

        ObjectMapper mapper = new ObjectMapper();
        NumeralPair[] pairs = mapper.readValue(stringWriter.toString(), NumeralPair[].class);

        // verify values are correct.
        assertEquals(pairs[0].getNumeral(), "DCCCXCVIII");
        assertEquals(pairs[0].getNumber(), new Integer("898"));
        assertEquals(pairs[1].getNumeral(), "DCCCXCIX");
        assertEquals(pairs[1].getNumber(), new Integer("899"));
        assertEquals(pairs[2].getNumeral(), "CM");
        assertEquals(pairs[2].getNumber(), new Integer("900"));
        assertEquals(pairs[3].getNumeral(), "CMI");
        assertEquals(pairs[3].getNumber(), new Integer("901"));
        assertEquals(pairs[4].getNumeral(), "CMII");
        assertEquals(pairs[4].getNumber(), new Integer("902"));

        // verify response status correct.
        verify(response).setStatus(HttpServletResponse.SC_OK);

        // verify metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 1);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".rangeConversions").getCount(), 1);

    }

    /**
     * Invalid request with no parameters. Checks the response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void invalidNoParams() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // verify no metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 0);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".singleConversions").getCount(), 0);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".rangeConversions").getCount(), 0);

    }

    /**
     * Invalid single conversion request where an invalid string is passed in,
     * instead of a number. Checks response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void invalidSingleNotANumber() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("query")).thenReturn("asdf");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // verify no metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 0);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".singleConversions").getCount(), 0);

    }

    /**
     * Invalid single conversion where the query parameter is too high. Checks the
     * response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void invalidSingleTooHigh() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("query")).thenReturn("4000");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // verify no metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 0);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".singleConversions").getCount(), 0);

    }

    /**
     * Invalid single conversion where the query parameter is too low. Checks the
     * response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void invalidSingleTooLow() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("query")).thenReturn("0");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // verify no metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 0);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".singleConversions").getCount(), 0);

    }

    /**
     * Invalid range conversion where the min parameter is too low. Checks the
     * response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void invalidRangeTooLow() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("min")).thenReturn("0");
        when(request.getParameter("max")).thenReturn("3999");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // verify no metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 0);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".rangeConversions").getCount(), 0);

    }

    /**
     * Invalid range conversion where the max parameter is too high. Checks the
     * response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void invalidRangeTooHigh() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("min")).thenReturn("1");
        when(request.getParameter("max")).thenReturn("4000");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // verify no metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 0);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".rangeConversions").getCount(), 0);

    }

    /**
     * Invalid range conversion where the min parameter is higher than the max
     * parameter. Checks the response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void invalidRangeMinHigherThanMax() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("min")).thenReturn("3999");
        when(request.getParameter("max")).thenReturn("3000");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new NumeralServlet().doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // verify no metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 0);
        assertEquals(App.metrics.timer(App.METRIX_PREFIX + ".rangeConversions").getCount(), 0);

    }

    /**
     * Valid single conversion where an exception was thrown during the processing.
     * Checks the response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void validSingleInterrupted() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("query")).thenReturn("3000");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        Thread.currentThread().interrupt();

        new NumeralServlet().doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        // verify no metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 0);

    }

    /**
     * Valid range conversion where an exception was thrown during the processing.
     * Checks the response status & metrics.
     * 
     * @throws Exception
     */
    @Test
    public void validRangeInterrupted() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("min")).thenReturn("3000");
        when(request.getParameter("max")).thenReturn("3999");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        Thread.currentThread().interrupt();

        new NumeralServlet().doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        // verify no metrics captured.
        assertEquals(App.metrics.histogram(App.METRIX_PREFIX + ".successResponseSizes").getCount(), 0);

    }

}
