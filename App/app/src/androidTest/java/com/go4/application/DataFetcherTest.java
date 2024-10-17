package com.go4.application;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import androidx.test.platform.app.InstrumentationRegistry;
import com.go4.utils.DataFetcher;
import org.junit.Test;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataFetcherTest {
    @Test (timeout = 10000)
    public void DataFetcherCreatesFile() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());
        DataFetcher dataFetcher = new DataFetcher(executorService, mainHandler, 1);

        // Mock a ProgressBar (optional, just to prevent errors)
        ProgressBar mockProgressBar = new ProgressBar(context);
        String fileName = "test_file.csv";
        dataFetcher.automaticAddRecords(context, fileName, mockProgressBar, () -> {
            // Assert that the file was created
            File generatedFile = new File(context.getCacheDir(), fileName);
            assertTrue("File should be generated", generatedFile.exists());
        });
    }
}
