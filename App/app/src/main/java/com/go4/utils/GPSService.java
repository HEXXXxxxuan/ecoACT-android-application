package com.go4.utils;

import android.app.Notification;
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

import com.go4.application.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Comparator;

public class GPSService extends Service {
    private final IBinder binder = new LocalBinder();
    private FusedLocationProviderClient locationClient;
    private LocationRequest request;
    private LocationCallback locationCallback;
    private Location latestLocation;
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
        request = new LocationRequest.Builder(5000).build();
        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult){
                Log.d("Debugging", "Location received");
                if(locationResult==null){
                    Notification notification = new Notification.Builder(getApplicationContext())
                            .setContentTitle("YOU DON'T EXIST")
                            .setContentText("YOU DON'T HAVE A LOCATION; YOU AREN'T REAL")
                            .setSmallIcon(R.drawable.gp_logo)
                            .setAutoCancel(true)
                            .build();
                    notificationManager.notify(0, notification);
                    return;
                }
                locationResult.getLocations().stream()
                        .min(Comparator.comparingInt(x -> (int) x.getTime()))
                        .ifPresent(x -> latestLocation = x);
                //debug location
                if (latestLocation != null) {
                    Log.d("Location Debug", "Latitude: " + latestLocation.getLatitude() + ", Longitude: " + latestLocation.getLongitude());
                }

                Notification notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle("YOU EXIST")
                        .setContentText("YOU ARE LITERALLY HERE: " + latestLocation.getLatitude() + ", " + latestLocation.getLongitude())
                        .setSmallIcon(R.drawable.gp_logo)
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(0, notification);
            }
        };
        startLocationUpdates();
    }

    @Override
    public void onDestroy(){
        locationClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Debugging", "Not enough perms");
            return;
        }
        locationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    public Location getRecentLocation(){
        if(latestLocation==null){
            Log.d("Debugging", "No stored latestLocation, giving fake (Ainslie)");
            Location location = new Location("");
            location.setLatitude(-35.27944d);
            location.setLongitude(149.11944d);
            Log.d("Debugging", "Location: " + location);
            return location;
        }
        return latestLocation;
    }

}
