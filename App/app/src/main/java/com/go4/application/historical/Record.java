package com.go4.application.historical;

import android.util.Log;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;


public class Record {
    private int recordId;
    private String timestamp;
    private String location;
    private double temperature;
    private int smokeLevel;
    private double carbonMonoxide;
    private Date parsedDate;

    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");



    public Record(int recordId, String timestamp, String location, double temperature, int smokeLevel, double carbonMonoxide) {
        this.recordId = recordId;
        this.timestamp = timestamp;
        this.location = location;
        this.temperature = temperature;
        this.smokeLevel = smokeLevel;
        this.carbonMonoxide = carbonMonoxide;

        try {
            this.parsedDate = TIMESTAMP_FORMAT.parse(timestamp);
            Log.d("RecordDebug", "Parsed date: " + this.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getSmokeLevel() {
        return smokeLevel;
    }

    public void setSmokeLevel(int smokeLevel) {
        this.smokeLevel = smokeLevel;
    }

    public double getCarbonMonoxide() {
        return carbonMonoxide;
    }

    public void setCarbonMonoxide(double carbonMonoxide) {
        this.carbonMonoxide = carbonMonoxide;
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(parsedDate);
    }

    public String getTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return timeFormat.format(parsedDate);
    }

    @Override
    public String toString() {
        return "Record{" +
                "recordId=" + recordId +
                ", timestamp='" + timestamp + '\'' +
                ", location='" + location + '\'' +
                ", temperature=" + temperature +
                ", smokeLevel=" + smokeLevel +
                ", carbonMonoxide=" + carbonMonoxide +
                '}';
    }
}
