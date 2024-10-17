package com.go4.application;
import android.content.Context;
import android.util.Log;

import com.go4.application.model.AirQualityRecord;
import com.go4.utils.CsvParser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

/**
 * This class contains unit tests for the CsvParser utility class, which parses CSV files containing
 * air quality data. The tests verify the correct parsing of valid records, the handling of invalid
 * formats, and edge cases such as empty files.
 *
 * Mocking and temporary files are used to simulate various CSV files with different data formats
 * and record structures.
 * @author Gea Linggar
 */
public class CSVParsingTest {
    @Mock
    private final Context context = mock(Context.class);

    @Rule
    public TemporaryFolder cacheDir = new TemporaryFolder();

    /**
     * Sets up the test environment before each test by creating temporary CSV files with various
     * contents. These include:
     * - An empty file (`text_file_empty.csv`) to test edge cases.
     * - A file with two valid records (`test_file_2.csv`).
     * - A file with many valid records (`test_file_many`).
     * - A file with incorrect formatting and missing fields (`test_wrong_format`).
     *
     * The setup method also mocks the `context.getCacheDir()` call to point to the temporary folder
     * created during testing.
     */
    @Before
    public void setup() throws IOException {
        cacheDir.newFile("text_file_empty.csv");
        when(context.getCacheDir()).thenReturn(cacheDir.getRoot());
        File testFile2Records = cacheDir.newFile("test_file_2.csv");
        File testFileManyRecords = cacheDir.newFile("test_file_many");
        File testWrongFormat = cacheDir.newFile("test_wrong_format");

        BufferedWriter writer = new BufferedWriter(new FileWriter(testFile2Records));
        writer.write("location,timestamp,aqi,co,no2,o3,so2,pm2_5,pm10,nh3\n");
        writer.write("Acton,1635724800000,42,0.02,9.1,40.5,1.2,13.5,25.6,0.8\n");
        writer.write("Barton,1635738400000,58,0.03,10.2,35.7,2.1,14.3,29.1,1.0\n");
        writer.close();

        writer = new BufferedWriter(new FileWriter(testFileManyRecords));
        writer.write("location,timestamp,aqi,co,no2,o3,so2,pm2_5,pm10,nh3\n");
        writer.write("Acton,1635724800000,42,0.02,9.1,40.5,1.2,13.5,25.6,0.8\n");
        writer.write("Barton,1635738400000,58,0.03,10.2,35.7,2.1,14.3,29.1,1.0\n");
        writer.write("Kingston,1635742000000,30,0.01,5.3,28.7,1.0,9.8,18.2,0.6\n");
        writer.write("Yarralumla,1635755600000,65,0.04,12.1,38.5,2.5,17.2,35.8,1.2\n");
        writer.write("Deakin,1635769200000,80,0.05,15.7,42.9,3.1,20.4,40.1,1.4\n");
        writer.write("Griffith,1635782800000,55,0.03,9.4,32.5,2.0,14.7,27.6,1.0\n");
        writer.write("Narrabundah,1635796400000,72,0.04,13.2,39.9,2.8,19.1,36.9,1.3\n");
        writer.write("O'Connor,1635810000000,48,0.02,7.6,30.2,1.5,12.4,23.8,0.9\n");
        writer.write("Braddon,1635823600000,60,0.03,11.0,35.1,2.3,16.2,31.5,1.1\n");
        writer.write("Ainslie,1635837200000,45,0.02,8.2,29.3,1.4,11.7,22.5,0.7\n");
        writer.write("Reid,1635850800000,70,0.04,14.5,41.7,2.9,18.5,37.2,1.3\n");
        writer.write("Turner,1635864400000,62,0.03,10.7,36.4,2.2,15.9,30.7,1.1\n");
        writer.write("Campbell,1635878000000,50,0.02,9.0,32.8,1.7,13.3,25.9,0.9\n");
        writer.close();

        writer = new BufferedWriter(new FileWriter(testWrongFormat));
        writer.write("Acton,1728097200,1.0,216.96,0.5,40.5,1.2,13.5,25.6,0.8\n"); // Correctly formatted row
        writer.write("Amaroo12,1728090000,1.0,223.64,1.46,60.08,0.37,0.67,1.15,1.01\n");
        writer.write("Barton,1728097200,1.0,230.31,2.59,59.37\n");
        writer.write("Ainslie,1728090000,1.0,230.31,1.0,50.0,1.0,20.0,30.0,0.7,5.0\n");
        writer.write("Deakin,1728090000,invalidAQI,230.31,1.0,50.0,1.0,20.0,30.0,0.7\n");
        writer.write("Yarralumla,1728090000,1.0,240.31,1.5,55.0,1.1,21.0,32.0,0.9\n"); // Correctly formatted row
        writer.close();
    }

