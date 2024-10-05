package com.go4.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class DataFetcher {
    private ExecutorService executorService;
    private Handler mainHandler;
    private String apiKey;


    long currentTime = System.currentTimeMillis() / 1000L;
    long startingTime;

    public DataFetcher(ExecutorService executorService, Handler mainHandler, int number_OfDays, String apiKey) {
        this.executorService = executorService;
        this.mainHandler = mainHandler;
        this.apiKey = apiKey;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_YEAR, -number_OfDays);
        startingTime = calendar.getTimeInMillis() / 1000L;
    }



    public void automaticAddRecords(Context context, String fileName, ProgressBar fetchingBar, Runnable onComplete) {
        String startDate = String.valueOf(startingTime);
        String endDate = String.valueOf(currentTime);

        try {
            //reads existing assets to extract coordinate for each suburbs
            InputStream is = context.getAssets().open("canberra_suburbs_coordinates.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONObject locationsObject = new JSONObject(jsonString.toString());
            //extracting each suburb as a key
            Iterator<String> keys = locationsObject.keys();

            //track the number of completed suburb
            int totalLocations = locationsObject.length();
            final int[] completedLocations = {0};

            //iterating through each suburbs
            while (keys.hasNext()) {
                String location = keys.next();

                JSONArray coordinates = locationsObject.getJSONArray(location);
                String latitude = String.valueOf(coordinates.getDouble(0));
                String longitude = String.valueOf(coordinates.getDouble(1));

                //API call for each suburbs and fetch the response to CSV
                fetchHistoricalDataTOCSV(context, location, latitude, longitude, startDate, endDate, apiKey, fileName, () -> {
                    //check the number of completed extraction
                    synchronized (completedLocations) {
                        completedLocations[0]++;

                        //update loading bar
                        mainHandler.post(() -> fetchingBar.setProgress(completedLocations[0]));

                        if (completedLocations[0] == totalLocations) {

                            // runnable if ALL suburb has completely processed
                            onComplete.run();
                        }
                    }
                });

                Log.i("LocationProcessing", "Processed location: " + location);
            }
        } catch (Exception e) {
            Log.e("LocationProcessing", "Error processing JSON file", e);
        }
    }

    public void fetchHistoricalDataTOCSV(Context context, String location, String latitude, String longitude, String start, String end, String apiKey, String fileName, Runnable onComplete) {

        executorService.submit(() -> {
            //API call for each suburb
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


                    // checking for the same record (if the csv file already stored before)
                    Set<String> existingRecords = new HashSet<>();

                    //reading existing csv and populate existingRecords
                    if (localFile.exists()) {
                        BufferedReader reader = new BufferedReader(new FileReader(localFile));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] columns = line.split(",");
                            if (columns.length > 1) {
                                String existingLocation = columns[0];
                                String existingTimestamp = columns[1];
                                existingRecords.add(existingLocation + "_" + existingTimestamp);
                            }
                        }
                        reader.close();
                    } else {
                        localFile.createNewFile();  // Create the file if it doesn't exist
                    }

                    // Append data to the CSV file in internal storage
                    try (FileWriter writer = new FileWriter(localFile, true)) {
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject record = list.getJSONObject(i);

                            long timestamp = record.getLong("dt");
                            String timestampStr = String.valueOf(timestamp);

                            // Suburb-timestamp key
                            String recordKey = location + "_" + timestampStr;

                            // Check if record already exists, only append new records
                            if (!existingRecords.contains(recordKey)) {
                                JSONObject main = record.getJSONObject("main");
                                JSONObject components = record.getJSONObject("components");

                                // Write the new data to CSV
                                writer.append(location).append(",");
                                writer.append(timestampStr).append(",");
                                writer.append(main.getDouble("aqi") + ",");
                                writer.append(components.getDouble("co") + ",");
                                writer.append(components.getDouble("no2") + ",");
                                writer.append(components.getDouble("o3") + ",");
                                writer.append(components.getDouble("so2") + ",");
                                writer.append(components.getDouble("pm2_5") + ",");
                                writer.append(components.getDouble("pm10") + ",");
                                writer.append(components.getDouble("nh3") + "\n");

                                // Add the record to the Set
                                existingRecords.add(recordKey);
                            }
                        }
                        Log.i("AirQualityAPI", "Data appended to internal storage file: " + localFile.getPath());

                        // Call the callback after completion
                        if (onComplete != null) {

                            //runnable after each suburb processed
                            onComplete.run();
                        }

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

}
