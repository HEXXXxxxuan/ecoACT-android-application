package com.go4.application;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.go4.utils.DataFetcher;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class DataFetcherTest {

    @Test
    public void testFetchHistoricalDataTOCSV_withRealAPI() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());


        String realApiKey = "4f6d63b7d7512fc4b14ee2aeb89d3128";

        DataFetcher dataFetcher = new DataFetcher(executorService, mainHandler, 1, realApiKey);

        // Mock a ProgressBar (optional, just to prevent errors)
        ProgressBar mockProgressBar = new ProgressBar(context);

        String fileName = "test_file.csv";

        dataFetcher.automaticAddRecords(context, fileName, mockProgressBar, () -> {
            System.out.println("Fetch complete.");
        });

        Thread.sleep(5000);

        // Check the CSV file in cache directory
        File generatedFile = new File(context.getCacheDir(), fileName);

        // Assert that the file was created
        assertTrue("File should be generated", generatedFile.exists());

    }

}
