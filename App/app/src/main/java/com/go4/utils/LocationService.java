package com.go4.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationService {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private LocationListener listener;

    public LocationService(Context context, LocationListener listener){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    public void forceLocationUpdate() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 0
        ).setMaxUpdates(1)
                .build();

        Log.d("Location DEBUG", "Forced calling location fused location");


        Log.d("Location DEBUG", "Flushing old locations");
        fusedLocationClient.flushLocations();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("Location DEBUG", "No location result received.");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLocation = location;
                        Log.d("Location DEBUG", "Location detected: " + location.getLatitude() + ", " + location.getLongitude());
                        if (listener != null) {
                            listener.onLocationUpdated(location);
                        }
                    }
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        Log.d("Location DEBUG", "requestLocationUpdates called");

    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 9000000
        ).setMinUpdateIntervalMillis(60000)
                .setMaxUpdateDelayMillis(1800000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("Location DEBUG", "No location result received.");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLocation = location;
                        Log.d("Location DEBUG", "Location detected: " + location.getLatitude() + ", " + location.getLongitude());
                    }
                    if (listener != null) {
                        listener.onLocationUpdated(location);
                    }
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public interface LocationListener {
        void onLocationUpdated(Location location);
    }
}

