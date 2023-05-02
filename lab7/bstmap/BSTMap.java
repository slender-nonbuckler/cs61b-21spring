package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.*;


public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {

    private Node root;

    private int size = 0;

    /** Returns the value corresponding to KEY or null if no such value exists. */
    @Override
    public V get(K key) {
       return get(root,key);
    }
    /** Returns the Node in the BST of key-value pairs whose key
     *  is equal to KEY, or null if no such Node exists. */
    private V get(Node T,K k) {
        if (T == null ) {
            return null;
        }
        if (k.equals(T.key)) {
            return T.val;
        }
        else if (k.compareTo(T.key) < 0 ) {
            return get(T.left,k);
        } else {
            return get(T.right,k);
        }

    }

    @Override
    public int size() {
        return size;
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    /** Inserts the key-value pair of KEY and VALUE into this dictionary,
     *  replacing the previous value associated to KEY, if any. */
    @Override
    public void put(K key, V val) {
        root = put (root,key,val);

    }
    private Node put(Node T, K key, V val){
        if (T == null){
            size++;
            return new Node(key, val, null, null);

        }
       if (key.compareTo(T.key) < 0){
           T.left = put(T.left, key, val);
       }
       else if (key.compareTo(T.key) > 0) {
           T.right = put(T.right, key, val);
       }
       return T;
    }

    /** Returns true if and only if this dictionary contains KEY as the
     *  key of some key-value pair. */
    @Override
    public boolean containsKey(K key) {

        return (get(root,key) != null || root != null);
    }

    public void printInOrder() {
        printInOrder(root);
    }
    private void printInOrder(Node T){
        if (T == null){
            return;
        }

        //In order traversal
        printInOrder(T.left);
        System.out.println(T.key + ": " + T.val);
        printInOrder(T.right);

    }

    private class Node {

        /** Stores KEY as the key in this key-value pair, VAL as the value, and
         *  NEXT as the next node in the linked list. */
        Node(K k, V v, Node l,Node r) {
            key = k;
            val = v;
            left = l;
            right = r;
        }



        /** Stores the key of the key-value pair of this node in the list. */
        private K key;
        /** Stores the value of the key-value pair of this node in the list. */
        private V val;
        /** Stores the left node in the BST. */
        private Node left;
        /** Stores the right node in the BST. */
        private Node right;

    }
    @Override
    public Iterator<K> iterator() {
        return new BSTMapIter();
    }

    /** An iterator that iterates over the keys of the dictionary. */
    private class BSTMapIter implements Iterator<K> {

        /**
         * Create a new BSTMapIter by setting cur to the first node in the
         * linked list that stores the key-value pairs.
         */
        public BSTMapIter() {

            cur = root;
        }

        @Override
        public boolean hasNext() {

            return cur != null;
        }

        @Override
        public K next() {
            K ret = cur.key;
            if(cur.left != null){
                cur = cur.left;
            }
            else if(cur.right != null){
                cur = cur.right;
            }

            return ret;
        }

        /**
         * Stores the current key-value pair.
         */
        private Node cur;
    }
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

}
