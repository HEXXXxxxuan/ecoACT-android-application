package com.go4.application.historical;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.go4.application.R;
import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.tree.AVLTree;
import com.go4.utils.CsvParser;
import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;

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
    private Button liveDataButton;
    private SemiCircleArcProgressBar semiCircleArcProgressBar;
    private ProgressBar pm25ProgressBar, pm10ProgressBar, o3ProgressBar, so2ProgressBar, coProgressBar, no2ProgressBar;
    private TextView aqiStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_historical);

        // Initialize views
        suburbSpinner = findViewById(R.id.suburbSpinner);
        hourSpinner = findViewById(R.id.hourSpinner);
        editTextDate = findViewById(R.id.editTextDate);
        searchButton = findViewById(R.id.searchButton);
        liveDataButton = findViewById(R.id.livePageButton);

        aqiStatusTextView = findViewById(R.id.aqiStatusTextView);
        semiCircleArcProgressBar = findViewById(R.id.semiCircleArcProgressBar);
        pm25ProgressBar = findViewById(R.id.pm25ProgressBar);
        pm10ProgressBar = findViewById(R.id.pm10ProgressBar);
        o3ProgressBar = findViewById(R.id.o3ProgressBar);
        so2ProgressBar = findViewById(R.id.so2ProgressBar);
        coProgressBar = findViewById(R.id.coProgressBar);
        no2ProgressBar = findViewById(R.id.no2ProgressBar);

        editTextDate.setOnClickListener(view -> showDatePickerDialog());
        searchButton.setOnClickListener(v -> searchForRecord());

        // Set up suburb and hour spinners
        setupSuburbSpinner();
        setupHourSpinner();

        liveDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SuburbLiveActivity.class);
            startActivity(intent);
        });

        // Parse CSV and insert data into AVLTree
        createAVLTree();
    }

    // Parse data and insert it into AVLTree
    private void createAVLTree() {
        CsvParser csvParser = new CsvParser();
        recordTreeLocationAndDateKey = csvParser.createAVLTree(this, false);
    }

    // Set up hour spinner
    private void setupHourSpinner() {
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d:00", i)); // Add hours in format 00:00, 01:00, etc.
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);
    }

    // Set up suburb spinner
    private void setupSuburbSpinner() {
        List<String> suburbList = loadSuburbsFromJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(adapter);
    }

    // Load suburbs from JSON file
    private List<String> loadSuburbsFromJson() {
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

    // Show date picker dialog
    private void showDatePickerDialog() {
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

    // Search for the record in AVLTree
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

        // Search in AVLTree
        String key = selectedSuburb + "_" + selectedDate;
        AirQualityRecord record = recordTreeLocationAndDateKey.search(key);

        // If the record is found, update the progress bars and display other data
        if (record != null) {
            // Set progress dynamically based on database data
            pm25ProgressBar.setProgress((int) record.getPm2_5());
            pm10ProgressBar.setProgress((int) record.getPm10());
            o3ProgressBar.setProgress((int) record.getO3());
            so2ProgressBar.setProgress((int) record.getSo2());
            coProgressBar.setProgress((int) record.getCo());
            no2ProgressBar.setProgress((int) record.getNo2());
            semiCircleArcProgressBar.setPercent(record.getAqi());

            // Set AQI status and emoji based on AQI value

            int aqi = record.getAqi();
            if (aqi < 50) {
                aqiStatusTextView.setText(aqi + " AQI 🙂 Low");
                aqiStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.secondaryColorLG));
                semiCircleArcProgressBar.setProgressBarColor(ContextCompat.getColor(this, R.color.secondaryColorLG));
            } else if (aqi < 100) {
                aqiStatusTextView.setText(aqi + " AQI 😐 Moderate");
                aqiStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                semiCircleArcProgressBar.setProgressBarColor(ContextCompat.getColor(this, R.color.yellow));
            } else {
                aqiStatusTextView.setText(aqi + " AQI 😷 High");
                aqiStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.red));
                semiCircleArcProgressBar.setProgressBarColor(ContextCompat.getColor(this, R.color.red));
            }

 //PM2.5
          int pm25 = (int) record.getPm2_5();
            pm25ProgressBar.setProgress(pm25);
            if (pm25 < 50) {
                pm25ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.secondaryColorLG));
            } else if (pm25 < 100) {
                pm25ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            } else {
                pm25ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
            }

            // PM10
            int pm10 = (int) Math.round(record.getPm10());
            pm10ProgressBar.setProgress(pm10);
            if (pm10 < 50) {
                pm10ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.secondaryColorLG));
            } else if (pm10 < 100) {
                pm10ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            } else {
                pm10ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
            }

// O3
            int o3 = (int) record.getO3();
            o3ProgressBar.setProgress(o3);
            if (o3 < 50) {
                o3ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.secondaryColorLG));
            } else if (o3 < 100) {
                o3ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            } else {
                o3ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
            }

// SO2
            int so2 = (int) record.getSo2();
            so2ProgressBar.setProgress(so2);
            if (so2 < 50) {
                so2ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.secondaryColorLG));
            } else if (so2 < 100) {
                so2ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            } else {
                so2ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
            }

// CO
            int co = (int) record.getCo();
            coProgressBar.setProgress(co);
            if (co < 50) {
                coProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.secondaryColorLG));
            } else if (co < 100) {
                coProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            } else {
                coProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
            }

// NO2
            int no2 = (int) record.getNo2();
            no2ProgressBar.setProgress(no2);
            if (no2 < 50) {
                no2ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.secondaryColorLG));
            } else if (no2 < 100) {
                no2ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            } else {
                no2ProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
            }


          // Display other data in TextView
            String result = "PM2.5: " + record.getPm2_5() + " μg/m³\n" +
                    "PM10: " + record.getPm10() + " μg/m³\n" +
                    "O3: " + record.getO3() + " μg/m³\n" +
                    "SO2: " + record.getSo2() + " μg/m³\n" +
                    "CO: " + record.getCo() + " ppm\n" +
                    "NO2: " + record.getNo2() + " ppm";
            resultTextView.setText(result);
        } else {
            resultTextView.setText("No matching records.");

      }
    }
}
