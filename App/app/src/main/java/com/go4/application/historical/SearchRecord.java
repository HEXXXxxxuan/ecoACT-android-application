package com.go4.application.historical;

public class SearchRecord {
    private String selectedSuburb;
    private String selectedDate;
    private String selectedHour;
    private String key;

    public SearchRecord() {
        this.selectedSuburb = "";
        this.selectedDate = "";
        this.selectedHour = "";
        this.key = generateKey();
    }

    private String generateKey() {
        return selectedSuburb + "_" + selectedDate + " " + selectedHour + ":00:00";
    }

    public void setSelectedSuburb(String suburb) {
        this.selectedSuburb = suburb;
    }

    public void setSelectedDate(String date) {
        this.selectedDate = date;
    }

    public void setSelectedHour(String hour) {
        this.selectedHour = hour;
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
        this.key = generateKey();
        return key;
    }
}