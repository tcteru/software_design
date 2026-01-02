package com.example.dto;

public class DataPointDTO {
    private String month; // YYYY-MM
    private double value;
    private String label;

    public DataPointDTO(String month, double value, String label) {
        this.month = month;
        this.value = value;
        this.label = label;
    }

    public String getMonth() { return month; }
    public double getValue() { return value; }
    public String getLabel() { return label; }
}