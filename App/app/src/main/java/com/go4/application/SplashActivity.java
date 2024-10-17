package com.go4.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.Manifest;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.go4.utils.DataFetcher;
import com.go4.utils.design_pattern.ExecutorServiceSingleton;

/**
 * Models the Splash Screen on app-startup.
 * <p>Fetches data using {@link DataFetcher}</p>
 *
 * @author u8006862 Shawn(Hexuan), u7902000 Gea, u7327620 Ryan Foote
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int GET_BACKGROUND_AFTER = 2000;
    private static final int START_ACTIVITY_AFTER = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ((ProgressBar) findViewById(R.id.fetchingProgressBar)).setMax(113);
        fetchData();
    }

    /**
     * <h1><strong>DOES NOT CHECK REQUIRED PERMISSIONS ARE GRANTED</strong></h1>
     * <p>Closes this {@link SplashActivity} and starts {@link MainActivity}.</p>
     *
     * @author u7327620 Ryan Foote
     */
    private void startNextActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Calls {@link #startNextActivity()} upon finishing.
     * <p>Callback Logic implemented by {@link #onRequestPermissionsResult(int, String[], int[])}.</p>
     * <p>Requests location permissions and {@link Manifest.permission#ACCESS_BACKGROUND_LOCATION}
     * separately as per android docs</p>
     *
     * @author u7902000 Gea, u7327620 Ryan Foote
     */
    private void checkPermissions(){
        ArrayList<String> permissions = new ArrayList<>();
        if(!hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(!hasPermissions(Manifest.permission.POST_NOTIFICATIONS)){
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        if(!hasPermissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        if(permissions.isEmpty()){
            startNextActivity();
        }
        else if(permissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && permissions.size()>1){
            permissions.remove(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            String[] req = new String[permissions.size()];
            permissions.toArray(req);
            Log.d("Debug", "Requesting: " + Arrays.toString(req));
            ActivityCompat.requestPermissions(this, req, GET_BACKGROUND_AFTER);
        }
        else {
            String[] req = new String[permissions.size()];
            permissions.toArray(req);
            Log.d("Debug", "Requesting: " + Arrays.toString(req));
            ActivityCompat.requestPermissions(this, req, START_ACTIVITY_AFTER);
        }
    }

    /**
     * Override to handle {@link #GET_BACKGROUND_AFTER} and {@link #START_ACTIVITY_AFTER}
     * requestCodes from {@link #checkPermissions()}.
     * @param requestCode The request code passed in {@link ActivityCompat#requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     * @author u7327620 Ryan Foote
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GET_BACKGROUND_AFTER){
            Log.d("Permissions", "GetBackgroundAfter");
            checkPermissions();
        }
        if(requestCode == START_ACTIVITY_AFTER){
            checkPermissions();
        }
    }

    /**
     * Stylistic wrapper for {@link ContextCompat#checkSelfPermission(Context, String)}
     * @param perm permissions to check
     * @return <code>== {@link PackageManager#PERMISSION_GRANTED}</code>
     *
     * @author u7327620 Ryan Foote
     */
    private boolean hasPermissions(String perm){
        return ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * <h1><strong>CALLS {@link #startNextActivity()} ONCE FINISHED</strong></h1>
     * <p>Fetches historical data records through {@link DataFetcher}</p>
     *
     * @author Gea
     */
    private void fetchData() {
        ExecutorService executorService = ExecutorServiceSingleton.getInstance();
        Handler mainHandler = new Handler(Looper.getMainLooper());
        TextView statusText = findViewById(R.id.statusTextView);
        ProgressBar progressBar = findViewById(R.id.fetchingProgressBar);

        // Provides text updates and loading bar whilst fetching data.
        statusText.setText("Hang tight! We're collecting info from the OpenWeather API.");
        DataFetcher dataFetcher = new DataFetcher(executorService, mainHandler, 7);
        dataFetcher.automaticAddRecords(this, "historical_data.csv", progressBar, () -> {
            statusText.setText("Data fetched. Phew!");
            mainHandler.postDelayed(() -> {
                Log.d("DelayedExecution", "Delay finished, calling checkPermissions()...");
                checkPermissions();
            }, 1000);
        });
    }
}