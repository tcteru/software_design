package com.example.Controller;

import com.example.model.DataPoint;
import com.example.data.DataCollection;
import com.example.data.DataSeries;
import com.example.service.WeatherService;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class WeatherController implements DataFetcherController {

    private final WeatherService weatherService = new WeatherService();

    /**
     * Hakee säädataa ja muuntaa sen DataCollection-muotoon
     */
    public DataCollection fetchData(String place, String parameter, String startTime, String endTime) {
        List<DataPoint> dataPoints = weatherService.fetchWeatherData(place, parameter, startTime, endTime);

        List<YearMonth> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        for (DataPoint dp : dataPoints) {
            xValues.add(dp.getMonth());  // YearMonth
            yValues.add(dp.getValue()); // Double
        }

        DataSeries series = new DataSeries(parameter, xValues, yValues);
        DataCollection collection = new DataCollection("Weather Data", "Month", parameter);
        collection.addSeries(series);

        return collection;
    }
}
