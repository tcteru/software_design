package com.example.Controller;

import com.example.data.DataCollection;
import com.example.data.DataSeries;
import com.example.model.DataPoint;
import com.example.service.EconomyService;
import com.example.utils.JsonParser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class EconomyController {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final JsonParser parser = new JsonParser();
    private final EconomyService economyService = new EconomyService();

    // Endpoints (Tilastokeskus pxdata)
    public static final String CPI_ENDPOINT =
                "https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/khi/statfin_khi_pxt_11xf.px";
    public static final String ELECTRICITY_ENDPOINT =
                "https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/khi/statfin_khi_pxt_12su.px";
    public static final String ENERGY_ENDPOINT =
                "https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/khi/statfin_khi_pxt_12st.px";

    /**
     * Hakee kuukausittaisen CPI:n (kuluttajahintaindeksi) valitulle vuodelle.
     * Hyödyke "0" = yleinen kokonaisindeksi.
     */
    public List<DataPoint> fetchMonthlyCPI(int year) throws IOException, InterruptedException {
        List<String> months = buildMonthCodes(year);
        JsonObject query = buildQuery(months, "0");
        String body = buildRequestBody(query);
        String response = postJson(CPI_ENDPOINT, body);
        return parser.parseMonthly(response, "cpi");
    }

    /**
     * Yleistetty talousdatan haku eri endpointille / hyödykkeelle.
     * @param endpoint PxWeb endpoint
     * @param months Kuukaudet muodossa ["2024M01", "2024M02", ...]
     * @param commodityCode Hyödykkeen koodi, esim. "0"
     * @param label DataPoint.metric -kenttään asetettava tunniste (esim. "CPI", "Electricity consumption")
     */
    public DataCollection fetchEconomyData(String startyear, String startmonth, String endyear, String endmonth, String commodityCode, String label)
            throws IOException, InterruptedException {

        //label = CPI tai Electrcitiy consumption
        
        //String urlToCall = handleEndPointInp(label);

        // Aiemmin parametrina oli suoraan months lista, mutta parametrit muutettu
        // inputeiksi viewistä ja economyservice voi hoitaa niiden luomisen 
        List<String> months = economyService.buildMonthCodes(Integer.parseInt(startyear), Integer.parseInt(startmonth), Integer.parseInt(endyear), Integer.parseInt(endmonth));
        
        
        List<DataPoint> dataPoints = economyService.fetchEconomyData(label, months, commodityCode, commodityCode);

        List<YearMonth> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        for (DataPoint dp : dataPoints) {
            xValues.add(dp.getMonth());  // YearMonth
            yValues.add(dp.getValue()); // Double
        }

        DataSeries series = new DataSeries(label, xValues, yValues);
        DataCollection collection = new DataCollection("Weather Data", "Month", label);
        collection.addSeries(series);
        return collection;
    }

    private String handleEndPointInp(String endPointInput) {
        if (endPointInput == "CPI") {
            return CPI_ENDPOINT;
        } 
        else if (endPointInput == "Electricity consumption") {
            return ELECTRICITY_ENDPOINT;
        }
        else if (endPointInput == "Energy consumption") {
            return ENERGY_ENDPOINT;
        }
        else {
            System.err.println("Error in selecting economy fetching endpoint. Fetching CPI as a default");
            return CPI_ENDPOINT;
        }
    }

    /**
     * Luo listan tietyn vuoden kuukausista muotoon ["2024M01", "2024M02", ...].
     */
    private List<String> buildMonthCodes(int year) {
        List<String> months = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            months.add(String.format("%dM%02d", year, m));
        }
        return months;
    }

    /**
     * Rakentaa PxWeb-kyselyn JSON-bodyn (json-stat2).
     */
    private JsonObject buildQuery(List<String> months, String commodityCode) {
        JsonObject root = new JsonObject();
        JsonArray queryArr = new JsonArray();

        // Kuukausi (Kuukausi)
        JsonObject monthObj = new JsonObject();
        monthObj.addProperty("code", "Kuukausi");
        JsonObject monthSel = new JsonObject();
        monthSel.addProperty("filter", "item");
        JsonArray monthValues = new JsonArray();
        for (String m : months) {
            monthValues.add(m);
        }
        monthSel.add("values", monthValues);
        monthObj.add("selection", monthSel);
        queryArr.add(monthObj);

        // Hyödyke
        JsonObject indicatorObj = new JsonObject();
        indicatorObj.addProperty("code", "Hyödyke");
        JsonObject indicatorSel = new JsonObject();
        indicatorSel.addProperty("filter", "item");
        JsonArray indicatorValues = new JsonArray();
        indicatorValues.add(commodityCode);
        indicatorSel.add("values", indicatorValues);
        indicatorObj.add("selection", indicatorSel);
        queryArr.add(indicatorObj);

        root.add("query", queryArr);

        JsonObject response = new JsonObject();
        response.addProperty("format", "json-stat2");
        root.add("response", response);

        return root;
    }

    /**
     * Muuntaa query-olion JSON-merkkijonoksi.
     */
    private String buildRequestBody(JsonObject query) {
        return new Gson().toJson(query);
    }

    /**
     * Tekee HTTP POST -pyynnön PxWeb-rajapintaan ja palauttaa rungon.
     */
    private String postJson(String url, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            throw new IOException("PxWeb API error: HTTP " + response.statusCode()
                    + " body=" + response.body());
        }
        return response.body();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        EconomyController ec = new EconomyController();
        DataCollection dc = ec.fetchEconomyData("2021", "1", "2021", 
                                                            "12", "SSS", "Electricity consumption");
        System.out.println(dc);
    }
}