package com.go4.application.historical;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
    private EditText editTextDate;
    //private List<Record> recordList;
    private AVLTree recordTree;
    private TextView resultTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_historical);

        suburbSpinner = findViewById(R.id.suburbSpinner);
        editTextDate = findViewById(R.id.editTextDate);
        resultTextView = findViewById(R.id.resultTextView);

        automaticAddRecords(this, "4f6d63b7d7512fc4b14ee2aeb89d3128", "1727395200", "1727481600", "historical_data.csv");

        editTextDate.setOnClickListener(view -> showDatePickerDialog());

        List<String> suburbList = loadSuburbsFromJson();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(adapter);

        CsvParser csvParser = new CsvParser();


        //Create an AVL Tree
        recordTree = new AVLTree();
        // Parse the CSV and insert records into AVLTree
        List<Record> recordList = csvParser.parseCsV(this, "environment_monitoring_data.csv");
        for (Record record : recordList) {
            String key = record.getLocation() + "_" + record.getDate();
            recordTree.insert(key, record);
        }

        findViewById(R.id.searchButton).setOnClickListener(v -> searchForRecord());

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

        Log.d("SearchDebug", "Selected Date UI: " + selectedDate);

        //search in tree

        String key = selectedSuburb + "_" + selectedDate;  // Generate key for search

        Record record = recordTree.search(key);  // Search the AVLTree for the key
        if (record != null) {
            String result = "Temperature: " + record.getTemperature() + "°C\n" +
                    "Smoke Level: " + record.getSmokeLevel() + " ppm\n" +
                    "Carbon Monoxide: " + record.getCarbonMonoxide() + " ppm";
            resultTextView.setText(result);
        } else {
            resultTextView.setText("No matching records.");
        }

    }

    // This method store historical air quality data to csv
    public void fetchHistoricalDataTOCSV(Context context, String location, String latitude, String longitude, String start, String end, String apiKey, String fileName) {

        // Use ExecutorService to run the network operation in the background
        ExecutorService executorService = Executors.newSingleThreadExecutor();

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

                    File localFile = new File(context.getFilesDir(), fileName);
                    Log.i("FilePath", "Writing to: " + localFile.getAbsolutePath());

                    if (!localFile.exists()) {
                        try (InputStream is = context.getAssets().open(fileName);
                             FileWriter writer = new FileWriter(localFile)) {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                writer.write(line + "\n");
                            }
                            reader.close();
                        } catch (IOException e) {
                            Log.e("AirQualityAPI", "Error copying CSV from assets: ", e);
                        }
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
