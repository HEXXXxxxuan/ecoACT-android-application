package com.go4.application.historical;

import android.util.Log;

import java.util.Scanner;

public class Parser {
    /**
     * The following exception should be thrown if the parse is faced with series of tokens that do not
     * correlate with any possible production rule.
     */
    public static class IllegalProductionException extends IllegalArgumentException {
        public IllegalProductionException(String errorMessage) {
            super(errorMessage);
        }
    }

    // The tokenizer (class field) this parser will use.
    Tokenizer tokenizer;
    SearchRecord record;

    /**
     * Parser class constructor
     * Simply sets the tokenizer field.
     * **** please do not modify this part ****
     */
    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.record = new SearchRecord();
    }

    /**
     * To help you both test and understand what this parser is doing, we have included a main method
     * which you can run. Once running, provide a mathematical string to the terminal and you will
     * receive back the result of your parsing.
     */
    public String parseInput() {
        if (tokenizer.current() == null) {
            throw new IllegalProductionException("Illegal Production");
        }

        parseLocation();
        parseDate();
        parseHour();

        return this.record.getKey();
    }

    public void parseLocation() {
        // Parse the location token
        Token location = tokenizer.current();
        if (location.getType() == Token.Type.LOCATION) {
            tokenizer.next();
            Log.d("SearchDebug", "location: " + location.getToken());
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
                    String selectedDate = year.getToken() + month.getToken() + day.getToken();
                    this.record.setSelectedDate(selectedDate);
                }
            }
        }
//        else if (date.getType() == Token.Type.YEARMONTHDATE || date.getType() == Token.Type.YEARMONTHDATETIME) {
//            tokenizer.next();
//        } else {
//            throw new IllegalProductionException("Expected a date");
//        }
    }

    public void parseHour() {
        // Parse the hour token
        Token hour = tokenizer.current();
        if (hour.getType() == Token.Type.TIME) {
            tokenizer.next();
            this.record.setSelectedDate(hour.getToken());
        } else {
            throw new IllegalProductionException("Expected an hour");
        }
    }

}
