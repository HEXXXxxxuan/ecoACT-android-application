package com.go4.application.historical;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.go4.application.R;
import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.model.AirQualityRecord;
import com.go4.utils.tree.AVLTree;
import com.go4.utils.CsvParser;
import com.go4.utils.tokenizer_parser.Parser;
import com.go4.utils.tokenizer_parser.Tokenizer;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;

public class SuburbHistoricalActivity extends AppCompatActivity {
    private Spinner suburbSpinner;
    private Spinner hourSpinner;
    private EditText editTextDate;
    private AVLTree<String, AirQualityRecord> recordTreeLocationAndDateKey;

    private Button searchButton;

    private EditText searchBar;
    private List<String> suburbList;

    private Button liveDataButton;
    private SemiCircleArcProgressBar semiCircleArcProgressBar;
    private ProgressBar pm25ProgressBar, pm10ProgressBar, o3ProgressBar, so2ProgressBar, coProgressBar, no2ProgressBar;
    private TextView aqiStatusTextView;

    private void initializeViews() {
        suburbSpinner = findViewById(R.id.suburbSpinner);
        hourSpinner = findViewById(R.id.hourSpinner);
        editTextDate = findViewById(R.id.editTextDate);
        searchButton = findViewById(R.id.searchButton);
        liveDataButton = findViewById(R.id.livePageButton);
        searchBar = findViewById(R.id.sh_search);
        semiCircleArcProgressBar = findViewById(R.id.semiCircleArcProgressBar);
        pm25ProgressBar = findViewById(R.id.pm25ProgressBar);
        pm10ProgressBar = findViewById(R.id.pm10ProgressBar);
        o3ProgressBar = findViewById(R.id.o3ProgressBar);
        so2ProgressBar = findViewById(R.id.so2ProgressBar);
        coProgressBar = findViewById(R.id.coProgressBar);
        no2ProgressBar = findViewById(R.id.no2ProgressBar);
        aqiStatusTextView = findViewById(R.id.aqiStatusTextView);
        // Lower tha max scale because air quality in Canberra so good
        coProgressBar.setMax(1000);
        o3ProgressBar.setMax(200);
        pm10ProgressBar.setMax(20);
        pm25ProgressBar.setMax(20);
        no2ProgressBar.setMax(5);
        so2ProgressBar.setMax(10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_historical);

        initializeViews();

        editTextDate.setOnClickListener(view -> showDatePickerDialog());
        searchButton.setOnClickListener(v -> searchForRecord());

        // Populate spinners
        suburbSpinner();
        hourSpinner();

        searchBar = findViewById(R.id.sh_search);
        suburbList = loadSuburbsFromJson();

        // Add listener for search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                parseSearchBarInput();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        liveDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SuburbLiveActivity.class);
            startActivity(intent);
        });

        // Parse CSV and insert data into AVL tree
        createAVLTree();
    }

    // Parse data and create AVL tree
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

    // Populate the suburb list from JSON file
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

    // Date picker dialog
    private void showDatePickerDialog() {
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

    /**
     * The parseSearchBarInput() function is used to parse the user's search bar input.
     * @author u8003980 Chan Cheng Leong
     */
    private void parseSearchBarInput() {
        Tokenizer tokenizer = new Tokenizer(searchBar.getText().toString(), suburbList);
        Parser parser = new Parser(tokenizer, getApplicationContext());
        parser.parseInput();

        String suburb = parser.getData()[0];
        if (!suburb.isEmpty()) {
            int suburbPosition = suburbList.indexOf(suburb);
            if (suburbPosition != -1) {
                Log.d("SearchDebug", "Parsed Suburb: " + suburb);
                suburbSpinner.setSelection(suburbPosition);
            } else {
                Log.d("SearchDebug", "Suburb not found: " + suburb);
            }
        }

        String date = parser.getData()[1];
        if (!date.isEmpty()) {
            String[] dateParts = date.split("-");
            String selectedDate = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
            editTextDate.setText(selectedDate);
            Log.d("SearchDebug", "Parsed Date: " + selectedDate);
        }

        String time = parser.getData()[2];
        if (!time.isEmpty()) {
            int hourPosition = Integer.parseInt(time.split(":")[0]);
            if (hourPosition >= 0 && hourPosition <= 24) {
                hourSpinner.setSelection(hourPosition);
                Log.d("SearchDebug", "Parsed Time: " + time);
            } else {
                Log.d("SearchDebug", "Invalid Time: " + time);
            }
        }
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
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedDate += " " + selectedHour + ":00:00";

        Log.d("SearchDebug", "Selected Date UI: " + selectedDate);

        // Search in AVL tree
        String key = selectedSuburb + "_" + selectedDate;

        AirQualityRecord record = recordTreeLocationAndDateKey.search(key);

        if (record != null) {
            // Update progress bars and their colors in one method call
            updateProgressBar(pm25ProgressBar, "PM2.5", record.getPm2_5());
            updateProgressBar(pm10ProgressBar, "PM10", record.getPm10());
            updateProgressBar(o3ProgressBar, "O3", record.getO3());
            updateProgressBar(so2ProgressBar, "SO2", record.getSo2());
            updateProgressBar(coProgressBar, "CO", record.getCo());
            updateProgressBar(no2ProgressBar, "NO2", record.getNo2());

            // Update semiCircleArcProgressBar and AQI status

            int aqi = (int) Math.round(record.getAqi());

            // Ensure AQI is an integer
            semiCircleArcProgressBar.setPercent(aqi);
            if (aqi < 50) {
                aqiStatusTextView.setText(aqi + " AQI 🙂 Low");
                aqiStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.secondaryColorLG));  // Green
                semiCircleArcProgressBar.setProgressBarColor(ContextCompat.getColor(this, R.color.secondaryColorLG));  // Green
            } else if (aqi < 100) {
                aqiStatusTextView.setText(aqi + " AQI 😐 Moderate");
                aqiStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.yellow));  // Yellow
                semiCircleArcProgressBar.setProgressBarColor(ContextCompat.getColor(this, R.color.yellow));  // Yellow
            } else {
                aqiStatusTextView.setText(aqi + " AQI 😷 High");
                aqiStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.red));  // Red
                semiCircleArcProgressBar.setProgressBarColor(ContextCompat.getColor(this, R.color.red));  // Red
            }
        } else {
            Toast.makeText(this, "No matching records.", Toast.LENGTH_SHORT).show();
        }
    }

    // Unified method to update progress bars and their colors
    private void updateProgressBar(ProgressBar progressBar, String type, double value) {
        int colorResId;

        int scaledValue = (int) value;  // Default is no scaling

        switch (type) {
            case "PM2.5":
                scaledValue = (int) (value * 10);  // Scale PM2.5 by 10
                if (value <= 1.0) {
                    colorResId = R.color.secondaryColorLG;
                } else if (value <= 2.5) {
                    colorResId = R.color.yellow;
                } else {
                    colorResId = R.color.red;
                }
                break;

            case "PM10":
                scaledValue = (int) (value * 10);  // Scale PM10 by 10
                if (value <= 2.0) {
                    colorResId = R.color.secondaryColorLG;
                } else if (value <= 5.0) {
                    colorResId = R.color.yellow;
                } else {
                    colorResId = R.color.red;
                }
                break;

            case "SO2":
                scaledValue = (int) (value * 10);  // Scale SO2 by 10
                if (value <= 2.0) {
                    colorResId = R.color.secondaryColorLG;
                } else if (value <= 8.0) {
                    colorResId = R.color.yellow;
                } else {
                    colorResId = R.color.red;
                }
                break;

            case "O3":
                if (value <= 60) {
                    colorResId = R.color.secondaryColorLG;
                } else if (value <= 100) {
                    colorResId = R.color.yellow;
                } else {
                    colorResId = R.color.red;
                }
                break;


            case "CO":
                if (value <= 4700) {
                    colorResId = R.color.secondaryColorLG;
                } else if (value <= 9400) {
                    colorResId = R.color.yellow;
                } else {
                    colorResId = R.color.red;
                }
                break;

            case "NO2":
                if (value <= 40) {
                    colorResId = R.color.secondaryColorLG;
                } else if (value <= 70) {
                    colorResId = R.color.yellow;
                } else {
                    colorResId = R.color.red;
                }
                break;

            default:
                colorResId = R.color.secondaryColorLG;  // Default to green if no match
                break;
        }

        progressBar.setProgress(scaledValue);
        progressBar.getProgressDrawable().setTint(ContextCompat.getColor(this, colorResId));
    }
}
