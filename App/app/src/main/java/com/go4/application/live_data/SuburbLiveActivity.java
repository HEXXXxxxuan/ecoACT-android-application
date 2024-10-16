package com.go4.application.live_data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
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
import com.go4.application.MainActivity;
import com.go4.application.historical.SuburbHistoricalActivity;
import com.go4.application.live_data.MockDataStream.MockJSONResponse;
import com.go4.application.live_data.adapter.LoadMoreSearchResultAdapter;
import com.go4.application.live_data.listener.SearchResultEndlessRecyclerOnScrollListener;
import com.go4.application.profile.ProfileActivity;
import com.go4.application.R;
import com.go4.application.model.AirQualityRecord;
import com.go4.utils.CsvParser;
import com.go4.utils.design_pattern.ExecutorServiceSingleton;
import com.go4.utils.tree.AVLTree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;

/**
 * This class is responsible for displaying live air quality data for various suburbs. It also provides
 * a refresh mechanism for real-time data updates at regular intervals.
 *
 * <p>This class includes several key features:</p>
 * <ul>
 *     <li>Integration with GPS services to automatically detect the nearest suburb</li>
 *     <li>Suburb selection via auto complete spinner</li>
 *     <li>Streaming and displaying air quality metrics such as CO, NO2, PM2.5, PM10, O3, and SO2 via text views and progress bars</li>
 *     <li>Display air quality data using the OpenWeatherMap API or mock data for testing</li>
 *     <li>Graph plotting functionality for visualizing air quality metrics over time </li>
 *     <li>Support for interval selection (Today, Yesterday, Last Week) </li>
 *     <li>Comparison of air quality metrics trends between two selected suburbs</li>
 * </ul>
 *
 * @author u7902000 Gea Linggar
 * @author u7635535 Zechuan Liu
 * @author u8006862 Hexuan(Shawn)
 * @see com.go4.application.historical.SuburbHistoricalActivity
 * @see com.github.mikephil.charting.charts.LineChart
 * @see com.go4.utils.GPSService
 * @see com.go4.utils.design_pattern.ExecutorServiceSingleton
 * @see com.go4.application.live_data.adapter.LoadMoreSearchResultAdapter
 */
public class SuburbLiveActivity extends AppCompatActivity {
    private AutoCompleteTextView suburbSpinnerLive;
    private AutoCompleteTextView comparingSpinner;
    private Spinner intervalSpinner;
    private TextView resultTextViewLive;
    private TextView coTextView, no2TextView, pm25TextView, pm10TextView, o3TextView, so2TexView;
    private ProgressBar pm25ProgressBar, pm10ProgressBar, o3ProgressBar, so2ProgressBar, coProgressBar, no2ProgressBar;
    private SemiCircleArcProgressBar semiCircleArcProgressBar;
    private HashMap<String, double[]> suburbMap;
    private AVLTree<String, AirQualityRecord> records;
    private LineChart lineChart;
    private IntervalOption option;
    private AirQualityMetric metricOption;
    private String selectedSuburb, comparedSuburb;
    private final ExecutorService executor = ExecutorServiceSingleton.getInstance();
    private List<AirQualityRecord> primarySuburbs;
    private List<AirQualityRecord> comparedSuburbs;
    private String email;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable fetchTask;
    private LoadMoreSearchResultAdapter loadMoreSearchResultAdapter;
    private List<Map<String, String>> dataList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<Map<String, String>> resultDataset = new ArrayList<>();

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

        clickListener();

        ViewGroup.LayoutParams layoutParams = swipeRefreshLayout.getLayoutParams();
        layoutParams.height = 1;
        swipeRefreshLayout.setLayoutParams(layoutParams);

        // Create custom list adapter
        loadMoreSearchResultAdapter = new LoadMoreSearchResultAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(loadMoreSearchResultAdapter);

        // Set dropdown refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // refresh data
            dataList.clear();
            getData(resultDataset);
            loadMoreSearchResultAdapter.notifyDataSetChanged();

