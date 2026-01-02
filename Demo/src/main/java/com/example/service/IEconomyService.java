package com.example.service;

import com.example.model.DataPoint;
import java.util.List;

/**
 * Service-rajapinta talousdatalle (CPI, energia, sähkö tms.).
 * Controller / REST -kerros käyttää tätä abstraktiona eikä ole
 * riippuvainen toteutuksen yksityiskohdista (SOLID: DIP).
 */
public interface IEconomyService {

    /**
     * Hakee kuluttajahintaindeksin (CPI) kuukausittaiset arvot annetulle vuodelle.
     * @param year esim. 2024
     * @return Lista DataPoint-olioita (period, value, "cpi")
     */
    List<DataPoint> fetchMonthlyCPI(int year);

    /**
     * Yleistetty talousdatan hakumetodi eri endpointille / hyödykkeelle.
     * @param endpoint PxWeb endpoint (esim. Tilastokeskus URL)
     * @param months Kuukaudet muodossa ["2024M01", "2024M02", ...]
     * @param commodityCode Hyödykkeen koodi esim. "0"
     * @param label Label joka asetetaan DataPoint.metric kenttään (esim. "cpi", "electricity")
     * @return Lista DataPoint-olioita
     */
    List<DataPoint> fetchEconomyData(String endpoint, List<String> months, String commodityCode, String label);
}
