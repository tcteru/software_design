package com.example.service;

import com.example.model.DataPoint;
import com.example.utils.XmlParser;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WeatherService {

    public List<DataPoint> fetchWeatherData(String place, String parameter, String startTime, String endTime) {
        try {
            String xml = downloadXml(place, parameter, startTime, endTime);
            return XmlParser.parseWeatherXml(xml);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // tyhjä lista virheen sattuessa
        }
    }

    private String downloadXml(String place, String parameter, String startTime, String endTime) throws Exception{

        String urlString = "http://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature"
            + "&storedquery_id=fmi::observations::weather::monthly::simple"
            + "&place=" + place
            + "&parameters=" + parameter
            + "&starttime=" + startTime
            + "&endtime=" + endTime;

        try (InputStream is = new URL(urlString).openStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