            // Delay for 1 second to close dropdown refresh
            swipeRefreshLayout.postDelayed(() -> {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);
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
                            runOnUiThread(() -> {
                                getData(resultDataset);
                                loadMoreSearchResultAdapter.setLoadState(loadMoreSearchResultAdapter.LOADING_COMPLETE);
                            });
                        }
                    }, 1000);
                } else {
                    // Display a prompt to load to the end
                    loadMoreSearchResultAdapter.setLoadState(loadMoreSearchResultAdapter.LOADING_END);
                }
            }
        });

        loadMoreSearchResultAdapter.setOnItemClickListener(data -> {
            selectedSuburb = data.get("title");

            // Fetch data in a background thread
            executor.execute(this::showDataAndRefresh);
            suburbSpinnerLive.setText(data.get("title"));
            setTitle("Suburb: " + data.get("title"));
        });

        updateLocationUsingGPS();
    }

    /**
     * Initializes and sets up all the UI components of the activity.
     *
     */
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
        pm10ProgressBar.setMax(50);
        pm25ProgressBar.setMax(50);
        no2ProgressBar.setMax(5);
        so2ProgressBar.setMax(10);
        semiCircleArcProgressBar = findViewById(R.id.semiCircleArcProgressBar);
        lineChart = findViewById(R.id.lineChart);
        lineChart.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    /**
     * Updates the selected suburb based on the user's current GPS location and periodically update the data.
     * <p>
     * This method uses the GPS service to obtain the user's most recent location, determines the nearest suburb
     * using the {@link NearestSuburbStrategy}, and updates the UI to display the nearest suburb. It then triggers
     * a background task to fetch and refresh the air quality data for the selected suburb.
     * </p>
     *
     * @author u7902000 Gea Linggar
     */
    public void updateLocationUsingGPS() {
        NearestSuburbStrategy nearestSuburbStrategy = new NearestSuburbStrategy();
        Location location = MainActivity.gpsService.getRecentLocation();
        selectedSuburb = nearestSuburbStrategy.getNearestSuburb(location.getLatitude(), location.getLongitude(), suburbMap);
        Log.d("LocationDebug: ", "Nearest suburb = " + selectedSuburb + "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) suburbSpinnerLive.getAdapter();
        int position = adapter.getPosition(selectedSuburb);
        if (position >= 0) {
            setTitle("Suburb: " + selectedSuburb);
        }

        suburbSpinnerLive.setText(selectedSuburb);
        // Fetch and display data based on the nearest suburb
        executor.execute(this::showDataAndRefresh);
    }

    /**
     * Enum representing the available time intervals for fetching air quality data.
     * <p>
     * The available options are:
     * <ul>
     *   <li>{@link #TODAY}: Fetch data for today.</li>
     *   <li>{@link #YESTERDAY}: Fetch data for yesterday.</li>
     *   <li>{@link #LASTWEEK}: Fetch data for the last week.</li>
     * </ul>
     */
    public enum IntervalOption {
        TODAY, YESTERDAY, LASTWEEK
    }

    /**
     * Sets up the {@link Spinner} for selecting a time interval and handles the selection events.
     * <p>
     * This method populates the spinner with a list of interval options such as "Today", "Yesterday",
     * and "Last week". When an item is selected, the corresponding {@link IntervalOption} is set,
     * and the air quality data is fetched and displayed for the selected suburb.
     * </p>
     * <ul>
     *   <li>Populates the interval spinner with "Today", "Yesterday", and "Last week" options.</li>
     *   <li>Handles user selection by triggering a data fetch for the selected interval.</li>
     *   <li>If the user has selected a suburb, the method fetches the data for that suburb based on the selected interval.</li>
     *   <li>If a comparison suburb is selected, it also fetches data for the comparison suburb.</li>
     *   <li>The data is displayed on the UI through the methods {@link #plotPrimaryData} and {@link #plotComparisonData}.</li>
     * </ul>
     *
     * @author u7902000 Gea Linggar
     */
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

    /**
     * Enum representing the different air quality metrics that can be displayed.
     * @author u7902000 Gea Linggar
     */
    public enum AirQualityMetric {
        AQI, CO, NO2, PM2_5, PM10, O3, SO2
    }

    /**
     * Fetches and displays mock air quality data.
     * <p>
     * This method utilizes the {@link MockJSONResponse} class to fetch mock air quality data
     * in the form of a JSON response. The retrieved data is then passed to
     * {@link #displayAirQualityData(String)} for display on the UI. This is used to demonstrate
     * our app's ability to stream data. Otherwise, it would take an hour for the data from the API
     * to change.
     * </p>
     * @author Gea Lingar
     */
    private void fetchAndDisplayMockData() {
        MockJSONResponse mockJSONResponse = new MockJSONResponse();
        String response = mockJSONResponse.getMockResponse();
        displayAirQualityData(response);
    }

    private void fetchAndDisplayData(){}

    /**
     * Starts a periodic task to fetch and display air quality data every two minutes.
     * <p>
     * This method sets up a {@link Runnable} task that calls {@link #fetchAndDisplayMockData()} to
     * fetch air quality data. The task is scheduled to run every 2 minutes (120,000 ms)
     * This allows the app to refresh the displayed data periodically.
     * The developer can choose to select between {@link #fetchAndDisplayData()} and
     * {@link #fetchAndDisplayMockData()} to select the source of stream data.
     * </p>
     *
     * @author u7902000 Gea linggar
     */
    private void showDataAndRefresh() {
        fetchTask = new Runnable() {
            @Override
            public void run() {
                //fetchAndDisplayData(); //run using live API
                fetchAndDisplayMockData(); //run using mock data
                handler.postDelayed(this, 120000); // loop interval of two minutes
            }
        };
        handler.post(fetchTask);
    }

    /**
     * Stops the periodic data refresh task.
     *
     * <p>This method is called to stop the periodic refresh when the activity is paused or destroyed,
     * to prevent memory leaks or unnecessary updates.</p>
     *
     * @author u7902000 Gea Linggar
     */
    private void stopShowDataRefresh() {
        if (handler != null && fetchTask != null) {
            handler.removeCallbacks(fetchTask);
        }
    }

    /**
     * Handles the comparison button click event by displaying and comparing air quality data.
     * <p>
     * This method makes the necessary UI components visible (such as the line chart, interval spinner,
     * and comparison suburb spinner) and fetches the air quality records for both the selected and compared suburbs.
     * </p>
     * <p>
     * The method first fetches and plots the primary suburb's data using {@link #fetchRecordsForSuburbAndInterval(String)}
     * and {@link #plotPrimaryData(List, AirQualityMetric)}. If a comparison suburb is selected, it also fetches the comparison
     * data and plots it on the chart using {@link #plotComparisonData(List, AirQualityMetric)}.
     * </p>
     * @author u7902000 Gea Linggar
     */
    private void comparisonOnClick() {
        lineChart.setVisibility(View.VISIBLE);
        intervalSpinner.setVisibility(View.VISIBLE);
        comparingSpinner.setVisibility(View.VISIBLE);
        primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);
        plotPrimaryData(primarySuburbs, metricOption);
        Log.d("Comparing debug", "Primary records hashcode: " + primarySuburbs);

        if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
            comparedSuburbs = fetchRecordsForSuburbAndInterval(comparedSuburb);
            plotComparisonData(comparedSuburbs, metricOption);
            Log.d("Comparing debug", "Comparison records hashcode: " + comparedSuburbs);
        }
    }

    /**
     * Initializes the click listeners for various UI components in the Suburb Live Activity.
     *
     * @author u7902000 Gea Linggar
     */
    private void clickListener() {
        resultTextViewLive.setOnClickListener(v -> {
            metricOption = AirQualityMetric.AQI;
            comparisonOnClick();
        });
        coTextView.setOnClickListener(v -> {
            metricOption = AirQualityMetric.CO;
            comparisonOnClick();
        });
        no2TextView.setOnClickListener(v -> {
            metricOption = AirQualityMetric.NO2;
            comparisonOnClick();
        });
        pm25TextView.setOnClickListener(v -> {
            metricOption = AirQualityMetric.PM2_5;
            comparisonOnClick();
        });
        pm10TextView.setOnClickListener(v -> {
            metricOption = AirQualityMetric.PM10;
            comparisonOnClick();
        });
        o3TextView.setOnClickListener(v -> {
            metricOption = AirQualityMetric.O3;
            comparisonOnClick();
        });
        so2TexView.setOnClickListener(v -> {
            metricOption = AirQualityMetric.SO2;
            comparisonOnClick();
        });

        //reset the text when clicked
        suburbSpinnerLive.setOnClickListener(v -> suburbSpinnerLive.setText(""));
        comparingSpinner.setOnClickListener(v -> comparingSpinner.setText(""));

        findViewById(R.id.historicalPageButton).setOnClickListener(v -> {
            Intent suburbHistoricalIntent = new Intent(getApplicationContext(), SuburbHistoricalActivity.class);
            suburbHistoricalIntent.putExtra("displayName", email);
            startActivity(suburbHistoricalIntent);
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            Intent profileIntent = new Intent(SuburbLiveActivity.this, ProfileActivity.class);
            profileIntent.putExtra("displayName", email);
            startActivity(profileIntent);
        });
    }

    /**
     * Fetches air quality records for the selected suburb and a specific time interval.
     * <p>
     * This method calculates the time range based on the selected interval option
     * (e.g., Today, Yesterday, Last Week) and formats the start and end dates. It then
     * filters the records for the given suburb within the specified date range by calling
     * {@link #filterRecordsBySuburbAndTimestamp(AVLTree, String, String, String)}.
     * </p>
     *
     * @param selectedSuburb the name of the suburb to fetch records for.
     * @return a list of {@link AirQualityRecord} instances that match the suburb and the selected time range.
     *
     * @author u7902000 Gea Linggar
     */
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

    /**
     * Initializes the suburb selection spinner and configures its behavior.
     * <p>
     * When a suburb is selected, the method triggers data fetching for that suburb and displays
     * the fetched data. The UI is updated on the main thread after fetching is complete.
     * </p>
     */
    private void selectSuburbSpinner() {
        // Create a list of suburbs
        List<String> suburbsList = new ArrayList<>();
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
                    showDataAndRefresh();
                    primarySuburbs = fetchRecordsForSuburbAndInterval(selectedSuburb);

                    // Update UI on the main thread after data is fetched
                    runOnUiThread(() -> plotPrimaryData(primarySuburbs, metricOption));
                    Log.d("Comparing debug", "Primary location: " + selectedSuburb + primarySuburbs);
                    Log.d("Comparing debug", "Comparing location: " + comparedSuburb + comparedSuburbs);
                    comparedSuburb = "";
                    Log.d("Comparing debug", "primary location: " + selectedSuburb);
                });
            } else {
                resultTextViewLive.setText(R.string.no_suburb_selected);
            }
        });

        suburbSpinnerLive.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER))) {

                String query = suburbSpinnerLive.getText().toString();
                search(query);


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
    }

    /**
     * Initializes the comparing suburb spinner and configures its behavior.
     * <p>
     * This method sets up an {@link AutoCompleteTextView} (comparingSpinner) to allow users
     * to search for and select a suburb from the list of available suburbs. It handles
     * user selection of a suburb and triggers the comparison process.
     * </p>
     * <p>
     * When a suburb is selected from the comparingSpinner, the method fetches air quality
     * records for the selected suburb and displays the comparison data alongside the primary suburb.
     * </p>
     * @author u7902000 Gea linggar
     */
    private void selectComparingSpinner() {
        // Create a list of suburbs and add the default entry as the first item
        List<String> suburbsList = new ArrayList<>(); // Why global variables?!?!
        suburbsList.addAll(suburbMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suburbsList);
        comparingSpinner.setAdapter(adapter);
        comparingSpinner.setThreshold(1);
        comparingSpinner.setOnItemClickListener((parent, view, position, id) -> {
            comparedSuburb = (String) parent.getItemAtPosition(position);
            if (comparedSuburb != null && !comparedSuburb.isEmpty()) {
                // Fetch data for the second suburb]
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

    /**
     * Filters records in an AVL tree based on the selected suburb and a specified timestamp range.
     * <p>
     * This method traverses the AVL tree and filters air quality records by matching the suburb name
     * and ensuring that the record's timestamp falls within the provided start and end timestamps.
     * Records are added to the result list if they meet these criteria.
     * </p>
     *
     * @param records          The AVL tree containing air quality records with a key format of "suburb_timestamp".
     * @param selectedSuburb   The name of the suburb to filter records for.
     * @param startTimestamp   The start of the timestamp range in the format "yyyy-MM-dd HH:mm:ss".
     * @param endTimestamp     The end of the timestamp range in the format "yyyy-MM-dd HH:mm:ss".
     * @return                 A list of {@link AirQualityRecord} objects filtered by the specified suburb and timestamp range.
     * @author u7902000 Gea Linggar
     */
    public List<AirQualityRecord> filterRecordsBySuburbAndTimestamp(AVLTree<String, AirQualityRecord> records,
                                                                    String selectedSuburb, String startTimestamp, String endTimestamp) {
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
        return recordsInSelectedSuburb;
    }

    /**
     * Loads a map of suburb names and their corresponding geographical coordinates from the
     * json file located in the assets folder.
     *
     * @return a {@code HashMap<String, double[]>} where each key is a suburb name, and each value
     *         is an array containing the latitude and longitude of the suburb.
     * @throws RuntimeException if there is an error loading or parsing the JSON file.
     */
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
    /**
     * Parses the provided JSON response to extract air quality data and update various UI elements
     * including text views and progress bars, as well as a semi-circular arc progress bar for AQI (Air Quality Index).
     * Implements semi-circular bar from @link(<a href="https://github.com/hadibtf/SemiCircleArcProgressBar">...</a>).
     *
     * @param jsonResponse the JSON string containing air quality data, which includes pollutants like CO, NO2, PM2.5, PM10, O3, and SO2.
     *
     * @ensures the text views and progress bars are updated with the correct air quality data, and the AQI value is displayed
     *          with the appropriate progress bar color and status text (low, moderate, or high).
     * @requires jsonResponse is a valid JSON string that includes the necessary air quality components.
     *
     * @author Gea,Hexuan(Shawn)
     */
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


    /**
     * Plots the primary air quality data on the line chart.
     * <p>
     * The X-axis labels are set based on the time values extracted from the records.
     * </p>
     *
     * @param data   the list of {@link AirQualityRecord} containing air quality data to plot
     * @param metric the selected {@link AirQualityMetric} (e.g., AQI, CO, etc.) for plotting
     * @author u7902000
     */
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

    /**
     * Plots the comparing air quality data on the line chart.
     * <p>
     * The X-axis labels are set based on the time values extracted from the records.
     * </p>
     *
     * @param data   the list of {@link AirQualityRecord} containing air quality data to plot
     * @param metric the selected {@link AirQualityMetric} (e.g., AQI, CO, etc.) for plotting
     * @author u7902000 Gea Linggar
     */
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

    /**
     * Helper method for plotting data
     * Extracts the metric value (e.g., AQI, CO, etc.) from an air quality record.
     *
     * @param record the {@link AirQualityRecord} from which to extract the metric value
     * @param metric the selected {@link AirQualityMetric} to retrieve the value for
     * @return the value of the selected metric for the given air quality record
     * @author Gea Linggar
     */
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

    /**
     * Helper method for plotting data
     * Creates a new {@link LineDataSet} for the chart.
     *
     * @param entries the list of {@link Entry} data points for the dataset
     * @param label   the label for the dataset
     * @param color   the color for the dataset's line and fill
     * @return a {@link LineDataSet} for the chart
     * @author Gea Linggar
     */
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

    /**
     * Helper method for plotting
     * Sets the labels for the X-axis of the chart based on the time values.
     *
     * @param hourIndex the list of hour values (in "HH:mm" format) to set as X-axis labels
     * @author Gea Linggar
     */
    private void setXAxisLabels(ArrayList<String> hourIndex) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(hourIndex));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
    }
    /**
     * Define the degree of air quality progress bars and their colors
     *
     * @author Hexuan(Shawn)
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

    protected void onPause() {
        super.onPause();
        stopShowDataRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopShowDataRefresh();
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
        Location l = MainActivity.gpsService.getRecentLocation();
        resultDataset = nearestSuburbStrategy.getNearestSuburbList(l.getLatitude(), l.getLongitude(), newMap);

        // Sort
        resultDataset.sort(Comparator.comparingDouble(o ->
                Double.parseDouble(Objects.requireNonNull(o.get("distance")))));
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