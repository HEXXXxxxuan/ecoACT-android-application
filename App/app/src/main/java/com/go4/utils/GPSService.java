package com.go4.utils;

import static com.go4.application.MainActivity.CHANNEL_ID;
import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Extends {@link Service} to provide an API interface to retrieve location data from.
 * <p>Intended as a bound-service through an implementation of
 * {@link android.content.ServiceConnection} where <code>gps = binder.getService()</code></p>
 * <p>Retrieve location with {@link #getRecentLocation()}</p>
 *
 * @author Ryan Foote
 */
public class GPSService extends Service {
    private final IBinder binder = new LocalBinder();
    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private Location latestLocation;
    private NotificationManager notificationManager;

    /**
     * Extends {@link Binder} with a single method {@link #getService()} to return the surrounding
     * {@link GPSService} instance.
     *
     * @author Ryan Foote
     */
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
        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    locationNotification("Location not available");
                }
                else {
                    latestLocation = locationResult.getLastLocation();
                    locationNotification("Lat: " + latestLocation.getLatitude() +", Long: " + latestLocation.getLongitude());
                }
            }};
        LocationRequest request = new LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 5000).build();
        startLocationUpdates(request, locationCallback);
    }

    /**
     * Helper for {@link NotificationCompat.Builder}
     * @param text for {@link NotificationCompat.Builder#setContentText}
     *
     * @author Ryan Foote
     */
    private void locationNotification(String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Retrieved")
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

    private void startLocationUpdates(LocationRequest request, LocationCallback locationCallback) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Debugging", "Not enough permissions");
            return;
        }
        locationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    /**
     * <strong>Returns a mock location (Ainslie) if no location has been retrieved yet.</strong>
     * @return {@link #latestLocation}
     *
     * @author Ryan Foote
     */
    public Location getRecentLocation() {
        if (latestLocation == null) {
            Log.d("Debugging", "latest location is null, returning mock location (Ainslie)");
            Location location = new Location("");
            location.setLatitude(-35.27944d);
            location.setLongitude(149.11944d);
            return location;
        }
        return latestLocation;
    }
}