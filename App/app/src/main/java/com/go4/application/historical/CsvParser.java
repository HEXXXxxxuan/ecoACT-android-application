package com.go4.application.historical;

import android.content.Context;
import android.util.Log;

import com.go4.application.tree.AVLTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    AVLTree<String, AirQualityRecord> avlTree = new AVLTree<>();

    public List<AirQualityRecord> parseCsV(Context context, String fileName){

        //File localFile = new File(context.getFilesDir(), fileName); //localfile
        File localFile = new File(context.getCacheDir(), fileName);   //cache file

        List<AirQualityRecord> records = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(localFile));

            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null){
                Log.d("SearchDebug", "Read line: " + line);
                String[] values = line.split(",");

                String location = values[0];
                long timestamp = Long.parseLong(values[1]);
                double aqi = Double.parseDouble(values[2]);
                double co = Double.parseDouble(values[3]);
                double no2 = Double.parseDouble(values[4]);
                double o3 = Double.parseDouble(values[5]);
                double so2 = Double.parseDouble(values[6]);
                double pm2_5 = Double.parseDouble(values[7]);
                double pm10 = Double.parseDouble(values[8]);
                double nh3 = Double.parseDouble(values[9]);

                records.add(new AirQualityRecord(location, aqi, co, no2, o3, so2, pm2_5, pm10, nh3, timestamp));

            }

            reader.close();

        } catch (IOException e) {
            Log.e("CSVParser", "Error reading file", e);
        }

        return records;

    }

    public AVLTree<String, AirQualityRecord> createAVLTreeFromCsv(Context context, boolean useLocationOnly){
        List<AirQualityRecord> recordList = parseCsV(context, "historical_data.csv");
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


}
