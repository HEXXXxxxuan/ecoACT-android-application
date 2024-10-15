package com.go4.utils.design_pattern;

import android.content.Context;

import com.go4.utils.tree.AVLTree;

import java.util.List;

/**
 * Theis interface provides an abstraction for
 * parsing data from a file and creating an AVL Tree from the parsed data.
 *
 * @param <T> the type of the data objects that will be handled by this DAO
 * @author u7902000 Gea Linggar
 */
public interface DataAccessObject<T> {
    List<T> parseData(Context context, String fileName);
    AVLTree<String, T> createAVLTree(Context context, boolean useLocationOnly);

}
