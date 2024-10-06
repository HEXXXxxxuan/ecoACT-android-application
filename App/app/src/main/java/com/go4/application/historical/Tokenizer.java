package com.go4.application.historical;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private String buffer;          // String to be transformed into tokens each time next() is called.
    private Token currentToken;     // The current token. The next token is extracted when next() is called.
    private List<String> suburbs;

    public Tokenizer(String text, List<String> suburbs) {
        this.buffer = text.toLowerCase();          // save input text (string)
        this.suburbs = suburbs;
        next();                 // extracts the first token.
    }

    public void next() {
        buffer = buffer.trim();     // remove whitespace

        if (buffer.isEmpty()) {
            currentToken = null;    // if there's no string left, set currentToken null and return
            return;
        }

        char firstChar = buffer.charAt(0);

        if (Character.isLetter(firstChar)) {
            // Check for location
            currentToken = null;

            List<String> locations = suburbs;
            for (String location : locations) {
                String lowerCaseLocation = location.toLowerCase();
                if (buffer.startsWith(lowerCaseLocation)) {
                    currentToken = new Token(location, Token.Type.LOCATION, location.length());
                    break;
                }
            }

            // Check for month
            String[] months = {"january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
            String[] shortMonths = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

            if (currentToken == null) {
                for (int i = 0; i < months.length; i++) {
                    String month = months[i];
                    if (buffer.startsWith(month)) {
                        Log.d("SearchDebug", "Found month: " + month);
                        currentToken = new Token(String.valueOf(i + 1), Token.Type.MONTH, month.length());
                        break;
                    }
                }
            }

            if (currentToken == null) {
                for (int i = 0; i < shortMonths.length; i++) {
                    String shortMonth = shortMonths[i];
                    if (buffer.startsWith(shortMonth)) {
                        Log.d("SearchDebug", "Found shortMonth: " + shortMonth);
                        currentToken = new Token(String.valueOf(i + 1), Token.Type.MONTH, shortMonth.length());
                        break;
                    }
                }
            }
        }
        else if (Character.isDigit(firstChar)) {
            // Handle time suffixes
            Pattern pattern = Pattern.compile("^(\\d{1,2}am|\\d{1,2}pm)");
            Matcher matcher = pattern.matcher(buffer);
            if (matcher.find()) {
                String time = matcher.group(1);
                int hour = Integer.parseInt(time.substring(0, time.length() - 2));
                String period = time.substring(time.length() - 2);
                if (period.equalsIgnoreCase("pm") && hour != 12) {
                    hour += 12;
                } else if (period.equalsIgnoreCase("am") && hour == 12) {
                    hour = 0;
                }
                String formattedTime = String.format("%02d:00", hour);
                currentToken = new Token(formattedTime, Token.Type.TIME, time.length());
            } else {
                pattern = Pattern.compile("^(\\d{1,2}:\\d{2})");
                matcher = pattern.matcher(buffer);
                if (matcher.find()) {
                    String time = matcher.group(1);
                    currentToken = new Token(time, Token.Type.TIME, time.length());
                } else {
                    // Extract digits to determine year, date, or an invalid sequence
                    String digits = buffer.split("\\D+")[0];
                    int value = Integer.parseInt(digits);

                    if (value >= 32 && value <= 2024) {
                        currentToken = new Token(digits, Token.Type.YEAR, digits.length());
                    } else if (value >= 0 && value <= 31) {
                        currentToken = new Token(digits, Token.Type.DATE, digits.length());
                    }
                }
            }
        } else {
            if (firstChar == ',' || firstChar == ';' || firstChar == '_') {
                currentToken = new Token(firstChar + "", Token.Type.SEPERATOR, 1);
            }
            //                else {
            //                    throw new Token.IllegalTokenException("Unrecognized token");
            //                }
        }

        // Remove the extracted token from buffer
        int tokenLen = currentToken.getLength();
        Log.d("SearchDebug", "Current Token Length: " + tokenLen);
        buffer = buffer.substring(tokenLen);
        Log.d("SearchDebug", "Shortened Buffer: " + buffer);
    }

    public Token current() {
        return currentToken;
    }

    public boolean hasNext() {
        return currentToken != null;
    }
}
