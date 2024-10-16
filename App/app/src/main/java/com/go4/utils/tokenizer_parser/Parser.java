
package com.go4.utils.tokenizer_parser;

import android.content.Context;
import android.widget.Toast;

import com.go4.application.historical.SearchRecord;

/**
 * This class is used to parser search bar input in the Suburb Historical Data Page.
 *
 * @author u8003980 Chan Cheng Leong
 */
public class Parser {
    Tokenizer tokenizer;
    SearchRecord record;
    Context context;

    public Parser(Tokenizer tokenizer, Context context) {
        this.tokenizer = tokenizer;
        this.record = new SearchRecord();
        this.context = context;
    }

    /**
     * This method is used to return data stored in record with type {@link SearchRecord}.
     *
     * @return      A list of strings from the user's input, including suburb, date and time.
     */
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
    }

    public void parseTime() {
        // Parse the time token
        Token time = tokenizer.current();
        tokenizer.next();
        record.setSelectedTime(time.getToken());
    }

}
