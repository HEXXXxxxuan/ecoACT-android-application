package com.go4.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SuburbJsonUtils {
    /**
     * Loads a list of suburbs from a JSON file in the assets folder.
     *
     * @param context the context used to access assets
     * @param fileName the name of the JSON file
     * @return a list of suburbs as strings
     * @throws RuntimeException if there is an error loading or parsing the JSON
     * @author u7902000 Gea Linggar
     */
    public static List<String> loadSuburbsFromJson(Context context, String fileName) {
        List<String> suburbs = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open(fileName);
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
}
