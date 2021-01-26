package com.borland.numerals.service;

/**
 * A simple sortable object for keeping track of numbers and their corresponding
 * numeral representation.
 * 
 * @author chrismborland
 */
public class NumeralPair implements Comparable<NumeralPair> {

    private int number;
    private String numeral;

    /**
     * Public constructor.
     * 
     * @param number  Number.
     * @param numeral Numeral.
     */
    public NumeralPair(int number, String numeral) {
        this.number = number;
        this.numeral = numeral;
    }

    /**
     * Get number portion of pair.
     * 
     * @return Number.
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Get numeral portion of pair.
     * 
     * @return Numeral.
     */
    public String getNumeral() {
        return numeral;
    }

    @Override
    public int compareTo(NumeralPair o) {
        return this.getNumber().compareTo(o.getNumber());
    }

    @Override
    public String toString() {
        return Integer.toString(getNumber()) + ": " + getNumeral();
    }

}