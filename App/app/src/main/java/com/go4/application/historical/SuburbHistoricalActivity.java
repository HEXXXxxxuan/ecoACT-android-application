package com.go4.application.historical;

import android.app.DatePickerDialog;
import android.content.Intent;
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
    private TextView resultTextView;
    private Button searchButton;


    private SemiCircleArcProgressBar semiCircleArcProgressBar;
    private ProgressBar pm25ProgressBar, pm10ProgressBar, o3ProgressBar, so2ProgressBar, coProgressBar, no2ProgressBar;
    private TextView aqiStatusTextView;

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

        // 初始化进度条控件
        aqiStatusTextView = findViewById(R.id.aqiStatusTextView);
        semiCircleArcProgressBar = findViewById(R.id.semiCircleArcProgressBar);
        pm25ProgressBar = findViewById(R.id.pm25ProgressBar);
        pm10ProgressBar = findViewById(R.id.pm10ProgressBar);
        o3ProgressBar = findViewById(R.id.o3ProgressBar);
        so2ProgressBar = findViewById(R.id.so2ProgressBar);
        coProgressBar = findViewById(R.id.coProgressBar);
        no2ProgressBar = findViewById(R.id.no2ProgressBar);

        // 初始化地点选择器和小时选择器
        setupSuburbSpinner();
        setupHourSpinner();

        editTextDate.setOnClickListener(view -> showDatePickerDialog());
        searchButton.setOnClickListener(v -> searchForRecord());

        liveDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SuburbLiveActivity.class);
            startActivity(intent);
        });

        // 搜索栏
        searchBar = findViewById(R.id.sh_search);
        testButton = findViewById(R.id.sh_testbutton);
        suburbList = loadSuburbsFromJson();
        testButton.setOnClickListener(v -> parseSearchBarInput());

        // 加载 CSV 数据到 AVLTree
        createAVLTree();
    }

    // 搜索解析
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

    // 搜索记录并更新 ProgressBar 和 SemiCircleArcProgressBar
    private void searchForRecord() {
        String selectedDate = editTextDate.getText().toString();
        String selectedSuburb = suburbSpinner.getSelectedItem().toString();
        String selectedHour = hourSpinner.getSelectedItem().toString().substring(0, 2);

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
        String key = selectedSuburb + "_" + selectedDate;
        AirQualityRecord record = recordTreeLocationAndDateKey.search(key);

        if (record != null) {
            // 更新进度条和颜色
            updateProgressBars(record);

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

    // 动态更新 ProgressBar 和颜色
    private void updateProgressBars(AirQualityRecord record) {
        // 更新 AQI 相关
        int aqi = record.getAqi();
        semiCircleArcProgressBar.setPercent(aqi);

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

        // 更新各个污染物的 ProgressBar
        updateIndividualProgressBar(pm25ProgressBar, (int) record.getPm2_5());
        updateIndividualProgressBar(pm10ProgressBar, (int) record.getPm10());
        updateIndividualProgressBar(o3ProgressBar, (int) record.getO3());
        updateIndividualProgressBar(so2ProgressBar, (int) record.getSo2());
        updateIndividualProgressBar(coProgressBar, (int) record.getCo());
        updateIndividualProgressBar(no2ProgressBar, (int) record.getNo2());
    }

    private void updateIndividualProgressBar(ProgressBar progressBar, int value) {
        progressBar.setProgress(value);
        if (value < 50) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.secondaryColorLG));
        } else if (value < 100) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.yellow));
        } else {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
        }
    }

    // 加载 AVLTree、设置选择器等代码保持不变
    private void createAVLTree() {
        CsvParser csvParser = new CsvParser();
        recordTreeLocationAndDateKey = csvParser.createAVLTree(this, false);
    }

    private void setupSuburbSpinner() {
        suburbList = loadSuburbsFromJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(adapter);
    }

    private void setupHourSpinner() {
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d:00", i));
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);
    }

    private List<String> loadSuburbsFromJson() {
        List<String> suburbs = new ArrayList<>();
        try {
            InputStream is = getAssets().open("canberra_suburbs.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                suburbs.add(jsonArray.getString(i));
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        return suburbs;
    }

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
}
