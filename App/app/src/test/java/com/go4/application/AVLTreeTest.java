package com.go4.application;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import com.go4.application.historical.AirQualityRecord;
import com.go4.application.tree.AVLTree;


import org.junit.Before;
import org.junit.Test;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AVLTreeTest {
    private AVLTree<String, AirQualityRecord> avlTree;
    private SimpleDateFormat dateFormat;
    private Context context;

    @Before
    public void testInsertAndBalance(){
        long baseTimestamp = 1635724800;
        avlTree = new AVLTree<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        AirQualityRecord[] records = new AirQualityRecord[6];
        records[0] = new AirQualityRecord("Sub1", 42, 0.02, 9.1, 40.5, 1.2, 13.5, 25.6, 0.8, baseTimestamp);
        records[1] = new AirQualityRecord("Sub2", 58, 0.03, 10.2, 35.7, 2.1, 14.3, 29.1, 1.0, baseTimestamp);
        records[2] = new AirQualityRecord("Sub3", 55, 0.01, 7.3, 38.4, 1.4, 12.1, 24.7, 1.1, baseTimestamp);
        records[3] = new AirQualityRecord("Sub4", 45, 0.02, 8.9, 30.6, 1.7, 14.2, 26.3, 1.0, baseTimestamp);
        records[4] = new AirQualityRecord("Sub5", 50, 0.04, 9.8, 32.9, 1.6, 15.2, 28.1, 1.2, baseTimestamp);
        records[5] = new AirQualityRecord("Sub6", 70, 0.05, 11.4, 37.1, 2.3, 18.3, 32.4, 1.3, baseTimestamp);


        for (AirQualityRecord record : records) {
            String key = record.getLocation() + "_" + record.getTimestamp();
            avlTree.insert(key, record);
        }
    }

    @Test
    public void testHeight(){
        assertEquals(2, avlTree.getHeight());
    }

    @Test
    public void testElement(){
        List<String> inOrderTranversalResult = new ArrayList<>();
        avlTree.inOrderTraversal(avlTree.getRoot(), (key, value) -> inOrderTranversalResult.add(key));

        String convertedDate = dateFormat.format(new Date(1635724800 * 1000L));

        List<String> expectedInOrder = Arrays.asList("Sub1_" + convertedDate, "Sub2_"+ convertedDate, "Sub3_"+ convertedDate, "Sub4_"+ convertedDate, "Sub5_"+ convertedDate, "Sub6_"+ convertedDate);
        assertEquals(expectedInOrder, inOrderTranversalResult);

    }
}
