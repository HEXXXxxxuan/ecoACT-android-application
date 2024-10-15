package com.go4.utils;

import android.content.Context;
import android.util.Log;

import com.go4.application.model.AirQualityRecord;
import com.go4.utils.tree.AVLTree;
import com.go4.utils.design_pattern.DataAccessObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to parse CSV files containing air quality data
 * and store the parsed data into an AVL tree structure.
 *
 * @author u7902000 Gea Linggar
 */
public class CsvParser implements DataAccessObject {
    AVLTree<String, AirQualityRecord> avlTree = new AVLTree<>();

    /**
     * Parses a CSV file containing air quality data and returns a list of {@link AirQualityRecord} objects.
     * <p>
     * The method expects the CSV file to have 10 columns with specific values representing location,
     * timestamp, and various air quality metrics (AQI, CO, NO2, etc.). It skips rows with an invalid number of columns
     * or rows containing non-numeric data where numeric values are expected.
     * </p>
     *
     * @param context  used to access the file
     * @param fileName the name of the CSV file to parse
     * @return a list of parsed {@link AirQualityRecord} objects
     * @author u7902000 Gea Linggar
     */
    public List<AirQualityRecord> parseData(Context context, String fileName){

        //File localFile = new File(context.getFilesDir(), fileName); //localfile
        File localFile = new File(context.getCacheDir(), fileName);   //cache file

        List<AirQualityRecord> records = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(localFile));

            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null){
                String[] values = line.split(",");

                if (values.length != 10) {
                    Log.e("CSVParser", "Invalid number of columns in line: " + line);
                    continue; // Skip incorrectly formatted row
                }

                String location = values[0];

                // Check if the location string valid
                if (!location.matches("[a-zA-Z' ]+")) {
                    Log.e("CSVParser", "Invalid location (non-alphabetic characters): " + location);
                    continue;
                }

                long timestamp;
                double aqi, co, no2, o3, so2, pm2_5, pm10, nh3;


                try {
                    timestamp = parseLongSafely(values[1], 0L);
                    aqi = parseDoubleSafely(values[2], 0.0);
                    co = parseDoubleSafely(values[3], 0.0);
                    no2 = parseDoubleSafely(values[4], 0.0);
                    o3 = parseDoubleSafely(values[5], 0.0);
                    so2 = parseDoubleSafely(values[6], 0.0);
                    pm2_5 = parseDoubleSafely(values[7], 0.0);
                    pm10 = parseDoubleSafely(values[8], 0.0);
                    nh3 = parseDoubleSafely(values[9], 0.0);
                } catch (NumberFormatException e) {
                    Log.e("CSVParser", "Error parsing numeric values in line: " + line, e);
                    continue; // Skip this record if there’s an error
                }

                records.add(new AirQualityRecord(location, (int)aqi, co, no2, o3, so2, pm2_5, pm10, nh3, timestamp));
            }

            reader.close();

        } catch (IOException e) {
            Log.e("CSVParser", "Error reading file", e);
        }

        return records;

    }

    /**
     * Creates an AVL tree from the air quality records parsed from a CSV file.
     * Allows the caller to choose whether to use only the location as the key or to
     * use a combination of location and timestamp as the key.
     *
     * @param context         used to access the file
     * @param useLocationOnly if {@code true}, only the location is used as the key for the AVL tree;
     *                        otherwise, both location and timestamp are used as the key
     * @return the populated AVL tree containing the parsed air quality records
     * @author u7902000 Gea Linggar
     */
    public AVLTree<String, AirQualityRecord> createAVLTree(Context context, boolean useLocationOnly){
        List<AirQualityRecord> recordList = parseData(context, "historical_data.csv");
        for (AirQualityRecord record : recordList) {
            String key;
            if (useLocationOnly) {
                key = record.getLocation();  // Suburb only as the key
            } else {
                key = record.getLocation() + "_" + record.getTimestamp();  // Suburb + timestamp as the key
            }

            avlTree.insert(key, record);  // Insert into AVL tree
        }
        return avlTree;
    }

    // Helper method to safely parse long values
    public long parseLongSafely(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            Log.e("CSVParser", "Invalid long value: " + value);
            return defaultValue;
        }
    }

    // Helper method to safely parse double values
    public double parseDoubleSafely(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Log.e("CSVParser", "Invalid double value: " + value);
            return defaultValue;
        }
    }


}
