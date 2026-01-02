/* 
package com.example.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.example.data.DataCollection;
import com.example.events.ChartEvent;
import com.example.View.View;

public class ViewController {

    private final View view;
    private final WeatherController wCtrl = new WeatherController();
    private final EconomyController eCtrl = new EconomyController();

    public ViewController(View view) {
        this.view = view;
        initializeView();
    }

    private void initializeView() {
        view.addChartListener(this::handleChartEvent);
    }

    private void handleChartEvent(ChartEvent event) throws IOException, InterruptedException {
        LocalDate start = view.getStartDate();
        LocalDate end = view.getEndDate();
        List<String> selections = event.getDataSelections();
        String place = event.getTimeSelection();

        if (selections.contains("weather")) {
            String code = view.weatherComboBox.getValue().equals("Average temperature") ? "tmon" : "rrmon";
            DataCollection wData = wCtrl.fetchData(place, code, start.toString() + "T00:00:00Z", end.toString() + "T00:00:00Z");
            view.updateChart(wData);
        }
        if (selections.contains("economy")) {
            String ecoVar = view.economyComboBox.getValue();
            String ecoCode = "0";
            if (ecoVar != "CPI") {
                ecoCode = "SSS";
            }
            DataCollection eData = eCtrl.fetchEconomyData(String.valueOf(start.getYear()), String.valueOf(start.getMonth()), String.valueOf(end.getYear()), 
                                                            String.valueOf(end.getMonth()), ecoCode, ecoVar);
            view.updateChart(eData);
        }
    }    
}
    */
