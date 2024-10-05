package com.go4.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.widget.ProgressBar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

import com.go4.utils.DataFetcher;

@SuppressLint("CustomSplashScreen")


public class SplashActivity extends AppCompatActivity {

    static ExecutorService executorService = Executors.newFixedThreadPool(6);
    static Handler mainHandler = new Handler(Looper.getMainLooper());
    ProgressBar fetchingBar;
    DataFetcher dataFetcher;
    private String apiKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fetchingBar = findViewById(R.id.fetchingProgressBar);
        fetchingBar.setMax(113); //there are 113 suburbs!!!

        apiKey = "4f6d63b7d7512fc4b14ee2aeb89d3128";


        //Fetching historical data and parse it to CSV and AVLTree
        DataFetcher dataFetcher = new DataFetcher(executorService, mainHandler, 1, apiKey);
        dataFetcher.automaticAddRecords(this, "historical_data.csv", fetchingBar, this::goToNextActivity);
    }


    private void goToNextActivity(){;
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
