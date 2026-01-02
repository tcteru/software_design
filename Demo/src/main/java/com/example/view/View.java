package com.example.View;

import java.awt.Point;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.example.events.ChartEvent;
import com.example.model.DataPoint;

import com.example.Controller.EconomyController;
import com.example.Controller.WeatherController;
import com.example.data.DataCollection;

import com.example.data.DataCollection;
import com.example.data.DataSeries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class View extends HBox {
    private final EconomyController eCtrl = new EconomyController();
    private final WeatherController wCtrl = new WeatherController();

    public View() {
        super();  // call the constructor of HBox

        // initialize the components
        initializeComponentLayouts();
        initializeComponentValues();
        initializeActions();

        // create and initialize the line chart
        chart = createNewChart();
        graphPane.setCenter(chart);
        updateChart(DataCollection.getEmptyCollection());
    }

    /**
     * Adds listeners to the chart selections.
     */
    public void addChartListener(Consumer<ChartEvent> handler) {
        locationComboBox.setOnAction(
            event -> handler.accept(getCurrentChartEvent())
        );
        weatherSelection.setOnAction(
            event -> handler.accept(getCurrentChartEvent())
        );
        economySelection.setOnAction(
            event -> handler.accept(getCurrentChartEvent())
        );
        economyComboBox.setOnAction(
            event -> handler.accept(getCurrentChartEvent())
        );
    }

    public void clearChart() {
        chart.getData().clear(); 
        ((CategoryAxis) chart.getXAxis()).getCategories().clear();  
        chart.setVisible(false);  
    }


    /**
     * Updates the shown chart.
     
    public void updateChart(DataCollection data) {
        if (chart == null) {
            return;
        }
        chart.getData().clear();

        if (data.getSeries().isEmpty()) {
            chart.setVisible(false);
            return;
        }

        chart.setTitle(data.getTitle());
        for (DataSeries series : data.getSeries()) {
            chart.getData().add(getXYSeries(series));
        }
        //updateAxis(data);
        chart.setVisible(true);
    }*/

    public void updateChart(DataCollection data) {
        if (chart == null) return;

        chart.getData().clear();

        // Show chart even if empty
        chart.setVisible(true);

        if (!data.getSeries().isEmpty()) {
            chart.setTitle(data.getTitle());
            for (DataSeries series : data.getSeries()) {
                chart.getData().add(getXYSeries(series));
            }
            updateYAxis(data);
        } 
        else {
            chart.setTitle("No data");
        }   
    }


    /**
     * Helper method to collect the current chart selections.
     */
    private ChartEvent getCurrentChartEvent() {
        List<String> dataSelections = FXCollections.observableArrayList();

        if (weatherSelection.isSelected()) {
            dataSelections.add("weather");
        }
        if (economySelection.isSelected()) {
            dataSelections.add("economy");
        }

        return new ChartEvent(
            locationComboBox.getValue(),
            dataSelections
        );
    }

    /**
     * Initializes all the UI components and their layouts for the main pane.
     */
    private void initializeComponentLayouts() {
        // The main horizontal box configuration
        this.setPrefHeight(100.0);
        this.setPrefWidth(200.0);

        // A vertical box for all user selections
        VBox userSelections = new VBox();
        userSelections.setPrefHeight(600.0);
        userSelections.setPrefWidth(200.0);
        userSelections.setSpacing(30.0);
        HBox.setMargin(userSelections, new javafx.geometry.Insets(20.0, 10.0, 10.0, 20.0));
        this.getChildren().add(userSelections);

        // A vertical box for all location management related components
        VBox locationManagement = new VBox();
        locationManagement.setSpacing(5.0);
        userSelections.getChildren().add(locationManagement);

        // A label and a combo box for location selection
        Label locationLabel = new Label();
        locationLabel.setAlignment(javafx.geometry.Pos.CENTER);
        locationLabel.setMinWidth(150.0);
        locationLabel.setFont(new Font(16.0));
        locationLabel.setText("Location");
        locationManagement.getChildren().add(locationLabel);

        locationComboBox = new ComboBox<>();
        locationComboBox.setPrefWidth(150.0);
        locationComboBox.setStyle("-fx-font-size: 16;");
        locationManagement.getChildren().add(locationComboBox);

        Label economyCBOXLabel = new Label("Economy Variable");
        economyCBOXLabel.setFont(new Font(16.0));

        economyComboBox = new ComboBox<>();
        economyComboBox.setStyle("-fx-font-size: 16;");
        economyComboBox.getItems().addAll(
            "CPI",
            "Electricity consumption"
        );
        economyComboBox.setValue("CPI");
        locationManagement.getChildren().addAll(economyCBOXLabel, economyComboBox);

        Label weatherCBOXLabel = new Label("Weather Variable");
        weatherCBOXLabel.setFont(new Font(16.0));

        weatherComboBox = new ComboBox<>();
        weatherComboBox.setStyle("-fx-font-size: 16;");
        weatherComboBox.getItems().addAll(
            "Average temperature",
            "Precipitaion"
        );
        weatherComboBox.setValue("Average temperature");
        locationManagement.getChildren().addAll(weatherCBOXLabel, weatherComboBox);

        Label startLabel = new Label("Start Date");
        startLabel.setFont(new Font(16));
        locationManagement.getChildren().add(startLabel);

        startDatePicker = new DatePicker();
        locationManagement.getChildren().add(startDatePicker);

        Label endLabel = new Label("End Date");
        endLabel.setFont(new Font(16));
        locationManagement.getChildren().add(endLabel);

        endDatePicker = new DatePicker();
        locationManagement.getChildren().add(endDatePicker);

        // A vertical box for all data source selection related components
        VBox dataManagement = new VBox();
        dataManagement.setMinWidth(150.0);
        dataManagement.setSpacing(5.0);
        userSelections.getChildren().add(dataManagement);

        // A label and two check boxes for data source selection
        Label dataTypeLabel = new Label();
        dataTypeLabel.setAlignment(javafx.geometry.Pos.CENTER);
        dataTypeLabel.setMinWidth(150.0);
        dataTypeLabel.setFont(new Font(16.0));
        dataTypeLabel.setText("Display data");
        dataManagement.getChildren().add(dataTypeLabel);

        weatherSelection = new CheckBox();
        weatherSelection.setMinWidth(150.0);
        weatherSelection.setFont(new Font(16.0));
        weatherSelection.setText("Weather");
        VBox.setMargin(weatherSelection, new javafx.geometry.Insets(0.0, 0.0, 0.0, 10.0));
        dataManagement.getChildren().add(weatherSelection);

        economySelection = new CheckBox();
        economySelection.setMinWidth(150.0);
        economySelection.setFont(new Font(16.0));
        economySelection.setText("Economy");
        VBox.setMargin(economySelection, new javafx.geometry.Insets(0.0, 0.0, 0.0, 10.0));
        dataManagement.getChildren().add(economySelection);

        // A button to refresh the charts
        calculateButton = new Button();
        calculateButton.setFont(new Font(16.0));
        calculateButton.setText("Update graph");
        VBox.setMargin(calculateButton, new javafx.geometry.Insets(10, 0, 0, 10));
        dataManagement.getChildren().add(calculateButton);

        // A button to quit the application
        quitButton = new Button();
        quitButton.setPrefWidth(60.0);
        quitButton.setFont(new Font(16.0));
        quitButton.setText("Quit");
        VBox.setMargin(quitButton, new javafx.geometry.Insets(0.0, 0.0, 0.0, 10.0));
        userSelections.getChildren().add(quitButton);

        // A border pane to hold the graph pane and the chart
        graphPane = new BorderPane();
        graphPane.setPrefWidth(600.0);
        this.getChildren().add(graphPane);
    }

    /**
     * Initializes all default values to the UI components.
     */
    private void initializeComponentValues() {
        // populate the combo boxes and set the default values
        locationComboBox.setItems(intervals);
        locationComboBox.setValue(intervals.get(0));
    }

    /**
     * Initializes all actions for the UI components.
     */
    private void initializeActions() {
        // quit the application when the user clicks the quit button
        quitButton.setOnAction(event -> quitButtonAction());
        calculateButton.setOnAction(event -> updateButtonAction());
    }

    /**
     * Closes the application.
     */
    private void quitButtonAction() {
        // get the Scene object from the quit button
        Scene scene = quitButton.getScene();
        if (scene != null) {
            scene.getWindow().hide();
        }
        else {
            System.out.println("Error: the quit button is not in a Scene");
        }
    }

    /**
     * Handler for the "update graph" button
     */
    private void updateButtonAction() {
        ChartEvent event = getCurrentChartEvent();
        List<String> selectedDatas = event.getDataSelections();

        String placeVar = locationComboBox.getValue();
        String selectedEconomyVar = economyComboBox.getValue();
        String ecoCode = "0";
            if (selectedEconomyVar != "CPI") {
                ecoCode = "SSS";
            }
        String selectedWeatherVar = weatherComboBox.getValue();
        String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : "2021-01-01";
        String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : "2024-12-31";

        // Voi muokata kutsumaan controlleria
        if (selectedDatas.isEmpty()) {
            System.err.println("Sää- ja/tai economydata tulee valita.");
            
        }
        else {
            DataCollection combined = DataCollection.getEmptyCollection();

            if (selectedDatas.contains("weather")) {
                // Voisi myös yhdistää yhdeksi ja vain kutsua fetchData ja asettaa parameter parametriksi selectedWeatherVar
                // ja jättää tulkinnan weathercontollerille johon input handler
                String dataCode = selectedWeatherVar.equals("Average temperature") ? "tmon" : "rrmon";
                DataCollection wData = wCtrl.fetchData(placeVar, dataCode, startDate + "T00:00:00Z", endDate + "T00:00:00Z");
                for (DataSeries s : wData.getSeries()) {
                    combined.addSeries(s);
                }
            }
            if (selectedDatas.contains("economy")) {
                // Saman tyyppinen kuin yläpuolella, jos economycontrolleriin lisää input handler ja economyservice hoitaa
                // month codejen luonnin, ja datan haun, economycontroller inputin handlen avulla kutsuu economyservice oikeilla
                // parameilla jne.
                try {

                    DataCollection eData = eCtrl.fetchEconomyData(startDate.substring(0, 4), startDate.substring(5, 7), endDate.substring(0, 4), 
                                                            endDate.substring(5, 7), ecoCode, selectedEconomyVar);
                    for (DataSeries s : eData.getSeries()) {
                        combined.addSeries(s);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            updateChart(combined);
        }
    }

    /**
     * Creates a line chart.
     * @return A new line chart
     */
    private LineChart<String, Number> createNewChart() {
        CategoryAxis xAxis = new CategoryAxis(); 
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        // disable auto-ranging and use the custom updateAxis method to update the axis

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        // set a fixed size for the chart
        lineChart.setMinWidth(CHART_WIDTH);
        lineChart.setMaxWidth(CHART_WIDTH);
        lineChart.setMinHeight(CHART_HEIGHT);
        lineChart.setMaxHeight(CHART_HEIGHT);

        // disable animations
        lineChart.setAnimated(false);

        return lineChart;
    }

    /**
     * Creates and returns an XYChart.Series object containing the same data points as the input data series.
     * @param series The data series to be transformed
     * @return XY series containing the input data
     
    private XYChart.Series<Number, Number> getXYSeries(DataSeries series) {
        XYChart.Series<Number, Number> xySeries = new XYChart.Series<>();

        xySeries.setName(series.getName());
        for (var point : series.getPoints()) {
            xySeries.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
        }

        return xySeries;
    } */

    private XYChart.Series<String, Number> getXYSeries(DataSeries series) {
        XYChart.Series<String, Number> xySeries = new XYChart.Series<>();
        xySeries.setName(series.getName());

        List<YearMonth> xVals = series.getXValues();
        List<Double> yVals = series.getYValues();

        for (int i = 0; i < series.size(); i++) {
            String xStr = xVals.get(i).toString(); // YYYY-MM format
            xySeries.getData().add(new XYChart.Data<>(xStr, yVals.get(i)));
        }

        return xySeries;
    }

    // Getters for end and start times
    public LocalDate getEndDate() {
        return endDatePicker.getValue();
    }

    public LocalDate getStartDate() {
        return startDatePicker.getValue();
    }

    private void updateYAxis(DataCollection data) {
    if (data.getSeries().isEmpty()) return;

    NumberAxis yAxis = (NumberAxis) chart.getYAxis();
    yAxis.setAutoRanging(false); // we manually scale

    // Collect all Y values
    double minY = Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;

    for (DataSeries series : data.getSeries()) {
        for (double y : series.getYValues()) {
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
    }

    // Add a small margin
    double range = maxY - minY;
    double margin = range * 0.1;
    if (margin == 0) margin = 1; // prevent flat line issue

    double lower = Math.floor((minY - margin));
    double upper = Math.ceil((maxY + margin));

    yAxis.setLowerBound(lower);
    yAxis.setUpperBound(upper);

    // Nice tick calculation
    double tick = (upper - lower) / NUMBER_OF_Y_TICKS;
    yAxis.setTickUnit(tick);
}



    /**
     * Updates the settings for the horizontal and vertical axis of the chart.
     * Tries to scale the axis based on the data in the chart in a human friendly way.
     * Does not access any other member variables other than the chart itself.
    
    private void updateAxis(DataCollection data) {
        if (data.getSeries().isEmpty()) {
            return;
        }

        // calculate the minimum and maximum for x and y values within the given data
        List<Point> pointList = data.getSeries().stream().map(DataSeries::getPoints).flatMap(Collection::stream).toList();
        List<Double> xList = pointList.stream().map(Point::getX).toList();
        List<Double> yList = pointList.stream().map(Point::getY).toList();
        double minX = xList.stream().min(Double::compareTo).orElseThrow();
        double maxX = xList.stream().max(Double::compareTo).orElseThrow();
        double minY = yList.stream().min(Double::compareTo).orElseThrow();
        double maxY = yList.stream().max(Double::compareTo).orElseThrow();

        // set up the x-axis
        Axis<Number> xAxis = chart.getXAxis();
        xAxis.setLabel(data.getXLabel());
        NumberAxis xNumberAxis = (NumberAxis) xAxis;
        xNumberAxis.setLowerBound(minX);
        xNumberAxis.setUpperBound(maxX);
        xNumberAxis.setTickUnit(1);

        // set up the y-axis
        Axis<Number> yAxis = chart.getYAxis();
        yAxis.setLabel(data.getYLabel());
        NumberAxis yNumberAxis = (NumberAxis) yAxis;
        double initialTick = maxY / NUMBER_OF_Y_TICKS;
        double tickSize = Math.pow(10, Math.floor(Math.log10(initialTick)));
        double tick = Math.round(initialTick / tickSize + 0.5) * tickSize;
        yNumberAxis.setLowerBound(Math.min(0, minY));
        yNumberAxis.setUpperBound(NUMBER_OF_Y_TICKS * tick);
        yNumberAxis.setTickUnit(tick);
    }*/

    // GUI components that are accessed outside the initializeComponentLayouts method
    private BorderPane graphPane;
    private Button calculateButton;
    private Button quitButton;
    private ComboBox<String> locationComboBox;
    public ComboBox<String> economyComboBox;
    public ComboBox<String> weatherComboBox;
    private ComboBox<String> operationComboBox;
    private CheckBox weatherSelection;
    private CheckBox economySelection;
    private Label resultLabel;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;


    // the chart that displays the data
    private final LineChart<String, Number> chart;

    // several static constants for the view
    private static final String ERROR_NO_OPERATOR = "No operator";
    private static final String ERROR_INVALID_NUMBER = "Invalid number";
    private static final String ERROR_NAN = "NaN";

    private static final ObservableList<String> intervals = FXCollections.observableArrayList("Helsinki", "Tampere");

    private static final int CHART_WIDTH = 550;
    private static final int CHART_HEIGHT = 550;
    private static final int NUMBER_OF_Y_TICKS = 6;

}