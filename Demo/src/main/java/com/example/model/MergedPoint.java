package com.example.model;

import java.time.YearMonth;

/**
 * Yhdistetty datapiste kahdelle muuttujalle korrelaatioanalyysiä varten.
 */
public class MergedPoint {
    private final YearMonth month;
    private final Double xValue; // esim. sää
    private final Double yValue; // esim. talous

    public MergedPoint(YearMonth month, Double xValue, Double yValue) {
        this.month = month;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public YearMonth getMonth() { return month; }
    public Double getXValue() { return xValue; }
    public Double getYValue() { return yValue; }

    @Override
    public String toString() {
        return month + " X=" + xValue + " Y=" + yValue;
    }
}