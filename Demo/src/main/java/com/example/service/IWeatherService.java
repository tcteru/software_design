package com.example.service;

import com.example.model.DataPoint;

import java.util.List;

/**
 * Service-rajapinta: tarjoaa sovelluslogiikan käyttöliittymälle/controllerille.
 * Palauttaa DataPointit, joita controller/REST käyttää.
 */
public interface IWeatherService {
    List<DataPoint> fetchWeatherData(String place, String parameter, String start, String end);
}
