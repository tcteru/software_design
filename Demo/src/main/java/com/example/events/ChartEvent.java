package com.example.events;

import java.util.List;

public class ChartEvent {
    public ChartEvent(String timeSelection, List<String> dataSelections) {
        this.timeSelection = timeSelection;
        this.dataSelections = dataSelections;
    }

    public String getTimeSelection() {
        return timeSelection;
    }

    public List<String> getDataSelections() {
        return dataSelections;
    }

    private final String timeSelection;
    private final List<String> dataSelections;
}
