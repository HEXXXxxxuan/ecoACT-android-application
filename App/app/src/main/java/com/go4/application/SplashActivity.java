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

@SuppressLint("CustomSplashScreen")


public class SplashActivity extends AppCompatActivity {

    static ExecutorService executorService = Executors.newFixedThreadPool(6);
    static Handler mainHandler = new Handler(Looper.getMainLooper());
    ProgressBar fetchingBar;
    DataFetcher dataFetcher;
    private String apiKey;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fetchingBar = findViewById(R.id.fetchingProgressBar);
        fetchingBar.setMax(113); //there are 113 suburbs!!!

        apiKey = "4f6d63b7d7512fc4b14ee2aeb89d3128";

        // Check for location permissions before proceeding
        checkLocationPermission();

    }


    private void goToNextActivity(){;
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchData();
        }
    }

    private void fetchData() {
        DataFetcher dataFetcher = new DataFetcher(executorService, mainHandler, 7, apiKey);
        dataFetcher.automaticAddRecords(this, "historical_data.csv", fetchingBar, this::goToNextActivity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with data fetching
                fetchData();
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Location permission is required to proceed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
