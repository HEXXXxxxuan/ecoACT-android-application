package com.go4.application.historical;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    public List<Record> parseCsV(Context context, String fileName){
        List<Record> records = new ArrayList<>();

        try {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null){
                String[] values = line.split(",");

                int recordId = Integer.parseInt(values[0]);
                String timestamp = values[1];
                String location = values[2];
                double temperature = Double.parseDouble(values[3]);
                int smokeLevel = Integer.parseInt(values[4]);
                double carbonMonoxide = Double.parseDouble(values[5]);

                records.add(new Record(recordId,timestamp, location, temperature, smokeLevel, carbonMonoxide));

            }

            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return records;

    }


}
