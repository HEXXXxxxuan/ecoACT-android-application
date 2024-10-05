package com.go4.application.live_data;

import android.graphics.Color;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.go4.application.R;
import com.go4.application.historical.AirQualityRecord;
import com.go4.utils.CsvParser;
import com.go4.application.tree.AVLTree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SuburbLiveActivity extends AppCompatActivity {
    private Spinner suburbSpinnerLive;
    private Spinner intervalSpinner;
    private TextView resultTextViewLive;
    private TextView coTextView;
    private TextView no2TextView;
    private TextView pm25TextView;
    private HashMap<String, double[]> suburbMap;
    private static final String API_KEY = "4f6d63b7d7512fc4b14ee2aeb89d3128";
    private AVLTree<String, AirQualityRecord> records;
    private LineChart lineChart;
    private IntervalOption option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_live);
        suburbSpinnerLive = findViewById(R.id.SuburbSpinnerLive);
        intervalSpinner = findViewById(R.id.intervalSpinner);
        resultTextViewLive = findViewById(R.id.resultTextView2);
        coTextView = findViewById(R.id.coTextView);
        no2TextView = findViewById(R.id.no2TextView);
        pm25TextView = findViewById(R.id.pm25TextView);
        lineChart = findViewById(R.id.lineChart);
        selectSuburbSpinner();
        selectIntervalSpinner();

        option = IntervalOption.LAST24;

        CsvParser csvParser = new CsvParser();
        records = csvParser.createAVLTree(this, false);
    }

    public enum IntervalOption {
        LAST24, YESTERDAY, LASTWEEK
    }

    private void selectIntervalSpinner() {
        List<String> intervals = new ArrayList<>();
        intervals.add("Last 24 hour");
        intervals.add("Yesterday");
        intervals.add("Last week");

        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intervals);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(intervalAdapter);

        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedInterval = intervals.get(position);



                switch (selectedInterval) {
                    case "Last 24 hour":
                        option = IntervalOption.LAST24;
                        selectSuburbSpinner();
                        break;
                    case "Yesterday":
                        option = IntervalOption.YESTERDAY;
                        selectSuburbSpinner();
                        break;
                    case "Last week":
                        option = IntervalOption.LASTWEEK;
                        selectSuburbSpinner();
                        break;
                    default:

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public enum AirQualityMetric {
        AQI, CO, NO2, PM2_5
    }

    private void selectSuburbSpinner() {
        suburbMap = loadSuburbsFromJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbMap.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinnerLive.setAdapter(adapter);


        suburbSpinnerLive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSuburb = parentView.getItemAtPosition(position).toString();
                double[] coordinates = suburbMap.get(selectedSuburb);
                String startDate = null;
                String endDate = null;

                /////////(for testing only!!)
               StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
               StrictMode.setThreadPolicy(policy);

                // Fetch air quality data using coordinates and display the result
                fetchAirQualityData(coordinates[0], coordinates[1]);

                ArrayList<AirQualityRecord> recordsInSelecetedSuburb = new ArrayList<>();

                Calendar startCalendar = Calendar.getInstance();
                Calendar endCalendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                switch (option) {
                    case LAST24:
                        startCalendar.add(Calendar.DAY_OF_YEAR, -1);  // Last 24 hours
                        startDate = dateFormat.format(startCalendar.getTime());
                        endDate = dateFormat.format(Calendar.getInstance().getTime());
                        recordsInSelecetedSuburb = (ArrayList<AirQualityRecord>) filterRecordsBySuburbAndTimestamp(records, selectedSuburb, startDate, endDate);
                        Log.d("Plot Last 24 Result:", recordsInSelecetedSuburb.toString());
                        break;

                    case YESTERDAY:
                        startCalendar.add(Calendar.DAY_OF_YEAR, -1);
                        startCalendar.set(Calendar.HOUR_OF_DAY, 0); // Start of yesterday
                        startCalendar.set(Calendar.MINUTE, 0);
                        startCalendar.set(Calendar.SECOND, 0);
                        startDate = dateFormat.format(startCalendar.getTime());

                        endCalendar = (Calendar) startCalendar.clone();
                        endCalendar.set(Calendar.HOUR_OF_DAY, 23); // End of yesterday
                        endCalendar.set(Calendar.MINUTE, 59);
                        endCalendar.set(Calendar.SECOND, 59);
                        endDate = dateFormat.format(endCalendar.getTime());

                        recordsInSelecetedSuburb = (ArrayList<AirQualityRecord>) filterRecordsBySuburbAndTimestamp(records, selectedSuburb, startDate, endDate);
                        Log.d("Plot Yesterday Result:", recordsInSelecetedSuburb.toString());
                        break;

                    case LASTWEEK:
                        startCalendar.add(Calendar.DAY_OF_YEAR, -7); // Last 7 days
                        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        startCalendar.set(Calendar.MINUTE, 0);
                        startCalendar.set(Calendar.SECOND, 0);
                        startDate = dateFormat.format(startCalendar.getTime());

                        endCalendar.add(Calendar.DAY_OF_YEAR, -1);
                        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                        endCalendar.set(Calendar.MINUTE, 59);
                        endCalendar.set(Calendar.SECOND, 59);
                        endDate = dateFormat.format(endCalendar.getTime());

                        recordsInSelecetedSuburb = (ArrayList<AirQualityRecord>) filterRecordsBySuburbAndTimestamp(records, selectedSuburb, startDate, endDate);
                        Log.d("Plot Lastweek Result:", String.valueOf(recordsInSelecetedSuburb.size()));
                        break;

                }

                final List<AirQualityRecord> finalRecordsInSelecetedSuburb = recordsInSelecetedSuburb;


                //traverse the tree
//                records.inOrderTraversal(records.getRoot(), (k, r) -> {
//                    if(k.startsWith(selectedSuburb)){
//                        recordsInSelecetedSuburb.add(r);
//                        Log.d("AQI Result", "found " + selectedSuburb);
//                    }
//                });

                Log.d("AQI Result", recordsInSelecetedSuburb.toString());
                plotData(recordsInSelecetedSuburb, AirQualityMetric.AQI);

                resultTextViewLive.setOnClickListener(v -> plotData(finalRecordsInSelecetedSuburb, AirQualityMetric.AQI));
                coTextView.setOnClickListener(v -> plotData(finalRecordsInSelecetedSuburb, AirQualityMetric.CO));
                no2TextView.setOnClickListener(v -> plotData(finalRecordsInSelecetedSuburb, AirQualityMetric.NO2));
                pm25TextView.setOnClickListener(v -> plotData(finalRecordsInSelecetedSuburb, AirQualityMetric.PM2_5));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing selected
            }
        });
    }

    public List<AirQualityRecord> filterRecordsBySuburbAndTimestamp(AVLTree<String, AirQualityRecord> records,
                                                                    String selectedSuburb, String startTimestamp,
                                                                    String endTimestamp) {

        List<AirQualityRecord> recordsInSelectedSuburb = new ArrayList<>();


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date startDate = dateFormat.parse(startTimestamp);
            Date endDate = dateFormat.parse(endTimestamp);

            // Traverse the AVL tree and filter by suburb and timestamp range
            records.inOrderTraversal(records.getRoot(), (k, r) -> {
                if (k.startsWith(selectedSuburb)) {
                    // Extract the timestamp from the key
                    String[] keyParts = k.split("_");
                    if (keyParts.length > 1) {
                        String recordTimestamp = keyParts[1];

                        try {
                            // Convert record timestamp to Date object
                            Date recordDate = dateFormat.parse(recordTimestamp);

                            // Check if the record's timestamp is within the range
                            if (recordDate != null && !recordDate.before(startDate) && !recordDate.after(endDate)) {
                                recordsInSelectedSuburb.add(r);
                                Log.d("AQI Result", "Found record in " + selectedSuburb + " with timestamp in range");
                            }
                        } catch (ParseException e) {
                            Log.e("AVLTree", "Error parsing record timestamp: " + recordTimestamp, e);
                        }
                    }
                }
            });

        } catch (ParseException e) {
            Log.e("AVLTree", "Error parsing start or end timestamp", e);
        }

        // Return the filtered list
        return recordsInSelectedSuburb;
    }

    private void fetchAirQualityData(double latitude, double longitude) {
        String urlString = String.format(
                "https://api.openweathermap.org/data/2.5/air_pollution?lat=%s&lon=%s&appid=%s",
                latitude, longitude, API_KEY);
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                displayAirQualityData(response.toString());
            } else {
                resultTextViewLive.setText("Failed to fetch air quality data.");
            }
        } catch (Exception e) {
            resultTextViewLive.setText("Error fetching data: " + e.getMessage());
        }
    }

    private HashMap<String, double[]> loadSuburbsFromJson() {
        HashMap<String, double[]> suburbMap = new HashMap<>();
        try {
            InputStream is = getAssets().open("canberra_suburbs_coordinates.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(json);

            // Iterate through suburb names
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String suburb = keys.next();
                JSONArray coordinates = jsonObject.getJSONArray(suburb);
                double latitude = coordinates.getDouble(0);
                double longitude = coordinates.getDouble(1);
                suburbMap.put(suburb, new double[]{latitude, longitude});
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        return suburbMap;
    }

    private void displayAirQualityData(String jsonResponse) {
        try {
            JSONObject responseObj = new JSONObject(jsonResponse);
            JSONArray listArray = responseObj.getJSONArray("list");
            JSONObject firstRecord = listArray.getJSONObject(0);
            JSONObject components = firstRecord.getJSONObject("components");
            JSONObject main = firstRecord.getJSONObject("main");

            String displayText = String.valueOf(main.getDouble("aqi"));

            resultTextViewLive.setText(displayText);
            coTextView.setText(String.valueOf(components.getDouble("co")));
            no2TextView.setText(String.valueOf(components.getDouble("no2")));
            pm25TextView.setText(String.valueOf(components.getDouble("pm2_5")));
        } catch (JSONException e) {
            resultTextViewLive.setText("Error parsing JSON response.");
        }
    }

    private void plotData(List<AirQualityRecord> data, AirQualityMetric metric) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> hourIndex = new ArrayList<>();

        // data points to the entries list
        for (int i = 0; i < data.size(); i++) {
            String formattedTime = data.get(i).getTimestamp().substring(11, 16);

            float value = 0f;
            switch (metric) {
                case AQI:
                    value = Float.parseFloat(String.valueOf(data.get(i).getAqi()));
                    break;
                case CO:
                    value = Float.parseFloat(String.valueOf(data.get(i).getCo()));
                    break;
                case NO2:
                    value = Float.parseFloat(String.valueOf(data.get(i).getNo2()));
                    break;
                case PM2_5:
                    value = Float.parseFloat(String.valueOf(data.get(i).getPm2_5()));
                    break;
            }

            entries.add(new Entry(i, value));
            hourIndex.add(formattedTime);
        }


        LineDataSet dataSet = new LineDataSet(entries, "AQI over Time");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        //customize the appearance
        dataSet.setMode(LineDataSet.Mode.LINEAR); //smooth line
        dataSet.setLineWidth(4f);
        dataSet.setCircleRadius(0f); // Remove circles at data points
        dataSet.setDrawValues(false); // Hide values at the data points
        dataSet.setColor(Color.GREEN); // Line color

        lineChart.getDescription().setEnabled(false);

        // Fill the area under the chart
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.GREEN);
        dataSet.setFillAlpha(40);

        // Create a LineData object with the dataset and set it to the chart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Remove grid lines
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        // Customize X and Y axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(hourIndex));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false); // Disable right Y axis

        lineChart.invalidate();
    }
}
