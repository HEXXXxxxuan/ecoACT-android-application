package com.go4.application.historical;

/**
 * This class is used to store search bar input and is used in {@link com.go4.utils.tokenizer_parser.Parser}
 *
 * @author u8003980 Chan Cheng Leong
 */
public class SearchRecord {
    private String selectedSuburb;
    private String selectedDate;
    private String selectedTime;
    private boolean invalidSearch;

    public SearchRecord() {
        this.selectedSuburb = "";
        this.selectedDate = "";
        this.selectedTime = "";
        this.invalidSearch = false;
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

    public String getInvalidSearch() {
        if (invalidSearch) return "invalid";
        else return "valid";
    }
}