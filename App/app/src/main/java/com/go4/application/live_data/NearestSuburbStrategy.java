package com.go4.application.live_data;

import android.location.Location;

import com.go4.utils.design_pattern.LocationStrategy;

import java.util.HashMap;
import java.util.Map;



public class NearestSuburbStrategy implements LocationStrategy {

    @Override
    public String getNearestSuburb(double userLatitude, double userLongitude, HashMap<String, double[]> suburbMap) {
        String nearestSuburb = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Map.Entry<String, double[]> entry : suburbMap.entrySet()){
            String suburb = entry.getKey();
            double[] coordinates = entry.getValue();

            double distance = calculateDistance(userLatitude, userLatitude, coordinates[0], coordinates[1] );

            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestSuburb = suburb;
            }
        }

        return nearestSuburb;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }
}
