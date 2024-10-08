package com.go4.application.model;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AirQualityRecord {
    private String location;  // Suburb name
    private double aqi;  // Air Quality Index
    private double co;  // Carbon Monoxide
    private double no2;  // Nitrogen Dioxide
    private double o3;  // Ozone
    private double so2;  // Sulfur Dioxide
    private double pm2_5;  // Particulate Matter 2.5
    private double pm10;  // Particulate Matter 10
    private double nh3;  // Ammonia
    private String timestamp;  // Human-readable timestamp

    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Constructor
    public AirQualityRecord(String location, double aqi, double co, double no2, double o3, double so2, double pm2_5, double pm10, double nh3, long unixTimestamp) {
        this.location = location;
        this.aqi = aqi;
        this.co = co;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
        this.nh3 = nh3;
        this.timestamp = TIMESTAMP_FORMAT.format(new Date(unixTimestamp * 1000L));
    }

    // Getters
    public String getLocation() {
        return location;
    }

    public double getAqi() {
        return aqi;
    }

    public double getCo() {
        return co;
    }


    public double getNo2() {
        return no2;
    }

    public double getO3() {
        return o3;
    }

    public double getSo2() {
        return so2;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public double getPm10() {
        return pm10;
    }

    public double getNh3() {
        return nh3;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "AirQualityRecord{" +
                "location='" + location + '\'' +
                ", aqi=" + aqi +
                ", co=" + co +
                ", no2=" + no2 +
                ", o3=" + o3 +
                ", so2=" + so2 +
                ", pm2_5=" + pm2_5 +
                ", pm10=" + pm10 +
                ", nh3=" + nh3 +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
