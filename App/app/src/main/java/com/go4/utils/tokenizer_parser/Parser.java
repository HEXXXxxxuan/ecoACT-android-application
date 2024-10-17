
package com.go4.utils.tokenizer_parser;
import com.go4.application.historical.SearchRecord;

/**
 * This class is used to parser search bar input in the Suburb Historical Data Page.
 *
 * @author u8003980 Chan Cheng Leong
 */
public class Parser {
    Tokenizer tokenizer;
    SearchRecord record;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.record = new SearchRecord();
    }

    /**
     * This method is used to return data stored in record with type {@link SearchRecord}.
     *
     * @return      A list of strings from the user's input, including suburb, date and time.
     */
    public String[] getData() {
        return new String[]{record.getSelectedSuburb(), record.getSelectedDate(), record.getSelectedTime(), record.getInvalidSearch()};
    }

    /**
     * This method checks the type of the current token,
     * decides which method to use and parse the token
     * and then calls itself recursively to parse the next token.
     *
     * <p>This method is called every time the text in the search bar is changed.</p>
     *
     * <p>All strings are case insensitive.</p>
     */
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
            tokenizer.next();
        }
        parseInput();
    }

    /**
     * This method parses the location token and stores it in record with type {@link SearchRecord}.
     */
    public void parseLocation() {
        // Parse the location token
        Token location = tokenizer.current();
        tokenizer.next();
        record.setSelectedSuburb(location.getToken());
    }

    /**
     * This method parses the date, which must contain a year token, a month token and a day token in that order.
     * It forms a date string with the three tokens and stores it in record with type {@link SearchRecord}.
     */
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
    }

    /**
     * This method parses the time token and stores it in record with type {@link SearchRecord}.
     */
    public void parseTime() {
        // Parse the time token
        Token time = tokenizer.current();
        tokenizer.next();
        record.setSelectedTime(time.getToken());
    }

}
