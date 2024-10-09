package com.go4.application;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.go4.application.live_data.SuburbLiveActivity;
import com.go4.application.profile.ProfileActivity;
import com.go4.utils.GPSService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.go4.utils.GPSService.LocalBinder;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "main";
    public FirebaseUser user;
    private FirebaseAuth mAuth;
    public static GPSService gpsService;
    public static boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        gpsService = new GPSService();
        setContentView(R.layout.activity_main);
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
        firebaseLogin();
        createNotificationChannel();
    }

    @Override
    protected void onDestroy(){
        unbindService(connection);
        bound = false;
        super.onDestroy();
    }

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

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void firebaseLogin(){
        user = mAuth.getCurrentUser();
        if(user!=null){
            // Already logged-in user, return to app flow
            Toast.makeText(this, "Already logged in as " + user.getEmail(), Toast.LENGTH_LONG).show();
        }
        else {
            signIn();
        }
    }

    private void signIn() {
        setContentView(R.layout.activity_firebase_login_ui);
        Button loginSubmit = findViewById(R.id.bt_login);
        loginSubmit.setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.lg_username)).getText().toString();
            String pass = ((EditText)findViewById(R.id.lg_password)).getText().toString();
            mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(task -> {
                    FirebaseUser user = task.getUser();
                    assert user != null;
                    Toast.makeText(this, "Successful login as " + user.getEmail(), Toast.LENGTH_LONG).show();
                    Log.d("Debugging", "User: " + user.getEmail());
                    Intent intent = new Intent(this, GPSService.class);
                    bindService(intent, connection, Context.BIND_AUTO_CREATE);
                    Intent profileIntent = new Intent(this, ProfileActivity.class);
                    profileIntent.putExtra("displayName", user.getEmail());
                    startActivity(profileIntent);
                })
                .addOnFailureListener(task -> Toast.makeText(this, task.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}
