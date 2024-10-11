package com.go4.application.live_data;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.go4.application.historical.SuburbHistoricalActivity;
import com.go4.application.live_data.adapter.LoadMoreSearchResultAdapter;
import com.go4.application.live_data.listener.SearchResultEndlessRecyclerOnScrollListener;
import com.go4.application.profile.ProfileActivity;
import com.go4.utils.GPSService;
import com.go4.application.R;
import com.go4.application.model.AirQualityRecord;
import com.go4.utils.CsvParser;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;

public class SuburbLiveActivity extends AppCompatActivity {
    private AutoCompleteTextView suburbSpinnerLive;
    private AutoCompleteTextView comparingSpinner;
    private Spinner intervalSpinner;
    private TextView resultTextViewLive;
    private TextView coTextView, no2TextView, pm25TextView, pm10TextView, o3TextView, so2TexView;
    private ProgressBar pm25ProgressBar, pm10ProgressBar, o3ProgressBar, so2ProgressBar, coProgressBar, no2ProgressBar;
    private Button historicalButton;
    private SemiCircleArcProgressBar semiCircleArcProgressBar;


    private HashMap<String, double[]> suburbMap;
    private static final String API_KEY = "4f6d63b7d7512fc4b14ee2aeb89d3128";
    private AVLTree<String, AirQualityRecord> records;
    private LineChart lineChart;
    private IntervalOption option;
    private AirQualityMetric metricOption;
    private String selectedSuburb;
    private String comparedSuburb;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private GPSService gpsService;
    private boolean isBound = false;
    private List<AirQualityRecord> primarySuburbs;
    private List<AirQualityRecord> comparedSuburbs;
    private String email;

    private LoadMoreSearchResultAdapter loadMoreSearchResultAdapter;
    private List<Map<String, String>> dataList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<Map<String, String>> resultDataset = new ArrayList<>();

    Location currentLocation = null;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
            gpsService = binder.getService();
            isBound = true;

            // Get the recent location and update the UI
            currentLocation = gpsService.getRecentLocation();
            updateLocationUsingGPS(currentLocation);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suburb_live);

        Intent intent = getIntent();
        email = intent.getStringExtra("displayName");

        suburbMap = loadSuburbsFromJson();
        selectedSuburb = "";
        comparedSuburb = "";

        initializeView();
        selectSuburbSpinner();
        selectComparingSpinner();
        selectIntervalSpinner();

        option = IntervalOption.TODAY;
        metricOption = AirQualityMetric.AQI;

        CsvParser csvParser = new CsvParser();
        records = csvParser.createAVLTree(this, false);
        //Location testLocation = MainActivity.gpsService.getRecentLocation();
        //Log.d("Debugging", testLocation.toString());

        // Bind the GPS service
        Intent gpsIntent = new Intent(this, GPSService.class);
        bindService(gpsIntent, connection, Context.BIND_AUTO_CREATE);

        historicalButton.setOnClickListener(v -> {
            Intent suburbHistoricalIntent = new Intent(getApplicationContext(), SuburbHistoricalActivity.class);
            startActivity(suburbHistoricalIntent);
        });

        suburbSpinnerLive.setOnClickListener(v -> suburbSpinnerLive.setText(""));
        comparingSpinner.setOnClickListener(v -> comparingSpinner.setText(""));
        textViewClickListener();


        suburbSpinnerLive.setOnClickListener(v -> suburbSpinnerLive.setText(""));

        LinearLayout profile = findViewById(R.id.nav_profile);
        profile.setOnClickListener(v -> {
            Intent profileIntent = new Intent(SuburbLiveActivity.this, ProfileActivity.class);
            profileIntent.putExtra("displayName", email);
            startActivity(profileIntent);
            ;
        });
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int itemId = item.getItemId();
//                if (itemId == R.id.nav_profile) {
//
//                    startActivity(new Intent(SuburbLiveActivity.this, ProfileActivity.class));
//                    return true;
//                } else if (itemId == R.id.nav_suburb_live) {
//
//                    return true;
//                }
//                return false;
//            }
//        });


