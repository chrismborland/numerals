package com.borland.numerals.service;

import java.util.Set;

/**
 * Service interface for converting numbers to numeral representations.
 * 
 * @author chrismborland
 */
public interface NumeralService {

    /**
     * Converts the <code>number</code> to a numeral.
     * 
     * Caller is responsible for validating number via {@link #isEligible(int)}
     * prior to calling.
     * 
     * @param number Number to convert.
     * @return A numeral pairs representing the numbers and associated numeral.
     *         Returns <code>null</code> on error.
     */
    public NumeralPair convertToNumeral(final int number);

    /**
     * Converts all numbers between <code>lowNumber</code> and
     * <code>highNumber</code> to numerals.
     * 
     * Caller is responsible for validating number range via
     * {@link #isRangeEligible(int, int)} prior to calling.
     * 
     * @param lowNumber  Low boundary of range of numbers to convert.
     * @param highNumber High boundary of range of numbers to convert.
     * @return Set of numeral pairs, ordered from lowest to highest, representing
     *         all of the numbers in the range and their associated numerals.
     *         Returns <code>null</code> on error.
     */
    public Set<NumeralPair> convertToNumeral(final int lowNumber, final int highNumber);

    /**
     * Check to see if the number is eligible for the underlying numeral service.
     * 
     * @param number Number to check.
     * @return <code>true</code>, if eligible. <code>false</code>, otherwise.
     */
    public boolean isEligible(final int number);

    /**
     * Checks to see if the number range is eligble for the underlying numeral
     * service.
     * 
     * @param lowNumber  Low end of range number.
     * @param highNumber High end of range number.
     * @return <code>true</code>, if number range is eligible. <code>false</code>,
     *         otherwise.
     */
    public boolean isRangeEligible(final int lowNumber, final int highNumber);

}