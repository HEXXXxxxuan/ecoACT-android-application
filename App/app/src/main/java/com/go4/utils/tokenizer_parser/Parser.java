package com.go4.utils.tokenizer_parser;

import android.content.Context;
import android.widget.Toast;

import com.go4.application.historical.SearchRecord;

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
        return new String[]{record.getSelectedSuburb(), record.getSelectedDate(), record.getSelectedTime()};
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
        } else if (currentType == Token.Type.SEPARATOR) {
            tokenizer.next();
        } else {
            record.setInvalidSearch(true);
            Toast.makeText(context, "Invalid Search", Toast.LENGTH_SHORT).show();
            tokenizer.next();
        }
        parseInput();
    }

    public void parseLocation() {
        // Parse the location token
        Token location = tokenizer.current();
        tokenizer.next();
        record.setSelectedSuburb(location.getToken());
    }

    public void parseDate() {
        // Parse the date token
        Token date = tokenizer.current();
        if (date.getType() == Token.Type.YEAR) {
            Token year = tokenizer.current();
            tokenizer.next();
            if (tokenizer.current() != null && tokenizer.current().getType() == Token.Type.MONTH) {
                Token month = tokenizer.current();
                tokenizer.next();
                if (tokenizer.current() != null && tokenizer.current().getType() == Token.Type.DATE) {
                    Token day = tokenizer.current();
                    tokenizer.next();
                    String selectedDate = year.getToken() + "-" + month.getToken() + "-" + day.getToken();
                    record.setSelectedDate(selectedDate);
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
        tokenizer.next();
        record.setSelectedTime(time.getToken());
    }

}
