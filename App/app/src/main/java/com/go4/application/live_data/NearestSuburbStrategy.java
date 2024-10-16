package com.go4.application.live_data;

import android.util.Log;

import com.go4.utils.design_pattern.LocationStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The  class provide functionality for finding the nearest suburb based on the user's geographical location.
 * This class calculates the distance between the user's coordinates and the coordinates
 * of various suburbs to determine the nearest suburb.
 *
 * @author u7902000 Gea Linggar
 * @author u7635535 Zechuan Liu
 */
public class NearestSuburbStrategy implements LocationStrategy {

    /**
     * Finds the nearest suburb to the user based on the user's latitude and longitude.
     * The suburb's coordinates are compared to the user's coordinates, and the one with the
     * shortest distance is returned.
     *
     * @param userLatitude  the latitude of the user's current location
     * @param userLongitude the longitude of the user's current location
     * @param suburbMap     a map where the key is the suburb name and the value is an array
     *                      containing the suburb's latitude and longitude
     * @return the name of the nearest suburb
     * @author u7902000 Gea Linggar
     */
    @Override
    public String getNearestSuburb(double userLatitude, double userLongitude, HashMap<String, double[]> suburbMap) {
        String nearestSuburb = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Map.Entry<String, double[]> entry : suburbMap.entrySet()) {
            String suburb = entry.getKey();
            double[] coordinates = entry.getValue();

            
            
            // Using the Haversine formula to calculate distance
            double distance = calculateDistanceKM(userLatitude, userLongitude, coordinates[0], coordinates[1]);

            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestSuburb = suburb;
            }
        }

        return nearestSuburb;
    }

    /**
     * Calculates the distance between two geographical coordinates using the Haversine formula.
     *
     * @param lat1 the latitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the latitude of the second point
     * @param lon2 the longitude of the second point
     * @return the distance between the two points in kilometers
     * @author u7902000 Gea Linggar
     * @author u7635535 Zechuan Liu
     */
    private static final double EARTH_RADIUS = 6371.0; // Earth radius in kilometers
    private double calculateDistanceKM(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Apply the Haversine formula
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c; // Returns the distance in kilometers
    }

    public List<Map<String, String>> getNearestSuburbList(double userLatitude, double userLongitude, HashMap<String, double[]> suburbMap) {
        List<Map<String, String>> result_list = new ArrayList<>();

        for (Map.Entry<String, double[]> entry : suburbMap.entrySet()) {
            String suburb = entry.getKey();
            double[] coordinates = entry.getValue();

            // Using the Haversine formula to calculate distance
            double distance = calculateDistanceKM(userLatitude, userLongitude, coordinates[0], coordinates[1]);

            Map<String, String> map = new HashMap<>();
            map.put("title", suburb);
            map.put("position", coordinates[0] + " " + coordinates[1]);
            map.put("distance", String.format("%.1f", distance));
            map.put("other", "");
            result_list.add(map);
        }

        return result_list;
    }
}

