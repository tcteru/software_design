package com.example.service;

import com.example.model.DataPoint;
import com.example.utils.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * EconomyService — samanlainen rakenne kuin WeatherService:
 * - Lataa JSON PxWeb-rajapinnasta
 * - Delegoi parsimisen utils-luokalle (JsonStatParser)
 * - Palauttaa List<DataPoint>
 */
public class EconomyService {
    // PxWeb (Tilastokeskus) CPI-endpoint (yleinen kokonaisindeksi)
    private static final String CPI_ENDPOINT =
            "https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/khi/statfin_khi_pxt_11xf.px";
    public static final String ELECTRICITY_ENDPOINT =
                "https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/ehk/statfin_ehk_pxt_12su.px";
    public static final String ENERGY_ENDPOINT =
                "https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/ehk/statfin_khi_pxt_12st.px";
    /**
     * Hakee kuukausittaisen CPI:n (kuluttajahintaindeksi) annetulle vuodelle.
     * @param year esim. 2024
     * @return lista datapisteitä (YearMonth, value, "cpi")
     */
    public List<DataPoint> fetchMonthlyCPI(int year) {
        try {
            List<String> months = buildMonthCodes(year);
            // "0" = Hyödyke-koodissa yleinen kokonaisindeksi
            String json = downloadJson("CPI", months, "0");
            try {
                return JsonParser.parseMonthly(json, "cpi");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // tyhjä lista virheen sattuessa
        }
                return null;
    }

    /**
     * Yleistetty metodi muiden PxWeb-talouddatamittareiden hakemiseen.
     * @param dataType Määrittää mistä URL data haetaan. "CPI" -> CPI_ENDPOINT, "SSS" -> ELECTRICITY_ENDPOINT
     * @param months Kuukaudet muodossa ["2024M01", "2024M02", ...]
     * @param commodityCode Hyödykkeen koodi, esim. "0"
     * @param label Mittarin nimi, esim. "cpi"
     */
    public List<DataPoint> fetchEconomyData(String dataType, List<String> months, String commodityCode, String label) {
        String endpoint = "";

        if (dataType.equals("CPI")) {
            endpoint = CPI_ENDPOINT;
        }
        else if (dataType.equals("Electricity consumption")) {
            endpoint = ELECTRICITY_ENDPOINT;
        }

        try {
            String json = downloadJson(endpoint, months, commodityCode);
            return JsonParser.parseMonthly(json, label);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Rakentaa JSON-body:n ja tekee POST-pyynnön PxWeb-rajapintaan
    /**
     * 
     * @param endpoint Haluttu endpoint johon post tehdään esim. "cpi" tai electricity"
     * @param months String lista kuukausista joille haku tehdään. Muodossa "yyyyMmm"
     * @param commodityCode "0" = cpi, "SSS" = Sähkö
     * @return
     * @throws Exception
     */
    private String downloadJson(String endpoint, List<String> months, String commodityCode) throws Exception {
        String dataType = "";
        
        if (endpoint.equals(CPI_ENDPOINT)) {
            dataType = "cpi";
        } 
        else if (endpoint.equals(ELECTRICITY_ENDPOINT)) {
            dataType = "electricity";
        }

        String body = buildQuery(months, commodityCode, dataType);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 300) {
            throw new IllegalStateException("PxWeb API status " + resp.statusCode());
        }
        return resp.body();
    }

    /**
     * Rakentaa queryn API kutsua varten
     * @param months Kuukaudet
     * @param commodityCode "0" cpi:lle, "SSS" sähkönkulutukselle
     * @param queryType "CPI" jos luodaan kutsu CPI hakua varten "electricity" jos sähkönkulutukselle
     * @return Queryn 
     */
    private String buildQuery(List<String> months, String commodityCode, String queryType) {
        JsonObject root = new JsonObject();
        JsonArray queryArr = new JsonArray();

        // Kuukausi-valinta
        JsonObject monthObj = new JsonObject();
        monthObj.addProperty("code", "Kuukausi");
        JsonObject monthSel = new JsonObject();
        monthSel.addProperty("filter", "item");
        JsonArray monthValues = new JsonArray();
        for (String m : months) monthValues.add(m);
        monthSel.add("values", monthValues);
        monthObj.add("selection", monthSel);
        queryArr.add(monthObj);

        // Hyödyke / mittari -valinta
        JsonObject commodityObj = new JsonObject();

        if (queryType == "cpi") {
            // yksi queryn lohko jossa code eka elementti
            commodityObj.addProperty("code", "Hyödyke");
            JsonObject commoditySel = new JsonObject();
            commoditySel.addProperty("filter", "item");
            JsonArray commodityValues = new JsonArray();
            commodityValues.add(commodityCode);
            commoditySel.add("values", commodityValues);
            commodityObj.add("selection", commoditySel);
            queryArr.add(commodityObj);

            root.add("query", queryArr);
        }
        else if (queryType == "electricity") {
            commodityObj.addProperty("code", "Sähkön tuotanto/hankinta");
            JsonObject commoditySel = new JsonObject();
            commoditySel.addProperty("filter", "item");
            JsonArray commodityValues = new JsonArray();
            commodityValues.add(commodityCode);
            commoditySel.add("values", commodityValues);
            commodityObj.add("selection", commoditySel);
            queryArr.add(commodityObj);

            JsonObject commodityObj2 = new JsonObject();
            commodityObj2.addProperty("code", "Tiedot");
            JsonObject commoditySel2 = new JsonObject();
            commoditySel2.addProperty("filter", "item");
            JsonArray commodityValues2 = new JsonArray();
            commodityValues2.add("maara_gwh");
            commoditySel2.add("values", commodityValues2);
            commodityObj2.add("selection", commoditySel2);
            queryArr.add(commodityObj2);

            root.add("query", queryArr);
        }

        // Vastausmuoto
        JsonObject response = new JsonObject();
        response.addProperty("format", "json-stat2");
        root.add("response", response);

        String body = new Gson().toJson(root);

        return body;
    }

    /**
     * Apuri: listaa yhden tietyn vuoden kaikki kuukaudet muodossa "YYYYMM"
     * @param year Vuosi jolle kuukausikoodit luodaan
     * @return Listan ym. string elementeistä
     */
    private List<String> buildMonthCodes(int year) {
        List<String> months = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            months.add(String.format("%dM%02d", year, m));
        }
        return months;
    }

    /**
     * Apuri: listaa tietyn aikavälin kaikki kuukaudet muodossa "YYYYMM"
     * @param startYear aikavälin alkuvuosi
     * @param endYear aikavälin loppuvuosi
     * @return Listan ym. string elementeistä
     */
    private List<String> buildMonthCodes(int startYear, int endYear) {
        List<String> months = new ArrayList<>();
        for (int y = startYear; y <= endYear; y++) {
            months.addAll(buildMonthCodes(y));
        }
        return months;
    }

    /**
     * Apuri: listaa tietyn vuosien ja kuukausi -aikaikkunan kuukaudet muodossa "YYYYMM"
     * Eli esim. 2015 tammikuu - 2019 heinäkuu.  
     * @param startYear Aikaikkunan alkuvuosi
     * @param startMonth Aikaikkunan alkukuukausi
     * @param endYear Aikaikkunan loppuvuosi
     * @param endMonth Aikaikkunan loppukuukausi
     * @return Ym. kuukausikoodien listan stringejä
     */
    public List<String> buildMonthCodes(int startYear, int startMonth, int endYear, int endMonth) {
        List<String> months = new ArrayList<>();
        
        // If start == end, return list from start month to end month
        if (startYear == endYear) {
            months.addAll(buildMonthCodesByMonth(startMonth, endMonth, endYear));
        }

        // If two seperate years with certain months
        else if (startYear - endYear == 1) {
            months.addAll(buildMonthCodesByMonth(startMonth, 12, startYear));
            months.addAll(buildMonthCodesByMonth(1, endMonth, endYear));
        }
        
        // If at least 3 years in the time window with certain months
        else {
            months.addAll(buildMonthCodesByMonth(startMonth, 12, startYear));
            for (int y = startYear + 1; y < endYear; y++) {
                months.addAll(buildMonthCodes(y));
            }
            months.addAll(buildMonthCodesByMonth(1, endMonth, endYear));
        }

        return months;        
    }
    
    /**
     * Listaa tietyn vuoden KUUKAUSIEN AIKAVÄLIN kuukausikoodit
     * @param startMonth Aikaikkunan alkukuukausi
     * @param endMonth Aikaikkunan loppukuuukausi
     * @param year Haluttu vuosi
     * @return Ym. kuukausikoodit string listana
     */
    private List<String> buildMonthCodesByMonth(int startMonth, int endMonth, int year) {
        List<String> months = new ArrayList<>();
        for (int m = startMonth; m <= endMonth; m++) {
            months.add(String.format("%dM%02d", year, m));
        }
        return months;
    }

    public static void main(String[] args) throws Exception {
        EconomyService es = new EconomyService();
        List<String> months = es.buildMonthCodes(2021);
        List<DataPoint> dp = es.fetchEconomyData("CPI", months, "0", "cpi");
        List<DataPoint> dp2 = es.fetchEconomyData("SSS", months, "SSS", "electricity");
        System.out.println(dp);
        System.out.println(dp2);
    }
}
