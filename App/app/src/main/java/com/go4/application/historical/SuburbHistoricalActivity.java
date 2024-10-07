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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.go4.application.R;
import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.tree.AVLTree;
import com.go4.utils.CsvParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;

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

    private EditText searchBar;
    private List<String> suburbList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_historical);

        // Initialize views
        suburbSpinner = findViewById(R.id.suburbSpinner);
        hourSpinner = findViewById(R.id.hourSpinner);
        editTextDate = findViewById(R.id.editTextDate);
        searchBar = findViewById(R.id.sh_search);
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


        // Set up suburb and hour spinners
        setupSuburbSpinner();
        setupHourSpinner();

        editTextDate.setOnClickListener(view -> showDatePickerDialog());
        searchButton.setOnClickListener(v -> searchForRecord());

        // Add text watcher for search bar
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                parseSearchBarInput();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        };
        searchBar.addTextChangedListener(textWatcher);

        liveDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SuburbLiveActivity.class);
            startActivity(intent);
        });

        // Parse CSV and insert data into AVLTree
        createAVLTree();
    }

    // Parsing data and inserting into AVLTree
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
        suburbList = loadSuburbsFromJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(adapter);
    }

    // Load suburbs from JSON file
    private List<String> loadSuburbsFromJson() {
        List<String> suburbs = new ArrayList<>();
        // Load your JSON logic here...
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

    // Parse search input from the search bar
    private void parseSearchBarInput() {
        Tokenizer tokenizer = new Tokenizer(searchBar.getText().toString(), suburbList);
        Parser parser = new Parser(tokenizer, getApplicationContext());
        parser.parseInput();

        String suburb = parser.getData()[0];
        String date = parser.getData()[1];
        String time = parser.getData()[2];

        // Update spinners and editText based on the search input
        if (!suburb.isEmpty()) {
            int suburbPosition = suburbList.indexOf(suburb);
            if (suburbPosition != -1) {
                suburbSpinner.setSelection(suburbPosition);
            }
        }

        if (!date.isEmpty()) {
            String[] dateParts = date.split("-");
            String selectedDate = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
            editTextDate.setText(selectedDate);
        }

        if (!time.isEmpty()) {
            int hourPosition = Integer.parseInt(time.split(":")[0]);
            if (hourPosition >= 0 && hourPosition <= 24) {
                hourSpinner.setSelection(hourPosition);
            }
        }
    }

    // Search for the record in AVLTree and update the progress bars
    private void searchForRecord() {
        String selectedDate = editTextDate.getText().toString();
        String selectedSuburb = suburbSpinner.getSelectedItem().toString();
        String selectedHour = hourSpinner.getSelectedItem().toString().substring(0, 2);

        // Modify date format for search
        String[] dateParts = selectedDate.split("/");
        if (dateParts.length == 3) {
            String day = String.format("%02d", Integer.parseInt(dateParts[0]));
            String month = String.format("%02d", Integer.parseInt(dateParts[1]));
            selectedDate = dateParts[2] + "-" + month + "-" + day;
        }

        String key = selectedSuburb + "_" + selectedDate + " " + selectedHour + ":00:00";

        // Search the AVLTree for the key
        AirQualityRecord record = recordTreeLocationAndDateKey.search(key);

        if (record != null) {
            updateProgressBars(record);
        } else {
            resultTextView.setText("No matching records.");
        }
    }

    // Update progress bars based on the found record
    private void updateProgressBars(AirQualityRecord record) {
        semiCircleArcProgressBar.setPercent(record.getAqi());

        // Update progress bars for PM2.5, PM10, O3, etc.
        updateProgressBar(pm25ProgressBar, (int) record.getPm2_5());
        updateProgressBar(pm10ProgressBar, (int) record.getPm10());
        updateProgressBar(o3ProgressBar, (int) record.getO3());
        updateProgressBar(so2ProgressBar, (int) record.getSo2());
        updateProgressBar(coProgressBar, (int) record.getCo());
        updateProgressBar(no2ProgressBar, (int) record.getNo2());

        // Update AQI status text and color
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
    }

    // Helper function to update progress bars based on value
    private void updateProgressBar(ProgressBar progressBar, int value) {
        progressBar.setProgress(value);
        if (value < 50) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.secondaryColorLG));
        } else if (value < 100) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.yellow));
        } else {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
        }
    }
}
