package com.go4.application.live_data;

import android.location.Location;

import com.go4.utils.design_pattern.LocationStrategy;

import java.util.HashMap;
import java.util.List;
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

    public List<Map<String, String>> getNearestSuburbList(double userLatitude, double userLongitude, HashMap<String, double[]> suburbMap) {
        List<Map<String, String>> result_list = new ArrayList<>();

        for (Map.Entry<String, double[]> entry : suburbMap.entrySet()){
            String suburb = entry.getKey();
            double[] coordinates = entry.getValue();

            double distance = calculateDistanceKM(userLatitude, userLatitude, coordinates[0], coordinates[1] );

            Map<String, String> map = new HashMap<>();
            map.put("title", suburb);
            map.put("position", coordinates[0] + " " + coordinates[1]);
            map.put("distance", String.format("%.1f", distance));
            map.put("other", "");
            result_list.add(map);
        }

        return result_list;
    }

    private static final double EARTH_RADIUS = 6371.0;
    public static double calculateDistanceKM(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);


        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
