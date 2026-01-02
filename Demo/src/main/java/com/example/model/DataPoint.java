package com.example.model;

import java.time.YearMonth;

/**
 * Yleinen datapiste aikaleimalla (kuukausitasolla) ja arvolla.
 */
public class DataPoint {
    private final YearMonth month;
    private final double value;
    private final String label; // esim. "temperature", "rainfall", "cpi"

    public DataPoint(YearMonth month, double value, String label) {
        this.month = month;
        this.value = value;
        this.label = label;
    }

    public YearMonth getMonth() { return month; }
    public double getValue() { return value; }
    public String getLabel() { return label; }

    @Override
    public String toString() {
        return month + " [" + label + "] = " + value;
    }
}