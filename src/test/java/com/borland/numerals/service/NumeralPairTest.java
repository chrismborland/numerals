package com.borland.numerals.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for NumeralPair.
 * 
 * @author chrismborland
 */
public class NumeralPairTest {

    /**
     * Verify toString functionality.
     */
    @Test
    public void toStringTest() {
        NumeralPair pair = new NumeralPair(1, "I");
        assertEquals("1: I", pair.toString());
    }

}
