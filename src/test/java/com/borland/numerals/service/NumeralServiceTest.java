package com.borland.numerals.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.borland.numerals.service.impl.StandardFormNumeralServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for numeral service. Currently only test the standard form
 * service.
 * 
 * @author chrismborland
 */
public class NumeralServiceTest {

    private NumeralService numeralService;

    @Before
    public void init() {
        numeralService = new StandardFormNumeralServiceImpl();
    }

    @After
    public void teardown() {
        numeralService = null;
    }

    /**
     * Verify too large of single number is rejected.
     */
    @Test
    public void isEligibleNumberTooLarge() {
        assertFalse(numeralService.isEligible(4000));
    }

    /**
     * Verify too small of single number is rejected.
     */
    @Test
    public void isEligibleNumberTooSmall() {
        assertFalse(numeralService.isEligible(0));
    }

    /**
     * Verify eligible number is accepted.
     */
    @Test
    public void isEligibleValid() {
        assertTrue(numeralService.isEligible(45));
    }

    /**
     * Verify too small of min number is rejected.
     */
    @Test
    public void isRangeEligibleMinInvalid() {
        assertFalse(numeralService.isRangeEligible(0, 398));
    }

    /**
     * Veify too large of min number is rejected.
     */
    @Test
    public void isRangeEligibleMaxInvalid() {
        assertFalse(numeralService.isRangeEligible(1, 4000));
    }

    /**
     * Verify min greater than max is rejected.
     */
    @Test
    public void isRangeEligibleMinGreaterThanMax() {
        assertFalse(numeralService.isRangeEligible(20, 12));
    }

    /**
     * Verify valid range is accepted.
     */
    @Test
    public void isRangeEligibleValid() {
        assertTrue(numeralService.isRangeEligible(1, 3999));
    }

    /**
     * Verify low single number conversion.
     */
    @Test
    public void convertSingleNumberLow() {
        assertEquals(numeralService.convertToNumeral(125).getNumeral(), "CXXV");
    }

    /**
     * Verify high single number conversion.
     */
    @Test
    public void convertSingleNumberHigh() {
        assertEquals(numeralService.convertToNumeral(3888).getNumeral(), "MMMDCCCLXXXVIII");
    }

    /**
     * Verify range conversion.
     */
    @Test
    public void convertRange() {
        Set<NumeralPair> pairs = numeralService.convertToNumeral(1, 5);
        assertEquals(pairs.size(), 5);
        List<NumeralPair> pairList = new ArrayList<NumeralPair>(pairs);
        assertEquals(pairList.get(0).getNumeral(), "I");
        assertEquals(pairList.get(1).getNumeral(), "II");
        assertEquals(pairList.get(2).getNumeral(), "III");
        assertEquals(pairList.get(3).getNumeral(), "IV");
        assertEquals(pairList.get(4).getNumeral(), "V");
    }

    /**
     * Verify error during single conversion returns null.
     */
    @Test
    public void convertSingleInterrupted() {
        Thread.currentThread().interrupt();
        NumeralPair pair = numeralService.convertToNumeral(1);
        assertEquals(pair, null);
    }

    /**
     * Verify error during range conversion returns null.
     */
    @Test
    public void convertRangeInterrupted() {
        Thread.currentThread().interrupt();
        Set<NumeralPair> pairs = numeralService.convertToNumeral(1, 5);
        assertEquals(pairs, null);
    }

}
