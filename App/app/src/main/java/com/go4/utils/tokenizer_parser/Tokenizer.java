package com.go4.utils.tokenizer_parser;

import android.util.Log;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author u8003980 Chan Cheng Leong
 */
public class Tokenizer {
    private String buffer;
    private Token currentToken;
    private List<String> suburbs;

    public Tokenizer(String text, List<String> suburbs) {
        this.buffer = text.toLowerCase();
        this.suburbs = suburbs;
        next();
    }

    public void next() {
        buffer = buffer.trim();

        if (buffer.isEmpty()) {
            currentToken = null;
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

            if (currentToken == null) {
                currentToken = new Token(String.valueOf(firstChar), Token.Type.INVALID, 1);
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
                    } else {
                        currentToken = new Token(String.valueOf(firstChar), Token.Type.INVALID, 1);
                    }
                }
            }
        } else if (firstChar == ',' || firstChar == ';' || firstChar == '_') {
            currentToken = new Token(String.valueOf(firstChar), Token.Type.SEPARATOR, 1);
        } else {
            currentToken = new Token(String.valueOf(firstChar), Token.Type.INVALID, 1);
        }

        // Remove the extracted token from buffer
        int tokenLen = currentToken.getLength();
        Log.d("SearchDebug", "Current Token Length:" + tokenLen);
        buffer = buffer.substring(tokenLen);
        Log.d("SearchDebug", "Shortened Buffer:" + buffer);
    }

    public Token current() {
        return currentToken;
    }
}