    /**
     * Tests the behavior of CsvParser when parsing an empty CSV file.
     */
    @Test
    public void testEmptyCSV(){
        CsvParser csvParser = new CsvParser();
        List<AirQualityRecord> records = csvParser.parseData(context,"testFileEmptyRecords");
        assertNotNull("List should not be null", records);
        assertEquals("Record should be empty list", 0, records.size());
    }

    /**
     * Tests the parsing of a small CSV file containing two valid AirQualityRecord entries.
     */
    @Test
    public void testSmallRecords(){
        CsvParser csvParser = new CsvParser();
        List<AirQualityRecord> records = csvParser.parseData(context,"test_file_2.csv");
        assertNotNull("List should not be null", records);
        assertEquals("Acton", records.get(0).getLocation());
        assertEquals(42, records.get(0).getAqi(), 0);
        assertEquals(0.8, records.get(0).getNh3(), 0);
        assertEquals("Barton", records.get(1).getLocation());
        assertEquals(1.0, records.get(1).getNh3(), 0);
    }

    /**
     * Tests the parsing of a CSV file with many records. Verifies that the number of records parsed
     * is correct and ensures no data loss during the process.
     */
    @Test
    public void testManyRecords(){
        CsvParser csvParser = new CsvParser();
        List<AirQualityRecord> records = csvParser.parseData(context,"test_file_many");
        assertNotNull("List should not be null", records);
        assertEquals( 13, records.size());
    }

    /**
     * Tests the handling of a CSV file with invalid formatting.
     */
    @Test
    public void testInvalidRecord(){
        CsvParser csvParser = new CsvParser();
        List<AirQualityRecord> records = csvParser.parseData(context,"test_wrong_format");
        assertEquals("Should have parsed 2 valid records", 2, records.size());
    }

    /**
     * Tests the method CsvParser.parseDoubleSafely, which parses a string into a double value.
     */
    @Test
    public void testParseDoubleSafely(){
        CsvParser csvParser = new CsvParser();
        double result = csvParser.parseDoubleSafely("123.45", 0.0);
        assertEquals("Should parse valid double", 123.45, result, 0.001);
    }

    /**
     * Tests the CsvParser.parseDoubleSafely method with invalid input.
     */
    @Test
    public void testParseDoubleSafely_InvalidInput() {
        CsvParser csvParser = new CsvParser();
        double result = csvParser.parseDoubleSafely("invalidDouble", 5.0);
        assertEquals("Should return default value for invalid input", 5.0, result, 0.001);
    }

    /**
     * Tests the method CsvParser.parseLongSafely, which parses a string into a long value.
     */
    @Test
    public void testParseLongSafely(){
        CsvParser csvParser = new CsvParser();
        double result = csvParser.parseLongSafely("123456789", 0);
        assertEquals("Should parse valid long", 123456789, result, 0.001);
    }

    /**
     * Tests the CsvParser.parseLongSafely method with invalid input.
     */
    @Test
    public void testParseLongSafely_InvalidInput() {
        CsvParser csvParser = new CsvParser();
        double result = csvParser.parseLongSafely("invalidLong", 5);
        assertEquals("Should return default value for invalid input", 5, result, 0.001);
    }
}
