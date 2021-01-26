package com.borland.numerals.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.borland.numerals.service.NumeralPair;

/**
 * Callable task for converting numbers to standard form Roman numerals.
 * 
 * @author chrismborland
 */
public class StandardFormNumeralCallable implements Callable<NumeralPair> {

    /**
     * Integer to numeral map.
     */
    public static final Map<Integer, String> numeralMap = new LinkedHashMap<Integer, String>();

    /**
     * Populate integer to numeral map. Entries are added from largest to smallest
     * as we will be looking for the largest possible match, while traversing our
     * keyset.
     * 
     * NOTE: We include specific mappings for 900, 400, 90, 40, 9, 4 because while
     * they are comprised of known symbols, they use subtractive notation (vs
     * additive) and are anomalies to our logic.
     */
    static {
        numeralMap.put(1000, "M");
        numeralMap.put(900, "CM");
        numeralMap.put(500, "D");
        numeralMap.put(400, "CD");
        numeralMap.put(100, "C");
        numeralMap.put(90, "XC");
        numeralMap.put(50, "L");
        numeralMap.put(40, "XL");
        numeralMap.put(10, "X");
        numeralMap.put(9, "IX");
        numeralMap.put(5, "V");
        numeralMap.put(4, "IV");
        numeralMap.put(1, "I");
    }

    private Integer numberToConvert;

    /**
     * Public constructor.
     * 
     * @param number Number to convert to numeral.
     */
    public StandardFormNumeralCallable(final int number) {
        this.numberToConvert = number;
    }

    /**
     * Recursively converts a number to a numeral by: 
     * 
     *   1. Finding the largest number in the numeral map that fits into the current number.
     *   2. Appends the numeral associated with number found in (1.) to the string builder.
     *   3. Subtracts the number found in (1.) from current number.
     *   4. Calls itself with result from (3.) and string builder.
     * 
     * The recursions stops when the resulting number in (3.) is 0 with the string builder
     * containing the result of the full conversion.
     * 
     * @param number Current number being converted.
     * @param sb     String builder to append numerals while converting.
     */
    private void getNumeral(int number, StringBuilder sb) {
        // check if done.
        if (number > 0) {
            // not done.  loop map to find largest mapped numeral.
            for (final int key : numeralMap.keySet()) {
                if (number >= key) {
                    // add numeral to string builder.
                    sb.append(numeralMap.get(key));
                    // call with remainder and string builder.
                    getNumeral(number - key, sb);
                    // stop looping map.
                    break;
                }
            }
        }
    }

    @Override
    public NumeralPair call() throws Exception {
        StringBuilder sb = new StringBuilder();
        getNumeral(numberToConvert, sb);
        return new NumeralPair(numberToConvert, sb.toString());
    }

}