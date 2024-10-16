package com.go4.utils;

import static com.go4.application.MainActivity.CHANNEL_ID;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.go4.application.R;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

public class GPSService extends Service {
    private final IBinder binder = new LocalBinder();
    private FusedLocationProviderClient locationClient;
    private LocationRequest request;
    private LocationCallback locationCallback;
    private static Location latestLocation;
    private static Location previousLocation = null;
    private NotificationManager notificationManager;

    public class LocalBinder extends Binder {
        public GPSService getService(){
            return GPSService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Debugging", "onBind reached");
        return binder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        request = createLocationRequest();
        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("Debugging", "Location received");
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    locationNotification("Location not available");
                    return;
                }

                latestLocation = locationResult.getLastLocation();
                Log.d("Debugging", "Lat: " + latestLocation.getLatitude() + ", Long: " + latestLocation.getLongitude());


                if (previousLocation != null && latestLocation.distanceTo(previousLocation) < 1) {
                    Log.d("Debugging", "Location unchanged, not updating");
                } else {
                    previousLocation = latestLocation;
                    locationNotification("Updated Location: Lat = " + latestLocation.getLatitude() + ", Long = " + latestLocation.getLongitude());
                }
            }
        };

        startLocationUpdates();
    }

    protected LocationRequest createLocationRequest() {
        return new LocationRequest.Builder(
                LocationRequest.PRIORITY_HIGH_ACCURACY, 5000)
                .build();
    }

    private void locationNotification(String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Results")
                .setContentText(text)
                .setSmallIcon(R.drawable.gp_logo)
                .setAutoCancel(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(0, builder.build());
    }

    @Override
    public void onDestroy() {
        locationClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Debugging", "Not enough permissions, requesting");
            return;
        }
        locationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    public Location getRecentLocation() {
        if (latestLocation == null) {
            Log.d("Debugging", "No stored latestLocation, giving default location (Ainslie)");
            Location location = new Location("");
            location.setLatitude(-35.27944d);
            location.setLongitude(149.11944d);
            previousLocation = location;
            return location;
        }
        return latestLocation;
    }
}