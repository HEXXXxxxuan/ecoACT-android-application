package com.go4.application.live_data;

import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.go4.application.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class SuburbLiveActivity extends AppCompatActivity {
    private Spinner suburbSpinnerLive;
    private TextView resultTextViewLive;
    private HashMap<String, double[]> suburbMap;
    private static final String API_KEY = "4f6d63b7d7512fc4b14ee2aeb89d3128";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_live);

        suburbSpinnerLive = findViewById(R.id.SuburbSpinnerLive);
        resultTextViewLive = findViewById(R.id.resultTextView2);

        suburbMap = loadSuburbsFromJson();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbMap.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinnerLive.setAdapter(adapter);


        suburbSpinnerLive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSuburb = parentView.getItemAtPosition(position).toString();
                double[] coordinates = suburbMap.get(selectedSuburb);

                /////////(for testing only!!)
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                // Fetch air quality data using coordinates
                fetchAirQualityData(coordinates[0], coordinates[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing selected
            }
        });
    }

    private void fetchAirQualityData(double latitude, double longitude) {
        String urlString = String.format(
                "https://api.openweathermap.org/data/2.5/air_pollution?lat=%s&lon=%s&appid=%s",
                latitude, longitude, API_KEY);

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                displayAirQualityData(response.toString());
            } else {
                resultTextViewLive.setText("Failed to fetch air quality data.");
            }
        } catch (Exception e) {
            resultTextViewLive.setText("Error fetching data: " + e.getMessage());
        }
    }

    private HashMap<String, double[]> loadSuburbsFromJson() {
        HashMap<String, double[]> suburbMap = new HashMap<>();
        try {
            InputStream is = getAssets().open("canberra_suburbs_coordinates.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);

            // Iterate through suburb names
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String suburb = keys.next();
                JSONArray coordinates = jsonObject.getJSONArray(suburb);
                double latitude = coordinates.getDouble(0);
                double longitude = coordinates.getDouble(1);
                suburbMap.put(suburb, new double[]{latitude, longitude});
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        return suburbMap;
    }

    private void displayAirQualityData(String jsonResponse) {
        try {
            JSONObject responseObj = new JSONObject(jsonResponse);
            JSONArray listArray = responseObj.getJSONArray("list");
            JSONObject firstRecord = listArray.getJSONObject(0);
            JSONObject components = firstRecord.getJSONObject("components");

            String displayText = "CO: " + components.getDouble("co") + " ppm\n" +
                    "NO: " + components.getDouble("no") + " ppm\n" +
                    "NO2: " + components.getDouble("no2") + " ppm\n" +
                    "O3: " + components.getDouble("o3") + " ppm\n" +
                    "SO2: " + components.getDouble("so2") + " ppm\n" +
                    "PM2.5: " + components.getDouble("pm2_5") + " µg/m³\n" +
                    "PM10: " + components.getDouble("pm10") + " µg/m³\n" +
                    "NH3: " + components.getDouble("nh3") + " ppm";

            resultTextViewLive.setText(displayText);
        } catch (JSONException e) {
            resultTextViewLive.setText("Error parsing JSON response.");
        }
    }
}
