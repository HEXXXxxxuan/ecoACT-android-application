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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    private static final String[] permissionsStr = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS};

    ActivityResultLauncher<String[]> permissionsLauncher = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            if(result.containsValue(false)){
                Toast.makeText(this, "All permissions are required for function", Toast.LENGTH_SHORT).show();
                Log.d("Debugging", result.keySet().toString());
                checkPermissions();
            }
            else{
                startNextActivity();
            }
        });

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
        List<String> toRequest = new ArrayList<>();
        for(String str : permissionsStr){
            if(!hasPermissions(str)){
                toRequest.add(str);
            }
        }
        String[] real = new String[toRequest.size()];
        for (int i = 0; i < toRequest.size(); i++) {
            real[i] = toRequest.get(i);
        }
        permissionsLauncher.launch(real);
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