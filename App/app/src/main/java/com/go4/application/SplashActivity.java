package com.go4.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.Manifest;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.go4.utils.DataFetcher;

/**
 * This activity launches the splashScreen on app launch.
 * <p>
 *     It also implements logic required during startup. Presently loading all historical data.
 * </p>
 * @author Shawn ?
 * @author Gea
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private ProgressBar fetchingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        fetchingBar = findViewById(R.id.fetchingProgressBar);
        fetchingBar.setMax(113); // only 113 suburbs
        checkLocationPermission();
        fetchData();
    }

    /**
     * Called to close {@link SplashActivity} and resume to next class
     * <p>
     *     Changing throughout development but should be {@link MainActivity} next
     * </p>
     */
    private void startNextActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Fetches historical data records using {@link DataFetcher}
     *
     * @author Gea
     */
    private void fetchData() {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        DataFetcher dataFetcher = new DataFetcher(executorService, mainHandler, 7);
        dataFetcher.automaticAddRecords(this, "historical_data.csv", fetchingBar, this::startNextActivity);
    }

    /**
     * Handles requested permissions for the {@link #checkLocationPermission()} method
     * @param requestCode The request code passed in
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchData();
                return;
            }
            // Fall-through when non-successful
            Toast.makeText(this, "Location permission is required to proceed.", Toast.LENGTH_SHORT).show();
            try {
                wait(2000);
            }
            catch (InterruptedException e) {
                Log.d("Debugging", "InterruptedException during onRequestPermissionResult wait");
                throw new RuntimeException(e);
            }
            checkLocationPermission();
        }
    }

    /**
     * Requests ACCESS_FINE_LOCATION as well as ACCESS_BACKGROUND_LOCATION
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}