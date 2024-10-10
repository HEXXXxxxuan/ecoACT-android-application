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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.go4.utils.DataFetcher;

/**
 * This activity launches the splashScreen on app launch.
 * <p>It also implements logic required during startup. Presently loading all historical data.</p>
 * @author Shawn ?
 * @author Gea
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int GET_BACKGROUND_AFTER = 2000;
    private static final int START_ACTIVITY_AFTER = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ProgressBar fetchingBar = findViewById(R.id.fetchingProgressBar);
        fetchingBar.setMax(113); // only 113 suburbs
        fetchData();
    }

    /**
     * <h1><strong>DOES NOT CHECK REQUIRED PERMISSIONS ARE GRANTED</strong></h1>
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
     * Calls {@link #startNextActivity()} upon finishing
     * <p>
     *     Uses an {@link ActivityResultLauncher} to request permissions and does logic in its callback
     * </p>
     */
    private void checkPermissions(){
        ArrayList<String> permissions = new ArrayList<>();
        if(!hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(!hasPermissions(Manifest.permission.POST_NOTIFICATIONS)){
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        if(!hasPermissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        Log.d("Permissions", permissions.toString());
        if(permissions.isEmpty()){
            startNextActivity();
        }
        else if(permissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && permissions.size()>1){
            permissions.remove(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            String[] req = new String[permissions.size()];
            for (int i = 0; i < permissions.size(); i++) {
                req[i] = permissions.get(i);
            }
            requestPermissions(req, GET_BACKGROUND_AFTER);
        }
        else {
            String[] req = new String[permissions.size()];
            for (int i = 0; i < permissions.size(); i++) {
                req[i] = permissions.get(i);
            }
            requestPermissions(req,START_ACTIVITY_AFTER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GET_BACKGROUND_AFTER){
            Log.d("Permissions", "GetBackghroundAFter");
            checkPermissions();
        }
        if(requestCode==START_ACTIVITY_AFTER){
            checkPermissions();
        }
    }

    private boolean hasPermissions(String perm){
        return ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * <h1><strong>CALLS {@link #startNextActivity()} ONCE FINISHED</strong></h1>
     * <p>Fetches historical data records using {@link DataFetcher}</p>
     * @author Gea
     */
    private void fetchData() {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        DataFetcher dataFetcher = new DataFetcher(executorService, mainHandler, 7);
        // dataFetcher.automaticAddRecords(this, "historical_data.csv", fetchingBar, this::checkPermissions);
        checkPermissions();
    }
}