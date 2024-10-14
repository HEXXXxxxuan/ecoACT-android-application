package com.go4.utils.design_pattern;

import java.util.HashMap;

/**
 * This interface is provide abstraction in determining the nearest suburb,
 * potentially using different algorithms or distance metrics depending on the implementation.
 * @author u7902000 Gea Linggar
 */
public interface LocationStrategy {
    String getNearestSuburb(double userLatitude, double userLongitude, HashMap<String, double[]> suburbMap);
}
