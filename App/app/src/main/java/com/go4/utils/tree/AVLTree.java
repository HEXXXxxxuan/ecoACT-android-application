package com.go4.utils.tree;

/**
 * The {@code AVLTree} class implements a self-balancing binary search tree.
 * The tree supports insertions, searches, and in-order traversal.
 *
 * @param <K> the type of the keys, which must implement {@code Comparable}
 * @param <T> the type of the values associated with the keys
 * @author u7902000 Gea Linggar
 */
public class AVLTree<K extends Comparable<K>, T>{
    /**
     * Inner class representing a node in the AVL tree.
     */
    class Node {
        K key;
        T value;
        Node left, right;
        int height;

        public Node(K key, T value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 0;
        }
    }

    private Node root;

    /**
     * Inserts a new key-value pair into the AVL tree.
     * @param key   the key to insert
     * @param value the value associated with the key
     */
    public void insert(K key, T value) {
        root = insertRec(root, key, value);
    }

    /**
     * Recursively inserts a new key-value pair into the subtree rooted at the specified node,
     * applying rotations if necessary to maintain balance.
     *
     * @param node  the root of the current subtree
     * @param key   the key to insert
     * @param value the value associated with the key
     * @return the new root of the subtree after insertion
     */
    private Node insertRec(Node node, K key, T value) {
        if (node == null) {
            return new Node(key, value);
        }

        if (key.compareTo(node.key) < 0) {
            node.left = insertRec(node.left, key, value);
        } else if (key.compareTo(node.key) > 0) {
            node.right = insertRec(node.right, key, value);
        } else {
            return node;
        }

        updateHeight(node);
        return applyRotation(node);
    }

    private void updateHeight(Node node) {
        int maxHeight = Math.max(calculateHeight(node.left), calculateHeight(node.right));
        node.height = maxHeight + 1;
    }

    private int calculateHeight(Node node) {
        if (node == null) return -1;
        else return node.height;
    }
    /**
     * Applies rotations to maintain the AVL tree balance after insertion.
     *
     * @param node the node to check for balance and apply rotations
     * @return the new root of the subtree after rotations
     */
    private Node applyRotation(Node node) {
        int balanceFactor = getBalanceFactor(node);

        if (balanceFactor > 1) {
            if (getBalanceFactor(node.left) < 0) {
                Node newLeftNode = rotateLeft(node.left);
                node.left = newLeftNode;
            }
            return rotateRight(node);
        }

        if (balanceFactor < -1) {
            // check for right-left situation
            if (getBalanceFactor(node.right) > 0) {
                Node newRightNode = rotateRight(node.right);
                node.right = newRightNode;
            }
            return rotateLeft(node);
        }

        return node;
    }

    /**
     * Rotates the subtree rooted at the given node to the right.
     *
     * @param node the root of the subtree to rotate
     * @return the new root after the rotation
     */
    private Node rotateRight(Node node) {
        Node leftNode = node.left;
        Node helperNode = leftNode.right;

        leftNode.right = node;
        node.left = helperNode;

        updateHeight(node);
        updateHeight(leftNode);

        return leftNode;
    }

    /**
     * Rotates the subtree rooted at the given node to the left.
     *
     * @param node the root of the subtree to rotate
     * @return the new root after the rotation\
     */
    private Node rotateLeft(Node node) {
        Node rightnode = node.right;
        Node helperNode = rightnode.left;

        rightnode.left = node;
        node.right = helperNode;

        updateHeight(node);
        updateHeight(rightnode);

        return rightnode;
    }

    private int getBalanceFactor(Node node) {
        if (node == null) {
            return 0;
        }
        return calculateHeight(node.left) - calculateHeight(node.right);
    }

    /**
     * Searches for the value associated with the given key in the AVL tree.
     *
     * @param key the key to search for
     * @return the value associated with the key, or {@code null} if the key is not found
     */
    public T search(K key) {
        Node result = searchRec(root, key);
        return (result != null) ? result.value : null;
    }

    /**
     * Recursively searches for the node with the specified key in the subtree rooted at the given node.
     *
     * @param node the root of the current subtree
     * @param key  the key to search for
     * @return the node with the specified key, or {@code null} if not found
     */
    private Node searchRec(Node node, K key) {
        if (node == null || node.key.equals(key)) {
            return node;
        }

        if (key.compareTo(node.key) < 0) {
            return searchRec(node.left, key);
        } else {
            return searchRec(node.right, key);
        }
    }

    /**
     * Performs an in-order traversal of the AVL tree.
     *
     * @param node   the root of the subtree to traverse
     * @param action the action to apply to each key-value pair
     */
    public void inOrderTraversal(Node node, java.util.function.BiConsumer<K, T> action) {
        if (node != null) {
            inOrderTraversal(node.left, action);
            action.accept(node.key, node.value);  // Abstract the action
            inOrderTraversal(node.right, action);
        }
    }

    public Node getRoot() {
        return root;
    }

    public int getHeight(){
        return calculateHeight(root);
    }
}
