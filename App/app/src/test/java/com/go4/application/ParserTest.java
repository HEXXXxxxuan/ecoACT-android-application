package com.go4.application;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


import com.go4.utils.tokenizer_parser.Parser;
import com.go4.utils.tokenizer_parser.Tokenizer;

import java.util.Arrays;
import java.util.List;

public class ParserTest {
    private static Tokenizer tokenizer;
    private static final List<String> LOCATION_STRINGS = Arrays.asList(new String[]{"Acton"});

    private static final String[] testOrder = new String[]{"Acton 2024 Oct 16 8pm", "Acton 8pm 2024 Oct 16",
                                                             "2024 Oct 16 Acton 8pm", "2024 Oct 16 8pm Acton",
                                                             "8pm Acton 2024 Oct 16", "8pm 2024 Oct 16 Acton"};

    private static final String[] testSeparator = new String[]{"Acton,2024 Oct 16,8pm",
                                                               "Acton;2024 Oct 16;8pm",
                                                               "Acton_2024 Oct 16_8pm"};

    private static final String[] testInvalid = new String[]{"Acton a 2024 Oct 16 8pm", "Acton 2024 a Oct 16 8pm",
                                                             "Acton 2024 Oct a 16 8pm", "Acton 2024 Oct 16 a 8pm",
                                                             "Acton 2024 Oct 16 8pm a"};

    @Test(timeout = 1000)
    public void testOrder1() {
        Tokenizer tokenizer = new Tokenizer(testOrder[0], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "valid", data[3]);
    }

    @Test(timeout = 1000)
    public void testOrder2() {
        Tokenizer tokenizer = new Tokenizer(testOrder[1], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "valid", data[3]);
    }

    @Test(timeout = 1000)
    public void testOrder3() {
        Tokenizer tokenizer = new Tokenizer(testOrder[2], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "valid", data[3]);
    }

    @Test(timeout = 1000)
    public void testOrder4() {
        Tokenizer tokenizer = new Tokenizer(testOrder[3], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "valid", data[3]);
    }

    @Test(timeout = 1000)
    public void testOrder5() {
        Tokenizer tokenizer = new Tokenizer(testOrder[4], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "valid", data[3]);
    }

    @Test(timeout = 1000)
    public void testOrder6() {
        Tokenizer tokenizer = new Tokenizer(testOrder[5], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "valid", data[3]);
    }

    @Test(timeout = 1000)
    public void testSeparator1() {
        Tokenizer tokenizer = new Tokenizer(testSeparator[0], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "valid", data[3]);
    }

    @Test(timeout = 1000)
    public void testSeparator2() {
        Tokenizer tokenizer = new Tokenizer(testSeparator[1], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "valid", data[3]);
    }

    @Test(timeout = 1000)
    public void testSeparator3() {
        Tokenizer tokenizer = new Tokenizer(testSeparator[2], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "valid", data[3]);
    }

    @Test(timeout = 1000)
    public void setTestInvalid1() {
        Tokenizer tokenizer = new Tokenizer(testInvalid[0], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "invalid", data[3]);
    }

    @Test(timeout = 1000)
    public void setTestInvalid2() {
        Tokenizer tokenizer = new Tokenizer(testInvalid[1], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "invalid", data[3]);
    }

    @Test(timeout = 1000)
    public void setTestInvalid3() {
        Tokenizer tokenizer = new Tokenizer(testInvalid[2], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "invalid", data[3]);
    }

    @Test(timeout = 1000)
    public void setTestInvalid4() {
        Tokenizer tokenizer = new Tokenizer(testInvalid[3], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "invalid", data[3]);
    }

    @Test(timeout = 1000)
    public void setTestInvalid5() {
        Tokenizer tokenizer = new Tokenizer(testInvalid[4], LOCATION_STRINGS);
        Parser parser = new Parser(tokenizer);
        parser.parseInput();
        String[] data = parser.getData();
        assertEquals("wrong location", "Acton", data[0]);
        assertEquals("wrong date", "2024-10-16", data[1]);
        assertEquals("wrong time", "20:00", data[2]);
        assertEquals("wrong validity", "invalid", data[3]);
    }
}
