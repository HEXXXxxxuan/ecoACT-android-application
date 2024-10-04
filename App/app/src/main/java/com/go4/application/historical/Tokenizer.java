package com.go4.application.historical;

import android.util.Log;

import java.util.List;
import java.util.Scanner;

/**
 * Welcome to Task 1.
 * In this task your job is to implement the next() method. Just some rules:
 * 1. You may NOT modify any other classes in this task 1 package.
 * 2. You may not modify any of the methods or fields (that already exist) within this class.
 * 3. You may create additional fields or methods to finish you implementation within this class.
 * <p>
 * Tokenization, within the context of this lab, is the process of splitting a string into
 * small units called, 'Tokens', to be passed onto the Parser.
 */
public class Tokenizer {
    private String buffer;          // String to be transformed into tokens each time next() is called.
    private Token currentToken;     // The current token. The next token is extracted when next() is called.
    private List<String> subrubs;


    /**
     * To help you both test and understand what this tokenizer is doing, we have included a main method
     * which you can run. Once running, provide a mathematical string to the terminal and you will
     * receive back the result of your tokenization.
     */
    public static void main(String[] args) {
        // Create a scanner to get the user's input.
        Scanner scanner = new Scanner(System.in);

        /*
         Continue to get the user's input until they exit.
         To exit press: Control + D or providing the string 'q'
         Example input you can try: ((1 + 2) * 5)/2
         */
        System.out.println("Provide a mathematical string to be tokenized:");
        while (scanner.hasNext()) {
            String input = scanner.nextLine();

            // Check if 'quit' is provided.
            if (input.equals("q"))
                break;

            // Create an instance of the tokenizer.
            Tokenizer tokenizer = new Tokenizer(input);

            // Print all the tokens.
            while (tokenizer.hasNext()) {
                System.out.print(tokenizer.current() + " ");
                tokenizer.next();
            }
            System.out.println();
        }
    }

    /**
     * Tokenizer class constructor
     * The constructor extracts the first token and save it to currentToken
     * **** please do not modify this part ****
     */
    public Tokenizer(String text) {
        buffer = text;          // save input text (string)
        next();                 // extracts the first token.
    }

    /**
     * This function will find and extract a next token from {@code _buffer} and
     * save the token to {@code currentToken}.
     */
    public void next() {
        buffer = buffer.trim();     // remove whitespace

        if (buffer.isEmpty()) {
            currentToken = null;    // if there's no string left, set currentToken null and return
            return;
        }

        /*
        To help you, we have already written the first few steps in the tokenization process.
        The rest will follow a similar format.
         */
        char firstChar = buffer.charAt(0);

        if (Character.isLetter(firstChar)) {
            // Check for location (e.g., suburbs from "canberra_suburbs.json")
            String[] locations = {"Suburb1", "Suburb2"}; // Example suburbs
            for (String location : locations) {
                if (buffer.startsWith(location)) {
                    currentToken = new Token(location, Token.Type.LOCATION);
                    break;
                }
            }

            // Check for month
            String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            String[] shortMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for (String month : months) {
                if (buffer.startsWith(month)) {
                    currentToken = new Token(month, Token.Type.MONTH);
                    break;
                }
            }
            for (String shortMonth : shortMonths) {
                if (buffer.startsWith(shortMonth)) {
                    currentToken = new Token(shortMonth, Token.Type.MONTH);
                    break;
                }
            }
//            if (currentToken == null)
//                throw new Token.IllegalTokenException("Unrecognized token starting with letter");
        }
        else if (Character.isDigit(firstChar)) {
            // Handle time suffixes
            if (buffer.matches("^(\\d{1,2}am|\\d{1,2}pm)")) {
                currentToken = new Token(buffer.substring(0, buffer.indexOf("m") + 1), Token.Type.TIME);
            }

            // Check for time format "1:00", "01:00"
            else if (buffer.matches("^\\d{1,2}:\\d{2}")) {
                currentToken = new Token(buffer.substring(0, buffer.indexOf(':') + 3), Token.Type.TIME);
            }

            // Check for yearmonthdate and yearmonthdatetime formats
            else if (buffer.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
                currentToken = new Token(buffer.substring(0, 19), Token.Type.YEARMONTHDATETIME);
            }
            else if (buffer.matches("^\\d{4}-\\d{2}-\\d{2}")) {
                currentToken = new Token(buffer.substring(0, 10), Token.Type.YEARMONTHDATE);
            }
            else if (buffer.matches("^\\d{8}")) {
                currentToken = new Token(buffer.substring(0, 8), Token.Type.YEARMONTHDATE);
            } else {
                // Extract digits to determine year, date, or an invalid sequence
                String digits = buffer.split("\\D+")[0];
                int value = Integer.parseInt(digits);

                if (value >= 32 && value <= 2024) {
                    currentToken = new Token(digits, Token.Type.YEAR);
                } else if (value >= 0 && value <= 31) {
                    currentToken = new Token(digits, Token.Type.DATE);
                }
//                else {
//                    throw new Token.IllegalTokenException("Unrecognized token starting with digit");
//                }
            }
        } else {
            if (firstChar == ',' || firstChar == ';') {
                currentToken = new Token(firstChar + "", Token.Type.SEPERATOR);
            }
            //                else {
//                    throw new Token.IllegalTokenException("Unrecognized token");
//                }
        }

        // Remove the extracted token from buffer
        int tokenLen = currentToken.getToken().length();
        Log.d("SearchDebug", "Current Token Length: " + tokenLen);
        buffer = buffer.substring(tokenLen);
        Log.d("SearchDebug", "Shortened Buffer: " + buffer);
    }

    /**
     * Returns the current token extracted by {@code next()}
     * **** please do not modify this part ****
     *
     * @return type: Token
     */
    public Token current() {
        return currentToken;
    }

    /**
     * Check whether tokenizer still has tokens left
     * **** please do not modify this part ****
     *
     * @return type: boolean
     */
    public boolean hasNext() {
        return currentToken != null;
    }
}
