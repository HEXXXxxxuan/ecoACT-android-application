package com.go4.application.live_data;

import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.go4.application.R;
import com.go4.application.model.AirQualityRecord;
import com.go4.utils.CsvParser;
import com.go4.utils.LocationService;
import com.go4.utils.tree.AVLTree;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuburbLiveActivity extends AppCompatActivity implements LocationService.LocationListener {
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
    private LocationService locationService;
    private String selectedSuburb;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_live);

        suburbMap = loadSuburbsFromJson();

        locationService = new LocationService(this, this);
        locationService.startLocationUpdates();


        suburbSpinnerLive = findViewById(R.id.SuburbSpinnerLive);
        intervalSpinner = findViewById(R.id.intervalSpinner);
        resultTextViewLive = findViewById(R.id.resultTextView2);
        coTextView = findViewById(R.id.coTextView);
        no2TextView = findViewById(R.id.no2TextView);
        pm25TextView = findViewById(R.id.pm25TextView);
        lineChart = findViewById(R.id.lineChart);

        selectSuburbSpinner();
        selectIntervalSpinner();

        option = IntervalOption.TODAY;

        CsvParser csvParser = new CsvParser();
        records = csvParser.createAVLTree(this, false);
    }

    @Override
    public void onLocationUpdated(Location location) {
        NearestSuburbStrategy nearestSuburbStrategy = new NearestSuburbStrategy();
        selectedSuburb = nearestSuburbStrategy.getNearestSuburb(location.getLatitude(), location.getLongitude(), suburbMap);

        Log.d("LocationDebug: ", "Nearest suburb = " + selectedSuburb);

        // Set the spinner to display the nearest suburb
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) suburbSpinnerLive.getAdapter();
        int position = adapter.getPosition(selectedSuburb);
        if (position >= 0) {
            suburbSpinnerLive.setSelection(position);
        }

        // Fetch and display data based on the nearest suburb
        fetchAndDisplayData(selectedSuburb);
    }

    public enum IntervalOption {
        TODAY, YESTERDAY, LASTWEEK
    }

    private void selectIntervalSpinner() {
        List<String> intervals = new ArrayList<>();
        intervals.add("Today");
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
                    case "Today":
                        option = IntervalOption.TODAY;
                        break;
                    case "Yesterday":
                        option = IntervalOption.YESTERDAY;
                        break;
                    case "Last week":
                        option = IntervalOption.LASTWEEK;
                        break;
                    default:
                }

                Toast.makeText(SuburbLiveActivity.this, "Fetching data, please wait...", Toast.LENGTH_SHORT).show();

                // Fetch data in a background thread
                executor.execute(() -> {
                    if (selectedSuburb != null) {
                        fetchAndDisplayData(selectedSuburb);
                    }
                });
            }



            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public enum AirQualityMetric {
        AQI, CO, NO2, PM2_5
    }

    private void fetchAndDisplayData(String selectedSuburb) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        double[] coordinates = suburbMap.get(selectedSuburb);

        fetchAirQualityData(coordinates[0], coordinates[1]);

        List<AirQualityRecord> recordsInSelectedSuburb = fetchRecordsForSuburbAndInterval(selectedSuburb);
        Log.d("LocationDebug: ", "records in nearest suburb final" + recordsInSelectedSuburb);



        plotData(recordsInSelectedSuburb, AirQualityMetric.AQI);

        resultTextViewLive.setOnClickListener(v -> plotData(recordsInSelectedSuburb, AirQualityMetric.AQI));
        coTextView.setOnClickListener(v -> plotData(recordsInSelectedSuburb, AirQualityMetric.CO));
        no2TextView.setOnClickListener(v -> plotData(recordsInSelectedSuburb, AirQualityMetric.NO2));
        pm25TextView.setOnClickListener(v -> plotData(recordsInSelectedSuburb, AirQualityMetric.PM2_5));

    }

    private List<AirQualityRecord> fetchRecordsForSuburbAndInterval(String selectedSuburb) {
        ArrayList<AirQualityRecord> recordsInSelectedSuburb = new ArrayList<>();
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startDate = null;
        String endDate = null;

        switch (option) {
            case TODAY:
                startCalendar.set(Calendar.HOUR_OF_DAY, 0);  // Start of today
                startCalendar.set(Calendar.MINUTE, 0);
                startCalendar.set(Calendar.SECOND, 0);
                startCalendar.set(Calendar.MILLISECOND, 0);
                startDate = dateFormat.format(startCalendar.getTime());
                endDate = dateFormat.format(Calendar.getInstance().getTime());
                break;
            case YESTERDAY:
                startCalendar.add(Calendar.DAY_OF_YEAR, -1);
                startCalendar.set(Calendar.HOUR_OF_DAY, 0); // Start of yesterday
                startCalendar.set(Calendar.MINUTE, 0);
                startCalendar.set(Calendar.SECOND, 0);
                startDate = dateFormat.format(startCalendar.getTime());

                endCalendar.set(Calendar.HOUR_OF_DAY, 23); // End of yesterday
                endCalendar.set(Calendar.MINUTE, 59);
                endCalendar.set(Calendar.SECOND, 59);
                endDate = dateFormat.format(endCalendar.getTime());
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
                break;
        }

        // Filter records based on the selected suburb and time range

        recordsInSelectedSuburb = (ArrayList<AirQualityRecord>) filterRecordsBySuburbAndTimestamp(records, selectedSuburb, startDate, endDate);

        Log.d("LocationDebug: ", "filtering in nearest suburb filtered by "+ startDate + endDate + recordsInSelectedSuburb);


        return recordsInSelectedSuburb;
    }


    private void selectSuburbSpinner() {
        suburbMap = loadSuburbsFromJson();


        List<String> suburbsList = new ArrayList<>();
        suburbsList.add("Select different city");  // Default entry
        suburbsList.addAll(suburbMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suburbsList.toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinnerLive.setAdapter(adapter);


        suburbSpinnerLive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSuburb = (String) parent.getItemAtPosition(position);

                // Only fetch data if a valid suburb is selected
                if (!"Select different city".equals(selectedSuburb)) {
                    fetchAndDisplayData(selectedSuburb);
                }
            }`
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

            Log.d("Location Debug:", "AVL height= " + records.getHeight());
            Log.d("Location Debug:", "searching for: " + selectedSuburb + " between " + startDate + " and " + endDate);

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

    @Override
    protected void onResume() {
        super.onResume();

        // Force a location update when returning to this activity
        if (locationService != null) {
            locationService.forceLocationUpdate();
            Toast.makeText(this, "Requesting GPS update...", Toast.LENGTH_SHORT).show();
        }
    }
}
