package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {


    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */

    private static double loadfactor = 0.75;
    private static final int initCapacity = 4;
    private int n;
    //number of key-value pairs
    private int m;
    //number of buckets
    private Collection<Node>[] buckets;


    /**
     * Constructors
     */
    public MyHashMap() {
        this(initCapacity, loadfactor);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, loadfactor);

    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.m = initialSize;
         buckets = new Collection[initialSize];
        for (int i = 0; i < m; i++) {
            buckets[i] = createBucket();
        }
        this.loadfactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = new ArrayList<>();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        buckets = new Collection[initCapacity];
        for (int i = 0; i < initCapacity; i++) {
            buckets[i] = createBucket();
        }
        this.n = 0;
        this.m = initCapacity;
    }

    // hash function for keys - returns value between 0 and m-1
    private int hashtext(K key) {
        return (key.hashCode() & 0x7fffffff) % m;
    }

    // hash function for keys - returns value between 0 and m-1 (assumes m is a power of 2)
    // (from Java 7 implementation, protects against poor quality hashCode() implementations)
    private int hash(K key) {
        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12) ^ (h >>> 7) ^ (h >>> 4);
        return h & (m-1);
    }



    @Override
    public boolean containsKey(K key) {
        int i = hash(key);
        if (buckets[i] != null) {
            for (Node node : buckets[i]) {
                if (node.key.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public V get(K key) {
        int i = hash(key);
        if (buckets[i] != null) {
            for (Node node : buckets[i]) {
                if (node.key.equals(key)) {
                    return node.value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public void put(K key, V value) {
        if (n >= 0.75 * m) {
            resize(2 * m);
        }
        int i = hash(key);
        if (!containsKey(key)) {
            n++;
            Node node = new Node(key, value);
            buckets[i].add(node);
        } else {
            for (Node node : buckets[i]) {
                if (node.key.equals(key)) {
                    node.value = value;
                }
            }
        }
    }

    public void resize(int size) {
        MyHashMap<K, V> temp = new MyHashMap<>(size, loadfactor);
        for (int i = 0; i < m; i++) {
            for (Node node : buckets[i]) {
                temp.put(node.key, node.value);
            }
        }
        this.m = temp.m;
        this.n = temp.n;
        this.buckets = temp.buckets;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                set.add(node.key);
            }
        }
        return set;
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
    public Iterator<K> iterator() {
        return new MyHashMapIter();
    }

    private class MyHashMapIter implements Iterator<K> {
        public int currBucketIndex;
        public Iterator<Node> currentNodeIterator;

        public MyHashMapIter() {
            currBucketIndex = 0;
            currentNodeIterator = null;
        }

        @Override
        public boolean hasNext() {
            //check if node within the bucket
            if (currBucketIndex < m && !currentNodeIterator.hasNext()) {
                return true;
            }

            return true;
        }

        @Override
        public K next() {
            if (currentNodeIterator.hasNext()) {
                return currentNodeIterator.next().key;
            } else {
                    currBucketIndex++;
                    Collection<Node> currbucket = buckets[currBucketIndex];
                    currentNodeIterator = currbucket.iterator();
                    return currentNodeIterator.next().key;
                }
        }
    }
}

