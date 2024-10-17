package com.go4.application;
import com.go4.utils.tokenizer_parser.Parser;
import com.go4.utils.tokenizer_parser.Token;
import com.go4.utils.tokenizer_parser.Tokenizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * This class is used to test the class {@link Tokenizer}.
 *
 * @author u8003980 Chan Cheng Leong
 */
public class TokenizerTest {
    private static Tokenizer tokenizer;
    private static final List<String> LOCATION_STRINGS = Arrays.asList(new String[]{"Acton"});

    private static final String LOCATION_TOKEN = "Acton";
    private static final String YEAR_TOKEN = "2024";
    private static final String MONTH_TOKEN_LONG = "October";
    private static final String MONTH_TOKEN_SHORT = "Oct";
    private static final String DAY_TOKEN = "16";
    private static final String TIME_TOKEN_24_1 = "8am";
    private static final String TIME_TOKEN_24_2 = "8pm";
    private static final String TIME_TOKEN_12 = "8:00";
    private static final String SEPARATOR_TOKEN = ",";
    private static final String INVALID_TOKEN_1 = "a";
    private static final String INVALID_TOKEN_2 = "!";

    @Test(timeout = 1000)
    public void testLocationToken() {
        tokenizer = new Tokenizer(LOCATION_TOKEN, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.LOCATION, tokenizer.current().getType());
        assertEquals("wrong token value", "Acton", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testYearToken() {
        tokenizer = new Tokenizer(YEAR_TOKEN, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.YEAR, tokenizer.current().getType());
        assertEquals("wrong token value", "2024", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testMonthTokenLong() {
        tokenizer = new Tokenizer(MONTH_TOKEN_LONG, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.MONTH, tokenizer.current().getType());
        assertEquals("wrong token value", "10", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testMonthTokenShort() {
        tokenizer = new Tokenizer(MONTH_TOKEN_SHORT, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.MONTH, tokenizer.current().getType());
        assertEquals("wrong token value", "10", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testDayTokenShort() {
        tokenizer = new Tokenizer(DAY_TOKEN, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.DATE, tokenizer.current().getType());
        assertEquals("wrong token value", "16", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testTime24Token_1() {
        tokenizer = new Tokenizer(TIME_TOKEN_24_1, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.TIME, tokenizer.current().getType());
        assertEquals("wrong token value", "08:00", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testTime24Token_2() {
        tokenizer = new Tokenizer(TIME_TOKEN_24_2, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.TIME, tokenizer.current().getType());
        assertEquals("wrong token value", "20:00", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testTime12Token() {
        tokenizer = new Tokenizer(TIME_TOKEN_12, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.TIME, tokenizer.current().getType());
        assertEquals("wrong token value", "8:00", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testSeparatorToken() {
        tokenizer = new Tokenizer(SEPARATOR_TOKEN, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.SEPARATOR, tokenizer.current().getType());
        assertEquals("wrong token value", ",", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testInvalidToken1() {
        tokenizer = new Tokenizer(INVALID_TOKEN_1, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.INVALID, tokenizer.current().getType());
        assertEquals("wrong token value", "a", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testInvalidToken2() {
        tokenizer = new Tokenizer(INVALID_TOKEN_2, LOCATION_STRINGS);
        assertEquals("wrong token type", Token.Type.INVALID, tokenizer.current().getType());
        assertEquals("wrong token value", "!", tokenizer.current().getToken());
    }
}
