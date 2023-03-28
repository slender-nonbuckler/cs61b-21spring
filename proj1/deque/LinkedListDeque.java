package deque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Intnode {
        private T item;
        private Intnode next;
        private Intnode prev;


        public Intnode(Intnode n, T i, Intnode m) {
            item = i;
            prev = n;
            next = m;
        }
    }

    private Intnode sentinel;
    private int size;

    public LinkedListDeque() {

        sentinel = new Intnode(null, null,null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }
    @Override
    public void addFirst(T x) {
        sentinel.next = new Intnode(sentinel, x, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }


    @Override
    public void addLast(T x) {
        size += 1;
        sentinel.prev = new Intnode(sentinel.prev, x, sentinel);
        sentinel.prev.prev.next = sentinel.prev;

    }
    @Override
    public void printDeque() {
        Intnode p = sentinel;
        for (int i = 1; i <= size; i++) {
            p = p.next;
            System.out.print(p.item + " ");
        }
    }
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T first = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;  //may need to check this
        size -= 1;
        return first;

    }
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        T last = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return last;
    }
    @Override
    public T get(int x) {
        Intnode p = sentinel;
        for (int i = 1; i <= x; i++){
            p = p.next;
        }
        return p.item;
    }

    private T getRecursivehelper (Intnode curr, int x){
        if (x == 1) {
            return curr.item;
        }
        return getRecursivehelper (curr.next,x - 1);
    }
     public T getRecursive(int x) {
        if (x < 1 || x > this.size) {
            return null;
        }

        return getRecursivehelper(sentinel.next, x);
     }


    @Override
    public int size() {
        return size;
    }

    public Iterator<T> iterator() {
        return new LDIterator();
    }
    private class LDIterator implements Iterator {
        Intnode wisP;
        public LDIterator() {
            wisP=sentinel.next;
        }
        public boolean hasNext() {
            return (wisP.item!=null);
        }

        public T next() {
            T returnitem = wisP.item;
            wisP = wisP.next;
            return returnitem;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)){
            return false;
        }
        LinkedListDeque<T> o1 = (LinkedListDeque <T> ) o;
        if(o1.size != this.size) {
            return false;
        }
        for(T i:o1 ) {
            int pos = 1;
            if (i != this.get(pos)) {
                return false;
            }
            pos += 1;
        }
            return true;

    }
}


