package com.go4.application.live_data.MockDataStream;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
/**
 * This class is responsible for generating a list of mock JSON responses representing air quality data.
 * used to simulate streaming of air quality data from an external source for testing purposes.
 * The class generates 30 mock records, each containing air quality data for a location.
 * method.
 * @author u7902000 Gea Linggar
 */
public class MockJSONResponse {
    private List<String> mockResponse;
    private int index;
    private Gson gson;

    public MockJSONResponse(){
        this.mockResponse = new ArrayList<>();
        this.index = 0;
        this.gson = new Gson();
        populateMockData();
    }

    /**
     * Populates the list of mock JSON responses with 30 records of air quality data.
     * Each record is serialized into JSON format using the {@code Gson} library.
     * @author Gea Linggar
     */
    private void populateMockData() {
        for(int i = 0; i < 30; i++){
            MockAirQualityRecord.AirQualityData.Components components = new MockAirQualityRecord.AirQualityData.Components(getRandomCO(), getRandomNo2(), getRandomO3(), getRandomSo2(), getRandomPm25(), getRandomPm10());
            MockAirQualityRecord.AirQualityData airQualityData = new MockAirQualityRecord.AirQualityData(getRandomAQI(), components, System.currentTimeMillis() / 1000 + (i * 120));

            MockAirQualityRecord mockAirQualityRecord = new MockAirQualityRecord(149.13, -35.28, airQualityData);

            String jsonResponse = gson.toJson(mockAirQualityRecord);
            mockResponse.add(jsonResponse);
        }
    }

    /**
     * Retrieves the next mock JSON response from the list. The method cycles through
     * the list of mock responses and resets to the first item after the last one is retrieved.
     *
     * @return the next mock JSON response as a {@code String}
     * @author Gea Linggar
     */
    public String getMockResponse(){
        String response = mockResponse.get(index);
        index++;
        if(index > 29) {
            index = 0;
        }
        return response;
    }

    private double getRandomAQI() {
        return Math.round(((Math.random() * 2) + 1) * 100.0) /100.0;  // between 1 and 2
    }

    private double getRandomCO() {
        return Math.round((210 + (Math.random() * 10 - 5)) * 100) / 100.0; // around 210 ± 5
    }

    private double getRandomNo2() {
        return Math.round((2.0 + (Math.random() * 0.2 - 0.1)) * 100.0) / 100.0; // 2.0 ± 0.1
    }

    private double getRandomO3() {
        return Math.round((50.0 + (Math.random() * 5 - 2.5)) * 100.0) / 100.0; // 50.0 ± 2.5
    }

    private double getRandomSo2() {
        return Math.round((0.55 + (Math.random() * 0.1 - 0.05)) * 100.0) / 100.0; // 0.55 ± 0.05
    }

    private double getRandomPm25() {
        return Math.round((1.50 + (Math.random() * 0.2 - 0.1)) * 100)/ 100.0; //1.50 ± 0.1
    }

    private double getRandomPm10() {
        return Math.round((3.0 + (Math.random() * 0.2 - 0.1)) * 100.0) / 100.0; // around 3.0 ± 0.1
    }

}
