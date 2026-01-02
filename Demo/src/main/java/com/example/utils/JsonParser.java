package com.example.utils;

import com.example.model.DataPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Parser for PxWeb json-stat2 responses.
 */
public class JsonParser {

    private static final DateTimeFormatter MONTH_CODE_FORMATTER = DateTimeFormatter.ofPattern("yyyy'M'MM");

    /**
     * Extracts month codes from dimension.Kuukausi.category.index in correct order.
     */
    public static List<String> extractMonthCodes(String json) {
            JsonObject root = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
            JsonObject indexObj = root.getAsJsonObject("dimension")
                    .getAsJsonObject("Kuukausi")
                    .getAsJsonObject("category")
                    .getAsJsonObject("index");
    
            // Collect keys and sort by their integer index to preserve API order
            List<String> codes = new ArrayList<>(indexObj.keySet());
            codes.sort(Comparator.comparingInt(c -> indexObj.get(c).getAsInt()));
            return codes;
        }
    
        /**
         * Parses monthly values into DataPoint list using provided month codes.
         * Label is set into DataPoint.metric (e.g., "cpi").
         */
        public static List<DataPoint> parseMonthly(String json, List<String> monthCodes, String label) {
                    JsonObject root = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                    JsonArray values = root.getAsJsonArray("value");
                    JsonObject indexObj = root.getAsJsonObject("dimension")
                            .getAsJsonObject("Kuukausi")
                            .getAsJsonObject("category")
                            .getAsJsonObject("index");
            
                    List<DataPoint> points = new ArrayList<>();
                    for (String code : monthCodes) {
                        int idx = indexObj.get(code).getAsInt();
                        double value = values.get(idx).isJsonNull() ? Double.NaN : values.get(idx).getAsDouble();
                        YearMonth ym = YearMonth.parse(code, MONTH_CODE_FORMATTER);
                        points.add(new DataPoint(ym, value, label));
                    }
                    return points;
                }
            
                /**
                 * Convenience method: parse with month codes extracted from JSON automatically.
                 */
                public static List<DataPoint> parseMonthly(String json, String label) {
                    List<String> months = extractMonthCodes(json);
                return parseMonthly(json, months, label);
    }
}