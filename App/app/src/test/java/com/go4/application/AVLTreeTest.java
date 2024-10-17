package com.go4.application;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import com.go4.application.model.AirQualityRecord;
import com.go4.utils.tree.AVLTree;


import org.junit.Before;
import org.junit.Test;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 * This class provides unit tests for the AVLTree implementation using AirQualityRecord objects as values.
 * It tests the insertion and balancing of the AVLTree, as well as the traversal and height of the tree.
 * The test inserts 6 records in a specific order, where the values of the keys are designed to
 * trigger both single and double rotations in the AVLTree. This ensures that the AVLTree maintains
 * its balanced property.
 *
 * @author Gea Linggar
 */
public class AVLTreeTest {
    private AVLTree<String, AirQualityRecord> avlTree;
    private SimpleDateFormat dateFormat;
    private Context context;

    /**
     * Sets up the AVLTree and inserts several AirQualityRecord objects into the tree.
     * The keys used for the AVLTree are a combination of the location and timestamp of each record.
     */
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

    /**
     * Tests the height of the AVL tree after insertion. The expected height is 2.
     */
    @Test
    public void testHeight(){
        assertEquals(2, avlTree.getHeight());
    }

    /**
     * Tests the in-order traversal of the AVL tree. The traversal result is compared with the expected
     * list of keys formed from the AirQualityRecord objects.
     */
    @Test
    public void testElement(){
        List<String> inOrderTranversalResult = new ArrayList<>();
        avlTree.inOrderTraversal(avlTree.getRoot(), (key, value) -> inOrderTranversalResult.add(key));

        String convertedDate = dateFormat.format(new Date(1635724800 * 1000L));

        List<String> expectedInOrder = Arrays.asList("Sub1_" + convertedDate, "Sub2_"+ convertedDate, "Sub3_"+ convertedDate, "Sub4_"+ convertedDate, "Sub5_"+ convertedDate, "Sub6_"+ convertedDate);
        assertEquals(expectedInOrder, inOrderTranversalResult);

    }

    /**
     * Inserts the same AirQualityRecord objects but in reverse order (from large to small).
     * This test ensures that the AVLTree still maintains its balanced property when records are inserted
     * in descending order.
     */
    @Test
    public void testInsertAndBalanceReverseOrder(){
        long baseTimestamp = 1635724800;
        avlTree = new AVLTree<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Insert records in reverse order (Sub6 to Sub1)
        AirQualityRecord[] records = new AirQualityRecord[6];
        records[0] = new AirQualityRecord("Sub6", 70, 0.05, 11.4, 37.1, 2.3, 18.3, 32.4, 1.3, baseTimestamp);
        records[1] = new AirQualityRecord("Sub5", 50, 0.04, 9.8, 32.9, 1.6, 15.2, 28.1, 1.2, baseTimestamp);
        records[2] = new AirQualityRecord("Sub4", 45, 0.02, 8.9, 30.6, 1.7, 14.2, 26.3, 1.0, baseTimestamp);
        records[3] = new AirQualityRecord("Sub3", 55, 0.01, 7.3, 38.4, 1.4, 12.1, 24.7, 1.1, baseTimestamp);
        records[4] = new AirQualityRecord("Sub2", 58, 0.03, 10.2, 35.7, 2.1, 14.3, 29.1, 1.0, baseTimestamp);
        records[5] = new AirQualityRecord("Sub1", 42, 0.02, 9.1, 40.5, 1.2, 13.5, 25.6, 0.8, baseTimestamp);

        // Insert records in reverse order
        for (AirQualityRecord record : records) {
            String key = record.getLocation() + "_" + record.getTimestamp();
            avlTree.insert(key, record);
        }
    }

    /**
     * Tests the height of the AVL tree after reverse-order insertion.
     */
    @Test
    public void testHeightReverseOrder(){
        assertEquals(2, avlTree.getHeight());
    }

    /**
     * Tests the in-order traversal of the AVL tree after reverse-order insertion.
     * The traversal result should still produce a sorted list of keys in ascending order.
     */
    @Test
    public void testElementReverseOrder(){
        List<String> inOrderTranversalResult = new ArrayList<>();
        avlTree.inOrderTraversal(avlTree.getRoot(), (key, value) -> inOrderTranversalResult.add(key));

        String convertedDate = dateFormat.format(new Date(1635724800 * 1000L));

        // Expected in-order traversal should be sorted in ascending order, even though inserted in reverse
        List<String> expectedInOrder = Arrays.asList("Sub1_" + convertedDate, "Sub2_"+ convertedDate, "Sub3_"+ convertedDate, "Sub4_"+ convertedDate, "Sub5_"+ convertedDate, "Sub6_"+ convertedDate);
        assertEquals(expectedInOrder, inOrderTranversalResult);
    }
}
