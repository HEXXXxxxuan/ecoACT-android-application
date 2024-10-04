package com.go4.application.historical;

public class SearchRecord {
    private String selectedSuburb;
    private String selectedDate;
    private String selectedHour;
    private String key;

    public SearchRecord(String suburb, String date, String hour) {
        this.selectedSuburb = suburb;
        this.selectedDate = date;
        this.selectedHour = hour;
        this.key = generateKey();
    }

    private String generateKey() {
        return selectedSuburb + "_" + selectedDate + " " + selectedHour + ":00:00";
    }

    public String getSelectedSuburb() {
        return selectedSuburb;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public String getSelectedHour() {
        return selectedHour;
    }

    public String getKey() {
        return key;
    }
}