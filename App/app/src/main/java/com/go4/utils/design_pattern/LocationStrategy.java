package com.go4.utils.design_pattern;

import java.util.HashMap;

public interface LocationStrategy {
    String getNearestSuburb(double userLatitude, double userLongitude, HashMap<String, double[]> suburbMap);
}
