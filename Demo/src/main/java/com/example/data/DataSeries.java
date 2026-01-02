package com.example.data;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a named series of data points with YearMonth x-arvot and Double y-arvot.
 */
public class DataSeries {
    private final String name;
    private final List<YearMonth> xValues;
    private final List<Double> yValues;

    /**
     * Luo uuden DataSeries-olion.
     *
     * @param name nimi sarjalle
     * @param xValues lista x-arvoista (YearMonth)
     * @param yValues lista y-arvoista (Double)
     */
    public DataSeries(String name, List<YearMonth> xValues, List<Double> yValues) {
        this.name = name;
        this.xValues = new ArrayList<>(xValues);
        this.yValues = new ArrayList<>(yValues);
    }

    public String getName() {
        return name;
    }

    public List<YearMonth> getXValues() {
        return xValues;
    }

    public List<Double> getYValues() {
        return yValues;
    }

    public int size() {
        return Math.min(xValues.size(), yValues.size());
    }

    public void printSeries() {
    System.out.println("Series: " + name);

    for (int i = 0; i < size(); i++) {
        System.out.println("  " + xValues.get(i) + " -> " + yValues.get(i));
        }
    }
}