//        bottomNavigationView.setSelectedItemId(R.id.nav_suburb_live);

        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        ViewGroup.LayoutParams layoutParams = swipeRefreshLayout.getLayoutParams();
        layoutParams.height = 1;
        swipeRefreshLayout.setLayoutParams(layoutParams);

        // Create custom list adapter
        loadMoreSearchResultAdapter = new LoadMoreSearchResultAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(loadMoreSearchResultAdapter);

        // Set dropdown refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refresh data
                dataList.clear();
                getData(resultDataset);
                loadMoreSearchResultAdapter.notifyDataSetChanged();

                // Delay for 1 second to close dropdown refresh
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

        // Set to load more listeners
        recyclerView.addOnScrollListener(new SearchResultEndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMoreSearchResultAdapter.setLoadState(loadMoreSearchResultAdapter.LOADING);

                int size = resultDataset.size();
                if (dataList.size() < size) {
                    // Simulate obtaining network data with a delay of 1 second
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getData(resultDataset);
                                    loadMoreSearchResultAdapter.setLoadState(loadMoreSearchResultAdapter.LOADING_COMPLETE);
                                }
                            });
                        }
                    }, 1000);
                } else {
                    // Display a prompt to load to the end
                    loadMoreSearchResultAdapter.setLoadState(loadMoreSearchResultAdapter.LOADING_END);
                }
            }
        });

        loadMoreSearchResultAdapter.setOnItemClickListener(new LoadMoreSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, String> data) {
                //Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                //intent.putExtra("data", (Serializable)data);
                //startActivity(intent);
                selectedSuburb = data.get("title");
                //fetchAndDisplayData();
                // Fetch data in a background thread
                executor.execute(() -> {
                    fetchAndDisplayData();
                });
                setTitle("Suburb: " + data.get("title"));
            }
        });
    }

    private void initializeView() {
        suburbSpinnerLive = findViewById(R.id.SuburbSpinnerLive);
        suburbSpinnerLive.setSingleLine();
        comparingSpinner = findViewById(R.id.compareSuburb);
        intervalSpinner = findViewById(R.id.intervalSpinner);
        intervalSpinner.setVisibility(View.GONE);
        comparingSpinner.setVisibility(View.GONE);
        resultTextViewLive = findViewById(R.id.aqiStatusTextView);
        coTextView = findViewById(R.id.coTextView);
        no2TextView = findViewById(R.id.no2TextView);
        pm25TextView = findViewById(R.id.pm25TextView);
        pm10TextView = findViewById(R.id.pm10TextView);
        so2TexView = findViewById(R.id.so2TextView);
        o3TextView = findViewById(R.id.o3TextView);
        pm25ProgressBar = findViewById(R.id.pm25ProgressBar);
        pm10ProgressBar = findViewById(R.id.pm10ProgressBar);
        o3ProgressBar = findViewById(R.id.o3ProgressBar);
        so2ProgressBar = findViewById(R.id.so2ProgressBar);
        coProgressBar = findViewById(R.id.coProgressBar);
        no2ProgressBar = findViewById(R.id.no2ProgressBar);
        coProgressBar.setMax(1000);
        o3ProgressBar.setMax(200);
        pm10ProgressBar.setMax(20);
        pm25ProgressBar.setMax(20);
        no2ProgressBar.setMax(5);
        so2ProgressBar.setMax(10);
        historicalButton = findViewById(R.id.historicalPageButton);
        semiCircleArcProgressBar = findViewById(R.id.semiCircleArcProgressBar);
        lineChart = findViewById(R.id.lineChart);
        lineChart.setVisibility(View.GONE);
    }

    public void updateLocationUsingGPS(Location location) {
        NearestSuburbStrategy nearestSuburbStrategy = new NearestSuburbStrategy();
        selectedSuburb = nearestSuburbStrategy.getNearestSuburb(location.getLatitude(), location.getLongitude(), suburbMap);

        Log.d("LocationDebug: ", "Nearest suburb = " + selectedSuburb);

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) suburbSpinnerLive.getAdapter();
        int position = adapter.getPosition(selectedSuburb);
        if (position >= 0) {
            //suburbSpinnerLive.setText(selectedSuburb, false);
            setTitle("Suburb: " + selectedSuburb);
        }
        // Fetch and display data based on the nearest suburb
        executor.execute(this::fetchAndDisplayData);

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
                        option = IntervalOption.TODAY;
                }

                if (selectedSuburb != null && !selectedSuburb.isEmpty()) {
                    Toast.makeText(SuburbLiveActivity.this, "Fetching data, please wait...", Toast.LENGTH_SHORT).show();

                    // Fetch data in the background based on the selected interval
                    executor.execute(() -> {
                        primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);
                        if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                            comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
                        }

                        // Update the UI with the new data
                        runOnUiThread(() -> {
                            plotPrimaryData(primarySuburbs, metricOption);

                            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                                plotComparisonData(comparedSuburbs, metricOption);
                            }
                        });
                    });
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public enum AirQualityMetric {
        AQI, CO, NO2, PM2_5, PM10, O3, SO2
    }

    private void fetchAndDisplayData() {
        double[] coordinates = suburbMap.get(selectedSuburb);

        String urlString = String.format(
                "https://api.openweathermap.org/data/2.5/air_pollution?lat=%s&lon=%s&appid=%s",
                coordinates[0], coordinates[1], API_KEY);
        try {
            runOnUiThread(() -> resultTextViewLive.setText("Fetching data of " + selectedSuburb + "…"));
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                runOnUiThread(() -> resultTextViewLive.setText(selectedSuburb));
                runOnUiThread(() -> displayAirQualityData(response.toString()));
            } else {
                runOnUiThread(() -> resultTextViewLive.setText("Failed to fetch air quality data."));
            }
        } catch (Exception e) {
            runOnUiThread(() -> resultTextViewLive.setText("Error fetching data: " + e.getMessage()));
        }
    }

    private void textViewClickListener() {

        //plotData(recordsInSelectedSuburb, AirQualityMetric.AQI);

        resultTextViewLive.setOnClickListener(v -> {
            lineChart.setVisibility(View.VISIBLE);
            intervalSpinner.setVisibility(View.VISIBLE);
            comparingSpinner.setVisibility(View.VISIBLE);
            metricOption = AirQualityMetric.AQI;
            primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);
            plotPrimaryData(primarySuburbs, metricOption);
            Log.d("Comparing debug", "Primary records hashcode: " + primarySuburbs);


            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
                plotComparisonData(comparedSuburbs, metricOption);
                Log.d("Comparing debug", "Comparison records hashcode: " + comparedSuburbs);
            }

        });

        coTextView.setOnClickListener(v -> {
            lineChart.setVisibility(View.VISIBLE);
            intervalSpinner.setVisibility(View.VISIBLE);
            comparingSpinner.setVisibility(View.VISIBLE);
            metricOption = AirQualityMetric.CO;
            primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);
            plotPrimaryData(primarySuburbs, metricOption);
            Log.d("Comparing debug", "Primary records hashcode: " + primarySuburbs);


            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
                plotComparisonData(comparedSuburbs, metricOption);
                Log.d("Comparing debug", "Comparison records hashcode: " + comparedSuburbs);
            }


        });

        no2TextView.setOnClickListener(v -> {
            lineChart.setVisibility(View.VISIBLE);
            intervalSpinner.setVisibility(View.VISIBLE);
            comparingSpinner.setVisibility(View.VISIBLE);
            metricOption = AirQualityMetric.NO2;
            primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);
            plotPrimaryData(primarySuburbs, metricOption);
            Log.d("Comparing debug", "Primary records hashcode: " + primarySuburbs);


            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
                plotComparisonData(comparedSuburbs, metricOption);
                Log.d("Comparing debug", "Comparison records hashcode: " + comparedSuburbs);
            }

        });

        pm25TextView.setOnClickListener(v -> {
            lineChart.setVisibility(View.VISIBLE);
            intervalSpinner.setVisibility(View.VISIBLE);
            comparingSpinner.setVisibility(View.VISIBLE);
            metricOption = AirQualityMetric.PM2_5;
            primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);
            plotPrimaryData(primarySuburbs, metricOption);
            Log.d("Comparing debug", "Primary records hashcode: " + primarySuburbs);


            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
                plotComparisonData(comparedSuburbs, metricOption);
                Log.d("Comparing debug", "Comparison records hashcode: " + comparedSuburbs);
            }

        });

        pm10TextView.setOnClickListener(v -> {
            lineChart.setVisibility(View.VISIBLE);
            intervalSpinner.setVisibility(View.VISIBLE);
            comparingSpinner.setVisibility(View.VISIBLE);
            metricOption = AirQualityMetric.PM10;
            primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);
            plotPrimaryData(primarySuburbs, metricOption);
            Log.d("Comparing debug", "Primary records hashcode: " + primarySuburbs);


            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
                plotComparisonData(comparedSuburbs, metricOption);
                Log.d("Comparing debug", "Comparison records hashcode: " + comparedSuburbs);
            }

        });

        o3TextView.setOnClickListener(v -> {
            lineChart.setVisibility(View.VISIBLE);
            intervalSpinner.setVisibility(View.VISIBLE);
            comparingSpinner.setVisibility(View.VISIBLE);
            metricOption = AirQualityMetric.O3;
            primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);
            plotPrimaryData(primarySuburbs, metricOption);
            Log.d("Comparing debug", "Primary records hashcode: " + primarySuburbs);


            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
                plotComparisonData(comparedSuburbs, metricOption);
                Log.d("Comparing debug", "Comparison records hashcode: " + comparedSuburbs);
            }

        });

        so2TexView.setOnClickListener(v -> {
            lineChart.setVisibility(View.VISIBLE);
            intervalSpinner.setVisibility(View.VISIBLE);
            metricOption = AirQualityMetric.SO2;
            comparingSpinner.setVisibility(View.VISIBLE);
            primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);
            plotPrimaryData(primarySuburbs, metricOption);
            Log.d("Comparing debug", "Primary records hashcode: " + primarySuburbs);


            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
                plotComparisonData(comparedSuburbs, metricOption);
                Log.d("Comparing debug", "Comparison records hashcode: " + comparedSuburbs);
            }


        });
    }

    private List<AirQualityRecord> fetchRecordsForSuburbAndInterval(String selectedSuburb) {
        List<AirQualityRecord> recordsInSelectedSuburb;
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

        recordsInSelectedSuburb = filterRecordsBySuburbAndTimestamp(records, selectedSuburb, startDate, endDate);

        Log.d("Comparing debug: ", "filtering in nearest suburb filtered by " + startDate + endDate + recordsInSelectedSuburb);

        return recordsInSelectedSuburb;
    }


    private void selectSuburbSpinner() {
        // Create a list of suburbs
        List<String> suburbsList = new ArrayList<>();
        suburbsList.addAll(suburbMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suburbsList);
        suburbSpinnerLive.setAdapter(adapter);

        suburbSpinnerLive.setThreshold(1);

        suburbSpinnerLive.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Clear the text when user clicks the text
                suburbSpinnerLive.setText("");
            }
        });


        // Set listener to handle suburb selection
        suburbSpinnerLive.setOnItemClickListener((parent, view, position, id) -> {
            selectedSuburb = (String) parent.getItemAtPosition(position);

            if (selectedSuburb != null && !selectedSuburb.isEmpty()) {

                search(selectedSuburb);
                executor.execute(() -> {
                    fetchAndDisplayData();
                    primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);

                    // Update UI on the main thread after data is fetched
                    runOnUiThread(() -> {
                        plotPrimaryData(primarySuburbs, metricOption);
                    });

                    Log.d("Comparing debug", "Primary location: " + selectedSuburb + primarySuburbs);
                    Log.d("Comparing debug", "Comparing location: " + comparedSuburb + comparedSuburbs);

                    comparedSuburb = "";
                    Log.d("Comparing debug", "primary location: " + selectedSuburb);

                });

            } else {
                resultTextViewLive.setText("No suburb selected.");
            }
        });

        suburbSpinnerLive.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER))) {

                    String query = suburbSpinnerLive.getText().toString();
                    search(query);
                    // 如果按下的是回车键，并且你想要关闭输入法键盘，可以调用如下代码：
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void selectComparingSpinner() {

        // Create a list of suburbs and add the default entry as the first item
        List<String> suburbsList = new ArrayList<>();
        suburbsList.addAll(suburbMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suburbsList);
        comparingSpinner.setAdapter(adapter);

        comparingSpinner.setThreshold(1);
        comparingSpinner.setOnItemClickListener((parent, view, position, id) -> {

            comparedSuburb = (String) parent.getItemAtPosition(position);

            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                // Fetch data for the second suburb

                executor.execute(() -> {
                    comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
                    runOnUiThread(() -> {
                        plotComparisonData(comparedSuburbs, metricOption);
                    });
                });

                Log.d("Comparing debug", "Primary location: " + selectedSuburb + primarySuburbs);
                Log.d("Comparing debug", "Comparing location: " + comparedSuburb + comparedSuburbs);
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

//    private void fetchAirQualityData(double latitude, double longitude) {
//        String urlString = String.format(
//                "https://api.openweathermap.org/data/2.5/air_pollution?lat=%s&lon=%s&appid=%s",
//                latitude, longitude, API_KEY);
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                StringBuilder response = new StringBuilder();
//                String inputLine;
//
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//
//                displayAirQualityData(response.toString());
//            } else {
//                resultTextViewLive.setText("Failed to fetch air quality data.");
//            }
//        } catch (Exception e) {
//            resultTextViewLive.setText("Error fetching data: " + e.getMessage());
//        }
//    }

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

            coTextView.setText(String.valueOf(components.getDouble("co")));
            no2TextView.setText(String.valueOf(components.getDouble("no2")));
            pm25TextView.setText(String.valueOf(components.getDouble("pm2_5")));
            pm10TextView.setText(String.valueOf(components.getDouble("pm10")));
            o3TextView.setText(String.valueOf(components.getDouble("o3")));
            so2TexView.setText(String.valueOf(components.getDouble("so2")));

            updateProgressBar(pm25ProgressBar, "PM2.5", components.getDouble("pm2_5"));
            updateProgressBar(pm10ProgressBar, "PM10", components.getDouble("pm10"));
            updateProgressBar(o3ProgressBar, "O3", components.getDouble("o3"));
            updateProgressBar(so2ProgressBar, "SO2", components.getDouble("so2"));
            updateProgressBar(coProgressBar, "CO", components.getDouble("co"));
            updateProgressBar(no2ProgressBar, "NO2", components.getDouble("no2"));

            // Update semiCircleArcProgressBar and AQI status

            int aqi = (int) Math.round(main.getDouble("aqi"));

            // Ensure AQI is an integer
            semiCircleArcProgressBar.setPercent(aqi);
            if (aqi < 50) {
                resultTextViewLive.setText(aqi + " AQI 🙂 Low");
                resultTextViewLive.setTextColor(ContextCompat.getColor(this, R.color.secondaryColorLG));  // Green
                semiCircleArcProgressBar.setProgressBarColor(ContextCompat.getColor(this, R.color.secondaryColorLG));  // Green
            } else if (aqi < 100) {
                resultTextViewLive.setText(aqi + " AQI 😐 Moderate");
                resultTextViewLive.setTextColor(ContextCompat.getColor(this, R.color.yellow));  // Yellow
                semiCircleArcProgressBar.setProgressBarColor(ContextCompat.getColor(this, R.color.yellow));  // Yellow
            } else {
                resultTextViewLive.setText(aqi + " AQI 😷 High");
                resultTextViewLive.setTextColor(ContextCompat.getColor(this, R.color.red));  // Red
                semiCircleArcProgressBar.setProgressBarColor(ContextCompat.getColor(this, R.color.red));  // Red
            }

        } catch (JSONException e) {
            resultTextViewLive.setText("Error parsing JSON response.");
        }
    }

//    private void plotData(List<AirQualityRecord> data, AirQualityMetric metric) {
//        ArrayList<Entry> entries = new ArrayList<>();
//        ArrayList<String> hourIndex = new ArrayList<>();
//
//        // data points to the entries list
//        for (int i = 0; i < data.size(); i++) {
//            String formattedTime = data.get(i).getTimestamp().substring(11, 16);
//
//            float value = 0f;
//            switch (metric) {
//                case AQI:
//                    value = Float.parseFloat(String.valueOf(data.get(i).getAqi()));
//                    break;
//                case CO:
//                    value = Float.parseFloat(String.valueOf(data.get(i).getCo()));
//                    break;
//                case NO2:
//                    value = Float.parseFloat(String.valueOf(data.get(i).getNo2()));
//                    break;
//                case PM2_5:
//                    value = Float.parseFloat(String.valueOf(data.get(i).getPm2_5()));
//                    break;
//                case O3:
//                    value = Float.parseFloat(String.valueOf(data.get(i).getO3()));
//                    break;
//                case SO2:
//                    value = Float.parseFloat(String.valueOf(data.get(i).getSo2()));
//                    break;
//                case PM10:
//                    value = Float.parseFloat(String.valueOf(data.get(i).getPm10()));
//            }
//
//            entries.add(new Entry(i, value));
//            hourIndex.add(formattedTime);
//        }
//
//
//        LineDataSet dataSet = new LineDataSet(entries, "AQI over Time");
//        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//
//        //customize the appearance
//        dataSet.setMode(LineDataSet.Mode.LINEAR); //smooth line
//        dataSet.setLineWidth(4f);
//        dataSet.setCircleRadius(0f); // Remove circles at data points
//        dataSet.setDrawValues(false); // Hide values at the data points
//        dataSet.setColor(Color.GREEN); // Line color
//
//        lineChart.getDescription().setEnabled(false);
//
//        // Fill the area under the chart
//        dataSet.setDrawFilled(true);
//        dataSet.setFillColor(Color.GREEN);
//        dataSet.setFillAlpha(40);
//
//        // Create a LineData object with the dataset and set it to the chart
//        LineData lineData = new LineData(dataSet);
//        lineChart.setData(lineData);
//
//        // Remove grid lines
//        lineChart.getXAxis().setDrawGridLines(false);
//        lineChart.getAxisLeft().setDrawGridLines(false);
//        lineChart.getAxisRight().setDrawGridLines(false);
//
//        // Customize X and Y axis
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(hourIndex));
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        YAxis yAxisLeft = lineChart.getAxisLeft();
//        YAxis yAxisRight = lineChart.getAxisRight();
//        yAxisRight.setEnabled(false); // Disable right Y axis
//
//        lineChart.invalidate();
//    }

    private void plotPrimaryData(List<AirQualityRecord> data, AirQualityMetric metric) {
        if (data == null || data.isEmpty()) {
            Log.d("Plotting Error", "Primary data is empty or null");
            return;
        }

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> hourIndex = new ArrayList<>();

        // Prepare the entries and labels
        for (int i = 0; i < data.size(); i++) {
            String formattedTime = data.get(i).getTimestamp().substring(11, 16);
            float value = getMetricValue(data.get(i), metric);
            entries.add(new Entry(i, value));
            hourIndex.add(formattedTime);
        }

        // Get or create the LineData object
        LineData lineData = lineChart.getData();
        if (lineData == null) {
            lineData = new LineData();
            lineChart.setData(lineData);
        }

        // Remove the existing "Primary" dataset if it exists to avoid overlap
        LineDataSet primaryDataSet = (LineDataSet) lineData.getDataSetByLabel("Primary", true);
        if (primaryDataSet != null) {
            lineData.removeDataSet(primaryDataSet);
        }

        // Create a new dataset and add it to the chart
        primaryDataSet = createDataSet(entries, "Primary", Color.GREEN);
        lineData.addDataSet(primaryDataSet);

        // Set the labels for the X-axis
        setXAxisLabels(hourIndex);

        // Log for debugging
        Log.d("PrimaryPlotDebug", "Primary plot data : " + data);

        // Update and refresh the chart
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void plotComparisonData(List<AirQualityRecord> data, AirQualityMetric metric) {
        if (data == null || data.isEmpty()) {
            Log.d("Plotting Error", "Comparison data is empty or null");
            return;
        }

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> hourIndex = new ArrayList<>();

        // Prepare the entries and labels
        for (int i = 0; i < data.size(); i++) {
            String formattedTime = data.get(i).getTimestamp().substring(11, 16);
            float value = getMetricValue(data.get(i), metric);
            entries.add(new Entry(i, value));
            hourIndex.add(formattedTime);
        }

        // Get or create the LineData object
        LineData lineData = lineChart.getData();
        if (lineData == null) {
            lineData = new LineData();
            lineChart.setData(lineData);
        }

        // Remove the existing "Comparison" dataset if it exists to avoid overlap
        LineDataSet comparisonDataSet = (LineDataSet) lineData.getDataSetByLabel("Comparison", true);
        if (comparisonDataSet != null) {
            lineData.removeDataSet(comparisonDataSet);
        }

        // Create a new dataset and add it to the chart
        comparisonDataSet = createDataSet(entries, "Comparison", Color.BLUE);
        lineData.addDataSet(comparisonDataSet);

        // Set the labels for the X-axis
        setXAxisLabels(hourIndex);

        // Log for debugging
        Log.d("ComparisonPlotDebug", "Comparison plot data: " + data);

        // Update and refresh the chart
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private float getMetricValue(AirQualityRecord record, AirQualityMetric metric) {
        switch (metric) {
            case AQI:
                return (float) record.getAqi();
            case CO:
                return (float) record.getCo();
            case NO2:
                return (float) record.getNo2();
            case PM2_5:
                return (float) record.getPm2_5();
            case O3:
                return (float) record.getO3();
            case SO2:
                return (float) record.getSo2();
            case PM10:
                return (float) record.getPm10();
            default:
                return 0;
        }
    }

    private LineDataSet createDataSet(ArrayList<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setLineWidth(4f);
        dataSet.setDrawValues(false);
        dataSet.setColor(color);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setFillAlpha(40);
        return dataSet;
    }

    private void setXAxisLabels(ArrayList<String> hourIndex) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(hourIndex));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    // Get 20 latest data each time
    private void getData(List<Map<String, String>> data) {
        int size = data.size();
        for (int i = 0; i < 20; i++) {
            int n = dataList.size();
            if (n >= size) {
                break;
            }
            dataList.add(data.get(n));
        }
    }

    private void search(String query) {
        HashMap<String, double[]> newMap = new HashMap<>();
        for (Map.Entry<String, double[]> entry : suburbMap.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith(query.toLowerCase())) {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }

        NearestSuburbStrategy nearestSuburbStrategy = new NearestSuburbStrategy();
        resultDataset = nearestSuburbStrategy.getNearestSuburbList(currentLocation.getLatitude(), currentLocation.getLongitude(), newMap);

        // Sort
        Collections.sort(resultDataset, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return Double.compare(Double.parseDouble(o1.get("distance")), Double.parseDouble(o2.get("distance")));
            }
        });

        dataList.clear();
        getData(resultDataset);
        loadMoreSearchResultAdapter.notifyDataSetChanged();

        suburbSpinnerLive.dismissDropDown();

        ViewGroup.LayoutParams layoutParams = swipeRefreshLayout.getLayoutParams();
        if (resultDataset.isEmpty()) {
            Toast.makeText(SuburbLiveActivity.this, "No matching data was found in the search!", Toast.LENGTH_SHORT).show();
            layoutParams.height = 1;
        } else {
            layoutParams.height = (int) (300 * getApplication().getResources().getDisplayMetrics().density);
        }
        swipeRefreshLayout.setLayoutParams(layoutParams);
    }
}