package com.go4.application.live_data;

import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.go4.application.R;
import com.go4.application.historical.AirQualityRecord;
import com.go4.application.historical.CsvParser;
import com.go4.application.historical.SuburbHistoricalActivity;
import com.go4.application.tree.AVLTree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class SuburbLiveActivity extends AppCompatActivity {
    private Spinner suburbSpinnerLive;
    private TextView resultTextViewLive;
    private TextView coTextView;
    private TextView no2TextView;
    private TextView pm25TextView;
    private HashMap<String, double[]> suburbMap;
    private static final String API_KEY = "4f6d63b7d7512fc4b14ee2aeb89d3128";
    private AVLTree<String, AirQualityRecord> records;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_live);
        suburbSpinnerLive = findViewById(R.id.SuburbSpinnerLive);
        resultTextViewLive = findViewById(R.id.resultTextView2);
        coTextView = findViewById(R.id.coTextView);
        no2TextView = findViewById(R.id.no2TextView);
        pm25TextView = findViewById(R.id.pm25TextView);
        lineChart = findViewById(R.id.lineChart);
        selectSuburbSpinner();
    }

    public enum AirQualityMetric {
        AQI, CO, NO2, PM2_5
    }

    private void selectSuburbSpinner() {
        suburbMap = loadSuburbsFromJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbMap.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinnerLive.setAdapter(adapter);

        CsvParser csvParser = new CsvParser();
        records = csvParser.createAVLTreeFromCsv(this, false);

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

                ArrayList<AirQualityRecord> recordsInSelecetedSuburb = new ArrayList<>();

                //traverse the tree
                records.inOrderTraversal(records.getRoot(), (k, r) -> {
                    if(k.startsWith(selectedSuburb)){
                        recordsInSelecetedSuburb.add(r);
                        Log.d("AQI Result", "found " + selectedSuburb);
                    }
                });

                Log.d("AQI Result", recordsInSelecetedSuburb.toString());
                plotData(recordsInSelecetedSuburb, AirQualityMetric.AQI);

                resultTextViewLive.setOnClickListener(v -> plotData(recordsInSelecetedSuburb, AirQualityMetric.AQI));
                coTextView.setOnClickListener(v -> plotData(recordsInSelecetedSuburb, AirQualityMetric.CO));
                no2TextView.setOnClickListener(v -> plotData(recordsInSelecetedSuburb, AirQualityMetric.NO2));
                pm25TextView.setOnClickListener(v -> plotData(recordsInSelecetedSuburb, AirQualityMetric.PM2_5));
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
            String json = new String(buffer, StandardCharsets.UTF_8);

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
            JSONObject main = firstRecord.getJSONObject("main");

            String displayText = String.valueOf(main.getDouble("aqi"));

//            String displayText = "CO: " + components.getDouble("co") + " ppm\n" +
//                    "NO: " + components.getDouble("no") + " ppm\n" +
//                    "NO2: " + components.getDouble("no2") + " ppm\n" +
//                    "O3: " + components.getDouble("o3") + " ppm\n" +
//                    "SO2: " + components.getDouble("so2") + " ppm\n" +
//                    "PM2.5: " + components.getDouble("pm2_5") + " µg/m³\n" +
//                    "PM10: " + components.getDouble("pm10") + " µg/m³\n" +
//                    "NH3: " + components.getDouble("nh3") + " ppm";

            resultTextViewLive.setText(displayText);
            coTextView.setText(String.valueOf(components.getDouble("co")));
            no2TextView.setText(String.valueOf(components.getDouble("no2")));
            pm25TextView.setText(String.valueOf(components.getDouble("pm2_5")));
        } catch (JSONException e) {
            resultTextViewLive.setText("Error parsing JSON response.");
        }
    }

    private void plotData(ArrayList<AirQualityRecord> data, AirQualityMetric metric) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> hourIndex = new ArrayList<>();

        // data points to the entries list
        for (int i = 0; i < data.size(); i++) {
            String formattedTime = data.get(i).getTimestamp().substring(11, 16);

            float value = 0f;
            switch (metric) {
                case AQI:
                    value = Float.parseFloat(String.valueOf(data.get(i).getAqi()));
                    break;
                case CO:
                    value = Float.parseFloat(String.valueOf(data.get(i).getCo()));
                    break;
                case NO2:
                    value = Float.parseFloat(String.valueOf(data.get(i).getNo2()));
                    break;
                case PM2_5:
                    value = Float.parseFloat(String.valueOf(data.get(i).getPm2_5()));
                    break;
            }

            entries.add(new Entry(i, value));
            hourIndex.add(formattedTime);
        }


        LineDataSet dataSet = new LineDataSet(entries, "AQI over Time");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        //customize the appearance
        dataSet.setMode(LineDataSet.Mode.LINEAR); //smooth line
        dataSet.setLineWidth(4f);
        dataSet.setCircleRadius(0f); // Remove circles at data points
        dataSet.setDrawValues(false); // Hide values at the data points
        dataSet.setColor(Color.GREEN); // Line color

        lineChart.getDescription().setEnabled(false);

        // Fill the area under the chart
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.GREEN);
        dataSet.setFillAlpha(40);

        // Create a LineData object with the dataset and set it to the chart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Remove grid lines
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        // Customize X and Y axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(hourIndex));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false); // Disable right Y axis

        lineChart.invalidate();
    }
}
