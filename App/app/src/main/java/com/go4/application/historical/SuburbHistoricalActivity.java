package com.go4.application.historical;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.go4.application.R;
import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.tree.AVLTree;
import com.go4.utils.CsvParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SuburbHistoricalActivity extends AppCompatActivity {
    private Spinner suburbSpinner;
    private Spinner hourSpinner;
    private EditText editTextDate;
    private AVLTree<String, AirQualityRecord> recordTreeLocationAndDateKey;
    private TextView resultTextView;
    private Button searchButton;

    private EditText searchBar;
    private Button testButton;
    private List<String> suburbList;

    private Button liveDataButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_historical);

        suburbSpinner = findViewById(R.id.suburbSpinner);
        hourSpinner = findViewById(R.id.hourSpinner);
        editTextDate = findViewById(R.id.editTextDate);
        resultTextView = findViewById(R.id.resultTextView);
        searchButton = findViewById(R.id.searchButton);
        liveDataButton = findViewById(R.id.livePageButton);

        editTextDate.setOnClickListener(view -> showDatePickerDialog());
        searchButton.setOnClickListener(v -> searchForRecord());

        //suburb spinner
        suburbSpinner();

        //hour spinner
        hourSpinner();

        searchBar = findViewById(R.id.sh_search);
        testButton = findViewById(R.id.sh_testbutton);
        suburbList = loadSuburbsFromJson();
        testButton.setOnClickListener(v-> parseSearchBarInput());

        liveDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SuburbLiveActivity.class);
            startActivity(intent);
        });

        //parseCSV and insert it in AVLTree
        createAVLTree();

    }

    //parsing to avl tree
    private void createAVLTree() {
        CsvParser csvParser = new CsvParser();
        recordTreeLocationAndDateKey = csvParser.createAVLTree(this, false);
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

    //populating the list (just for test)
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

    //method for select the date
    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();

        String currentSelectedDate = editTextDate.getText().toString();
        String[] dateParts = currentSelectedDate.split("/");
        if (dateParts.length == 3) {
            calendar.set(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]));
        }

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

    private void parseSearchBarInput() {
        Tokenizer tokenizer = new Tokenizer(searchBar.getText().toString(), suburbList);
        Parser parser = new Parser(tokenizer, getApplicationContext());
        parser.parseInput();

        String suburb = parser.getData()[0];
        if (!suburb.isEmpty()) {
            int suburbPosition = suburbList.indexOf(suburb);
            suburbSpinner.setSelection(suburbPosition);
        }

        String date = parser.getData()[1];
        if (!date.isEmpty()) {
            String[] dateParts = date.split("-");
            String selectedDate = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
            editTextDate.setText(selectedDate);
        }

        String time = parser.getData()[2];
        if (!time.isEmpty()) {
            int hourPosition = Integer.parseInt(time.split(":")[0]);
            if (hourPosition >= 0 && hourPosition <= 24) hourSpinner.setSelection(hourPosition);
        }

        searchForRecord();
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

        // Search the AVLTree for the key
        AirQualityRecord record = recordTreeLocationAndDateKey.search(key);

        //if the record is found use this method
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

}