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

import com.go4.application.MainActivity;
import com.go4.application.R;
import com.go4.application.SplashActivity;
import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.model.AirQualityRecord;
import com.go4.utils.SuburbJsonUtils;
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

/**
 * This activity class allows users to view historical air quality data for selected suburbs.
 * It provides the ability to select a suburb, a date, and an hour to search for air quality records
 * stored in an users cache file.
 * <p>
 * The air quality metrics are displayed via progress bars and other visual indicators, such as
 * a semi-circular progress bar for the Air Quality Index (AQI).Users can input air quality data
 * by selecting suburbs and specific dates using a spinner or by typing in the search bar,
 * which has a built-in grammar function.
 * </p>
 * <p>
 * The activity features:
 * <ul>
 *   <li>Spinners for selecting suburbs and hours</li>
 *   <li>A date picker dialog for selecting dates</li>
 *   <li>Search functionality that parses user input and updates the spinners</li>
 *   <li>Displaying air quality metrics through progress bars</li>
 * </ul>
 * </p>
 *
 *
 * @author u7902000 Gea Linggar, u8003980 Chan Cheng Leong, u8006862 Hexuan(Shawn)
 */
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
    private TextView pm25TextView, pm10TextView, o3TextView, so2TextView, coTextView, no2TextView;

    private String email;

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
        pm25TextView = findViewById(R.id.pm25TextView);
        pm10TextView = findViewById(R.id.pm10TextView);
        o3TextView = findViewById(R.id.o3TextView);
        so2TextView = findViewById(R.id.so2TextView);
        coTextView = findViewById(R.id.coTextView);
        no2TextView = findViewById(R.id.no2TextView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        email = intent.getStringExtra("displayName");

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
            Intent suburbLiveIntent = new Intent(getApplicationContext(), SuburbLiveActivity.class);
            suburbLiveIntent.putExtra("displayName", email);
            startActivity(suburbLiveIntent);
        });

        // Parse CSV and insert data into AVL tree
        createAVLTree();
    }

    /**
     * Initializes the {@code recordTreeLocationAndDateKey} by parsing a CSV file
     * and creating an AVL tree from the parsed data.
     * @author u7902000 Gea Linggar
     */
    private void createAVLTree() {
        CsvParser csvParser = new CsvParser();
        recordTreeLocationAndDateKey = csvParser.createAVLTree(this, false);
    }

    /**
     * Initializes the hour spinner with a list of hours in the format "HH:00" (e.g., 00:00, 01:00, etc.).
     * @author u7902000 Gea linggar
     */
    private void hourSpinner() {
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d:00", i)); // Add hours in format 00:00, 01:00, etc.
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);
    }


    /**
     * Initializes the suburb spinner by loading the list of suburbs from a {@code loadSuburbsFromJson()} and populating
     * the spinner with this list.
     * @author u7902000 Gea Linggar Galih
     */
    private void suburbSpinner() {
        List<String> suburbList = loadSuburbsFromJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(adapter);
    }

    /**
     * Loads a list of suburbs from the "canberra_suburbs.json" file in the assets folder.
     * <p>
     * This method delegates the actual loading process to the {@link SuburbJsonUtils}
     * which handles the JSON file parsing and returns a list of suburb names.
     * </p>
     *
     * @return a list of suburb names parsed from the "canberra_suburbs.json" file.
     * @throws RuntimeException if there is an error loading or parsing the JSON file.
     * @author u7902000 Gea Linggar
     */
    private List<String> loadSuburbsFromJson() {
        return SuburbJsonUtils.loadSuburbsFromJson(this, "canberra_suburbs.json");
    }

    /**
     * Called to displays a date picker dialog that allows the user to select a date.
     * <p>This method makes use of Android's {@link DatePickerDialog} to provide a
     * user-friendly way of selecting dates.</p>
     *
     * @author u7902000 Gea Linggar
     */
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
     * Called to parse the user's search bar input using {@link Tokenizer} and {{@link Parser}}
     *
     * @author u8003980 Chan Cheng Leong
     */
    private void parseSearchBarInput() {
        Tokenizer tokenizer = new Tokenizer(searchBar.getText().toString(), suburbList);
        Parser parser = new Parser(tokenizer);
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

    /**
     * Searches for an air quality record in the AVL tree based on the selected suburb, date, and hour.
     * <p>
     * This method retrieves the user-selected date, suburb, and hour from the UI, formats the date and time,
     * and searches for a corresponding record in the {@code recordTreeLocationAndDateKey} AVL tree.
     * If a matching record is found, it updates the UI by displaying air quality metrics and progress bars </p>
     * @author u7902000Gea,u8006862(Hexuan)Shawn
     */
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

            pm25TextView.setText(" " + record.getPm2_5());
            pm10TextView.setText(" " + record.getPm10());
            o3TextView.setText(" " + record.getO3());
            so2TextView.setText(" " + record.getSo2());
            coTextView.setText(" " + record.getCo());
            no2TextView.setText(" " + record.getNo2());

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
    /**
     * This method retrieves the progress bar level and determine their color
     * @author u8006862 (Hexuan)Shawn
     */
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
