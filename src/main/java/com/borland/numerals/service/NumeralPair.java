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
     * Default constructor.
     */
    public NumeralPair() {

    }

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
     * Set number portion of the pair.
     * 
     * @param number Number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * Get numeral portion of pair.
     * 
     * @return Numeral.
     */
    public String getNumeral() {
        return numeral;
    }

    /**
     * Set numeral portion of the pair.
     * 
     * @param numeral Numeral.
     */
    public void setNumeral(String numeral) {
        this.numeral = numeral;
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