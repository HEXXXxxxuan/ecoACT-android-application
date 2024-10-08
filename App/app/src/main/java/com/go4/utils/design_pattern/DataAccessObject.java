package com.go4.utils.design_pattern;

import android.content.Context;

import com.go4.utils.tree.AVLTree;

import java.util.List;

public interface DataAccessObject<T> {
    List<T> parseData(Context context, String fileName);
    AVLTree<String, T> createAVLTree(Context context, boolean useLocationOnly);

}
