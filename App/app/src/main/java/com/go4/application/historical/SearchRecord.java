package com.go4.application.historical;

public class SearchRecord {
    private String selectedSuburb;
    private String selectedDate;
    private String selectedTime;
    private String key;
    private boolean invalidSearch;

    public SearchRecord() {
        this.selectedSuburb = "";
        this.selectedDate = "";
        this.selectedTime = "";
        this.invalidSearch = false;
        this.key = generateKey();
    }

    private String generateKey() {
        return selectedSuburb + "_" + selectedDate + " " + selectedTime + ":00";
    }

    public void setSelectedSuburb(String suburb) {
        this.selectedSuburb = suburb;
    }

    public void setSelectedDate(String date) {
        this.selectedDate = date;
    }

    public void setSelectedTime(String hour) {
        this.selectedTime = hour;
    }

    public void setInvalidSearch(boolean invalid) {
        this.invalidSearch = invalid;
    }

    public String getSelectedSuburb() {
        return selectedSuburb;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public boolean getInvalidSearch() {
        return invalidSearch;
    }

    public String getKey() {
        this.key = generateKey();
        return key;
    }
}