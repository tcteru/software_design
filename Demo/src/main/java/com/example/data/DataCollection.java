package com.example.data;

import java.util.ArrayList;
import java.util.List;

/**
 * The DataCollection class represents a collection of data series.
 */
public class DataCollection {

    private final String title;
    private final String xLabel;
    private final String yLabel;
    private final List<DataSeries> seriesList;

    /**
     * Creates a new data collection object using the given parameters. The list for the data series will be empty.
     *
     * @param title  title for the data collection
     * @param xLabel label for the x-axis
     * @param yLabel label for the y-axis
     */
    public DataCollection(String title, String xLabel, String yLabel) {
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.seriesList = new ArrayList<>();
    }

    public static DataCollection getEmptyCollection() {
        return new DataCollection("", "", "");
    }

    /**
     * Adds a DataSeries to the collection if it contains points.
     *
     * @param series the DataSeries to be added
     */
    public void addSeries(DataSeries series) {
        if (series == null || series.getXValues().isEmpty() || series.getYValues().isEmpty()) {
            return;
        }
        this.seriesList.add(series);
    }

    public String getTitle() {
        return this.title;
    }

    public String getXLabel() {
        return this.xLabel;
    }

    public String getYLabel() {
        return this.yLabel;
    }

    public List<DataSeries> getSeries() {
        return this.seriesList;
    }

    public void printCollection() {
    System.out.println("Collection: " + title);
    System.out.println("X Label: " + xLabel);
    System.out.println("Y Label: " + yLabel);

    if (seriesList.isEmpty()) {
        System.out.println("  (no data series)");
        return;
        }

    for (DataSeries series : seriesList) {
        series.printSeries();
        }
    }

}
