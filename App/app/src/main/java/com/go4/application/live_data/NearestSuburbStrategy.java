package com.go4.application.live_data;

import com.go4.utils.design_pattern.LocationStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The {@code NearestSuburbStrategy} class implements the {@link LocationStrategy} interface
 * to provide functionality for finding the nearest suburb based on the user's geographical location.
 * <p>
 * This class calculates the distance between the user's coordinates and the coordinates
 * of various suburbs to determine the nearest suburb. Additionally, it can provide a list of
 * suburbs along with their positions and distances from the user's location.
 * </p>
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

        for (Map.Entry<String, double[]> entry : suburbMap.entrySet()){
            String suburb = entry.getKey();
            double[] coordinates = entry.getValue();

            double distance = calculateDistance(userLatitude, userLongitude, coordinates[0], coordinates[1] );

            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestSuburb = suburb;
            }
        }

        return nearestSuburb;
    }

    /**
     * Calculates the distance between two geographical coordinates using the Euclidean distance formula.
     *
     * @param lat1 the latitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the latitude of the second point
     * @param lon2 the longitude of the second point
     * @return the distance between the two points
     * @author u7902000 Gea Linggar
     */
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

            double distance = calculateDistance(userLatitude, userLatitude, coordinates[0], coordinates[1] );

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

