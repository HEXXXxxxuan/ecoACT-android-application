package com.go4.application.historical;

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
    static SearchRecord record;

    /**
     * Parser class constructor
     * Simply sets the tokenizer field.
     * **** please do not modify this part ****
     */
    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * To help you both test and understand what this parser is doing, we have included a main method
     * which you can run. Once running, provide a mathematical string to the terminal and you will
     * receive back the result of your parsing.
     */
    public static void main(String[] args) {
        // Create a scanner to get the user's input.
        Scanner scanner = new Scanner(System.in);

        /*
         Continue to get the user's input until they exit.
         To exit press: Control + D or providing the string 'q'
         Example input you can try: ((1 + 2) * 5)/2
         Note that evaluations will round down to negative infinity (because they are integers).
         */
        System.out.println("Provide a mathematical string to be parsed:");
        while (scanner.hasNext()) {
            String input = scanner.nextLine();

            // Check if 'quit' is provided.
            if (input.equals("q"))
                break;

            // Create an instance of the tokenizer.
            Tokenizer tokenizer = new Tokenizer(input);

            // Print out the expression from the parser.
            Parser parser = new Parser(tokenizer);
            record = parser.parseInput();
            //            System.out.println("Parsing: " + expression.show());
            //            System.out.println("Evaluation: " + expression.evaluate());
        }
    }

    public SearchRecord parseInput() {
        if (tokenizer.current() == null) {
            throw new IllegalProductionException("Illegal Production");
        }

        String selectedSuburb = parseLocation().getToken();
        String selectedDate = parseDate().getToken();
        String selectedHour = parseHour().getToken();

        return new SearchRecord(selectedSuburb, selectedDate, selectedHour);
    }

    public Token parseLocation() {
        // Parse the location token
        Token location = tokenizer.current();
        if (location.getType() == Token.Type.LOCATION) {
            tokenizer.next();
            return location;
        } else {
            throw new IllegalProductionException("Expected a location");
        }
    }

    public Token parseDate() {
        // Parse the date token
        Token date = tokenizer.current();
        if (date.getType() == Token.Type.SEPERATOR) {
            tokenizer.next();
            return parseDate();
        } else if (date.getType() == Token.Type.YEAR || date.getType() == Token.Type.YEARMONTHDATE || date.getType() == Token.Type.YEARMONTHDATETIME) {
            tokenizer.next();
            return date;
        } else {
            throw new IllegalProductionException("Expected a date");
        }
    }

    public Token parseHour() {
        // Parse the hour token
        Token hour = tokenizer.current();
        if (hour.getType() == Token.Type.TIME) {
            tokenizer.next();
            return hour;
        } else {
            throw new IllegalProductionException("Expected an hour");
        }
    }

}
