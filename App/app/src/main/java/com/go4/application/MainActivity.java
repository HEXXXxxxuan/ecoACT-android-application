package com.go4.application;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import androidx.core.content.ContextCompat;
import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.profile.ProfileActivity;
import com.go4.utils.GPSService;
import com.go4.utils.GPSService.LocalBinder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activity intended to handle backend operations
 */
public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "main";
    public static boolean bound;
    public static GPSService gpsService;

    /**
     * Implements {@link com.go4.application.FirebaseLoginActivity.FirebaseLoginActivityResultContract}
     * into an {@link ActivityResultLauncher<Void>} to provide type safety for user returns.
     * <p>On success, it launches {@link ProfileActivity} with the resulting user.</p>
     *
     * @author Ryan Foote
     */
    private final ActivityResultLauncher<Void> getUser = registerForActivityResult(
        new FirebaseLoginActivity.FirebaseLoginActivityResultContract(), result->{
            if(result != null){
                Intent profile = new Intent(this, ProfileActivity.class);
                profile.putExtra("User", result);
                startActivity(profile);
            }
            else{
                Log.e("Login", "GUAH NO USER");
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_suburb_live) {
                startActivity(new Intent(MainActivity.this, SuburbLiveActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        startGps();
        checkUserLogin();
    }

    @Override
    // unbinds gpsService to avoid memory leakage
    protected void onDestroy(){
        if (bound) {
            unbindService(connection);
            bound = false;
        }
        super.onDestroy();
    }

    /**
     * Stylistic wrapper for {@link #getUser}.
     * literally calls: <p>"<code>getUser.launch(null)</code>"</p>
     *
     * @author Ryan Foote
     */
    private void checkUserLogin() {
        getUser.launch(null);
    }

    /**
     * Checks for {@link Manifest.permission#ACCESS_BACKGROUND_LOCATION} before
     * calling {@link #bindService}
     *
     * @author Ryan Foote
     */
    private void startGps(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(this, GPSService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
        else{
            Log.e("Permissions", "No background location access");
        }
    }

    /**
     * Utilizing {@link ServiceConnection}, effectively binds the gpsService.
     *
     * @author Ryan Foote
     */
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocalBinder binder = (LocalBinder) iBinder;
            gpsService = binder.getService();
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    /**
     * Creates a {@link NotificationChannel} for the associated {@link NotificationManager}.
     * <p>Allows for notifications to be sent via {@link #CHANNEL_ID}</p>
     *
     * @author Ryan Foote
     */
    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
