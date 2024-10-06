package com.go4.application.historical;

import android.util.Log;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static class IllegalProductionException extends IllegalArgumentException {
        public IllegalProductionException(String errorMessage) {
            super(errorMessage);
        }
    }

    // The tokenizer (class field) this parser will use.
    Tokenizer tokenizer;
    SearchRecord record;
    Context context;

    public Parser(Tokenizer tokenizer, Context context) {
        this.tokenizer = tokenizer;
        this.record = new SearchRecord();
        this.context = context;
    }

    public String[] getData() {
        return new String[]{record.getSelectedSuburb(), record.getSelectedDate(), record.getSelectedTime(), record.getKey()};
    }

    public void parseInput() {
        if (tokenizer.current() == null) {
            return;
        }
        Token.Type currentType = tokenizer.current().getType();
        if (currentType == Token.Type.LOCATION) {
            parseLocation();
        } else if (currentType == Token.Type.YEAR) {
            parseDate();
        } else if (currentType == Token.Type.TIME) {
            parseTime();
        } else if (currentType == Token.Type.SEPERATOR) {
            tokenizer.next();
        } else {
            tokenizer.next();
        }
        parseInput();
    }

    public void parseLocation() {
        // Parse the location token
        Token location = tokenizer.current();
        if (location.getType() == Token.Type.LOCATION) {
            tokenizer.next();
            this.record.setSelectedSuburb(location.getToken());
        } else {
            throw new IllegalProductionException("Expected a location");
        }
    }

    public void parseDate() {
        // Parse the date token
        Token date = tokenizer.current();
        if (date.getType() == Token.Type.YEAR) {
            Token year = tokenizer.current();
            tokenizer.next();
            if (tokenizer.current().getType() == Token.Type.MONTH) {
                Token month = tokenizer.current();
                tokenizer.next();
                if (tokenizer.current().getType() == Token.Type.DATE) {
                    Token day = tokenizer.current();
                    tokenizer.next();
                    String selectedDate = year.getToken() + "-" + month.getToken() + "-" + day.getToken();
                    this.record.setSelectedDate(selectedDate);
                }
            }
        }
//        else if (date.getType() == Token.Type.YEARMONTHDATE) {
//            Token yearMonthDate = tokenizer.current();
//            tokenizer.next();
//            this.record.setSelectedDate(yearMonthDate.getToken());
//        }
//        else if (date.getType() == Token.Type.YEARMONTHDATE || date.getType() == Token.Type.YEARMONTHDATETIME) {
//            tokenizer.next();
//        } else {
//            throw new IllegalProductionException("Expected a date");
//        }
    }

    public void parseTime() {
        // Parse the hour token
        Token time = tokenizer.current();
        if (time.getType() == Token.Type.TIME) {
            tokenizer.next();
            this.record.setSelectedTime(time.getToken());
        } else {
            throw new IllegalProductionException("Expected an hour");
        }
    }

}
