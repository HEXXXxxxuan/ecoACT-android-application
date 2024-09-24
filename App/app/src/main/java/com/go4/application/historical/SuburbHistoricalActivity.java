package com.go4.application.historical;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.go4.application.R;
import com.go4.application.tree.AVLTree;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SuburbHistoricalActivity extends AppCompatActivity {
    private Spinner suburbSpinner;
    private EditText editTextDate;
    //private List<Record> recordList;
    private AVLTree recordTree;
    private TextView resultTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_historical);

        suburbSpinner = findViewById(R.id.suburbSpinner);
        editTextDate = findViewById(R.id.editTextDate);
        resultTextView = findViewById(R.id.resultTextView);

        editTextDate.setOnClickListener(view -> showDatePickerDialog());

        List<String> suburbList = loadSuburbsFromJson();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(adapter);

        CsvParser csvParser = new CsvParser();
        //recordList = csvParser.parseCsV(this, "environment_monitoring_data.csv");

        recordTree = new AVLTree();
        // Parse the CSV and insert records into AVLTree
        List<Record> recordList = csvParser.parseCsV(this, "environment_monitoring_data.csv");
        for (Record record : recordList) {
            String key = record.getLocation() + "_" + record.getDate();
            recordTree.insert(key, record);
        }

        findViewById(R.id.searchButton).setOnClickListener(v -> searchForRecord());

    }

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

    private void showDatePickerDialog() {
        // Get the current date
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

    private void searchForRecord() {
        String selectedDate = editTextDate.getText().toString();
        String selectedSuburb = suburbSpinner.getSelectedItem().toString();

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

        Log.d("SearchDebug", "Selected Date UI: " + selectedDate);

//        boolean recordFound = false;
//
//        for (Record record : recordList) {
//
//            if (record.getDate().equals(selectedDate) && record.getLocation().equalsIgnoreCase(selectedSuburb)) {
//                String result = "Temperature: " + record.getTemperature() + "°C\n" +
//                        "Smoke Level: " + record.getSmokeLevel() + " ppm\n" +
//                        "Carbon Monoxide: " + record.getCarbonMonoxide() + " ppm";
//                resultTextView.setText(result);
//                recordFound = true;
//                break;
//            }
//
//            if (!recordFound) {
//                resultTextView.setText("No matching records.");
//            }
//        }

        //search in tree

        String key = selectedSuburb + "_" + selectedDate;  // Generate key for search

        Record record = recordTree.search(key);  // Search the AVLTree for the key
        if (record != null) {
            String result = "Temperature: " + record.getTemperature() + "°C\n" +
                    "Smoke Level: " + record.getSmokeLevel() + " ppm\n" +
                    "Carbon Monoxide: " + record.getCarbonMonoxide() + " ppm";
            resultTextView.setText(result);
        } else {
            resultTextView.setText("No matching records.");
        }

    }

}
