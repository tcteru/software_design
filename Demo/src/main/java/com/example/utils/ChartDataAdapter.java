package com.example.utils;

import com.example.data.DataCollection;
import com.example.data.DataSeries;
import com.example.model.DataPoint;

import java.util.ArrayList;
import java.util.List;

public class ChartDataAdapter {

    /**
     * Muuntaa List<DataPoint> DataSeries-objektiksi.
     */
    public static DataSeries toDataSeries(List<DataPoint> dataPoints, String seriesName) {
        List<Double> yValues = new ArrayList<>();
        List<java.time.YearMonth> xValues = new ArrayList<>();

        for (DataPoint dp : dataPoints) {
            xValues.add(dp.getMonth()); // YearMonth suoraan
            yValues.add(dp.getValue());
        }

        return new DataSeries(seriesName, xValues, yValues);
    }

    /**
     * Luo DataCollection yhdestä tai useammasta List<DataPoint>-listasta.
     */
    public static DataCollection toDataCollection(List<List<DataPoint>> allData, List<String> seriesNames,
                                                  String title, String xLabel, String yLabel) {
        DataCollection collection = new DataCollection(title, xLabel, yLabel);

        for (int i = 0; i < allData.size(); i++) {
            DataSeries series = toDataSeries(allData.get(i), seriesNames.get(i));
            collection.addSeries(series);
        }

        return collection;
    }
}
