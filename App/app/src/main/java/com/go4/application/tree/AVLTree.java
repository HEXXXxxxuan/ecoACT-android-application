package com.go4.application.tree;

import com.go4.application.historical.Record;

public class AVLTree {
    class Node {
        String key;
        Record value;
        Node left, right;
        int height;

        public Node(String key, Record value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 0;
        }
    }

    private Node root;

    public void insert(String key, Record value) {
        root = insertRec(root, key, value);
    }

    private Node insertRec(Node node, String key, Record value) {
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

    private Node rotateRight(Node node) {
        Node leftNode = node.left;
        Node helperNode = leftNode.right;

        leftNode.right = node;
        node.left = helperNode;

        updateHeight(node);
        updateHeight(leftNode);

        return leftNode;
    }

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

    public Record search(String key) {
        Node result = searchRec(root, key);
        return (result != null) ? result.value : null;
    }

    private Node searchRec(Node node, String key) {
        if (node == null || node.key.equals(key)) {
            return node;
        }

        if (key.compareTo(node.key) < 0) {
            return searchRec(node.left, key);
        } else {
            return searchRec(node.right, key);
        }
    }

}
