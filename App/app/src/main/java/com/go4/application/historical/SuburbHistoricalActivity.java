package com.go4.application.historical;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.go4.application.R;
import com.go4.application.tree.AVLTree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuburbHistoricalActivity extends AppCompatActivity {
    private Spinner suburbSpinner;
    private Spinner hourSpinner;
    private EditText editTextDate;
    //private List<Record> recordList;
    private AVLTree<String, AirQualityRecord> recordTree;
    private TextView resultTextView;
    private Button searchButton;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_historical);

        suburbSpinner = findViewById(R.id.suburbSpinner);
        hourSpinner = findViewById(R.id.hourSpinner);
        editTextDate = findViewById(R.id.editTextDate);
        resultTextView = findViewById(R.id.resultTextView);
        searchButton = findViewById(R.id.searchButton);

        editTextDate.setOnClickListener(view -> showDatePickerDialog());
        searchButton.setOnClickListener(v -> searchForRecord());

        //automatically create dataset of latest 5 days record for each suburb and store it in internal folder
        //we can move this to other layout, ideally first time user open the app, it will automatically generated this historical file
        long currentTime = System.currentTimeMillis() / 1000L; //current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_YEAR, -5);
        long startingTime = calendar.getTimeInMillis() / 1000L;
        automaticAddRecords(this, "4f6d63b7d7512fc4b14ee2aeb89d3128", String.valueOf(startingTime), String.valueOf(currentTime), "historical_data.csv");

        //suburb spinner
        suburbSpinner();

        //hour spinner
        hourSpinner();

        //parseCSV and insert it in AVLTree
        createAVLTree();

    }

    private void createAVLTree() {
        CsvParser csvParser = new CsvParser();
        recordTree = new AVLTree<>();
        // Parse the CSV and insert records into AVLTree
        List<AirQualityRecord> recordList = csvParser.parseCsV(this, "historical_data.csv");
        for (AirQualityRecord record : recordList) {
            String key = record.getLocation() + "_" + record.getTimestamp();
            recordTree.insert(key, record);
        }
    }

    private void hourSpinner() {
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d:00", i)); // Add hours in format 00:00, 01:00, etc.
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);
    }

    private void suburbSpinner() {
        List<String> suburbList = loadSuburbsFromJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(adapter);
    }

    private List<String> loadSuburbsFromJson(){
        List<String> suburbs = new ArrayList<>();
        try {
            InputStream is = getAssets().open("canberra_suburbs.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            // Parse JSON array
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                suburbs.add(jsonArray.getString(i));
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        return suburbs;

    }

    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editTextDate.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void searchForRecord() {
        String selectedDate = editTextDate.getText().toString();
        String selectedSuburb = suburbSpinner.getSelectedItem().toString();
        String selectedHour = hourSpinner.getSelectedItem().toString().substring(0, 2);


        Log.d("SearchDebug", "Selected Suburb: " + selectedSuburb);

        String[] dateParts = selectedDate.split("/");
        if (dateParts.length == 3) {
            String day = String.format("%02d", Integer.parseInt(dateParts[0]));
            String month = String.format("%02d", Integer.parseInt(dateParts[1]));
            selectedDate = dateParts[2] + "-" + month + "-" + day;
        }

        if (selectedDate.isEmpty()) {
            resultTextView.setText("Please select a date");
            return;
        }

        selectedDate += " " + selectedHour + ":00:00";

        Log.d("SearchDebug", "Selected Date UI: " + selectedDate);

        //search in tree

        String key = selectedSuburb + "_" + selectedDate;  // Generate key for search

        AirQualityRecord record = recordTree.search(key);  // Search the AVLTree for the key
        if (record != null) {
            String result = "Air Quality Index (AQI): " + record.getAqi() + "\n" +
                    "Carbon Monoxide (CO): " + record.getCo() + " ppm\n" +
                    "Nitrogen Dioxide (NO2): " + record.getNo2() + " ppm\n" +
                    "Ozone (O3): " + record.getO3() + " ppm\n" +
                    "Sulfur Dioxide (SO2): " + record.getSo2() + " ppm\n" +
                    "PM2.5: " + record.getPm2_5() + " µg/m³\n" +
                    "PM10: " + record.getPm10() + " µg/m³\n" +
                    "Ammonia (NH3): " + record.getNh3() + " ppm";
            resultTextView.setText(result);
        } else {
            resultTextView.setText("No matching records.");
        }

    }

    // This method store historical air quality data to csv
    public void fetchHistoricalDataTOCSV(Context context, String location, String latitude, String longitude, String start, String end, String apiKey, String fileName) {

        executorService.submit(() -> {
            String urlString = String.format(
                    "https://api.openweathermap.org/data/2.5/air_pollution/history?lat=%s&lon=%s&start=%s&end=%s&appid=%s",
                    latitude, longitude, start, end, apiKey);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray list = jsonResponse.getJSONArray("list");

                    //store at local file
                    //File localFile = new File(context.getFilesDir(), fileName);
                    //Log.i("FilePath", "Writing to: " + localFile.getAbsolutePath());

                    // Store data in cache directory
                    File localFile = new File(context.getCacheDir(), fileName);
                    Log.i("FilePath", "Writing to: " + localFile.getAbsolutePath());

                    if (!localFile.exists()) {
                        localFile.createNewFile();
                    }

                    // Append data to the CSV file in internal storage
                    try (FileWriter writer = new FileWriter(localFile, true)) {
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject record = list.getJSONObject(i);
                            JSONObject main = record.getJSONObject("main");
                            JSONObject components = record.getJSONObject("components");

                            writer.append(location).append(",");
                            writer.append(record.getLong("dt") + ",");
                            writer.append(main.getInt("aqi") + ",");
                            writer.append(components.getDouble("co") + ",");
                            writer.append(components.getDouble("no2") + ",");
                            writer.append(components.getDouble("o3") + ",");
                            writer.append(components.getDouble("so2") + ",");
                            writer.append(components.getDouble("pm2_5") + ",");
                            writer.append(components.getDouble("pm10") + ",");
                            writer.append(components.getDouble("nh3") + "\n");
                        }
                        Log.i("AirQualityAPI", "Data appended to internal storage file: " + localFile.getPath());

                        mainHandler.post(() -> {
                            Toast.makeText(context, "Fetching data...", Toast.LENGTH_SHORT).show();
                        });

                    } catch (IOException e) {
                        Log.e("AirQualityAPI", "Error writing to CSV: ", e);
                    }
                } else {
                    Log.e("AirQualityAPI", "GET request failed: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("AirQualityAPI", "Error during API call: ", e);
            }
        });
    }


    //methods to fetch data for each location for a given period
    public static void automaticAddRecords(Context context, String apiKey, String startDate, String endDate, String fileName) {
        try {
            InputStream is = context.getAssets().open("canberra_suburbs_coordinates.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONObject locationsObject = new JSONObject(jsonString.toString());

            Iterator<String> keys = locationsObject.keys();

            while (keys.hasNext()) {
                String location = keys.next();

                JSONArray coordinates = locationsObject.getJSONArray(location);
                String latitude = String.valueOf(coordinates.getDouble(0));
                String longitude = String.valueOf(coordinates.getDouble(1));

                SuburbHistoricalActivity activity = new SuburbHistoricalActivity();
                activity.fetchHistoricalDataTOCSV(context, location, latitude, longitude, startDate, endDate, apiKey, fileName);

                Log.i("LocationProcessing", "Processed location: " + location);
            }
        } catch (Exception e) {
            Log.e("LocationProcessing", "Error processing JSON file", e);
        }
    }
}