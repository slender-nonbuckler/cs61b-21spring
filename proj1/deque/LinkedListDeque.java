package deque;

public class LinkedListDeque<T> {
    private class Intnode {
        public T item;
        public Intnode next;
        public Intnode prev;


        public Intnode( Intnode n,T i,Intnode m) {
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

    public void addFirst(T x) {
        sentinel.next= new Intnode( sentinel,x,sentinel.next);
        sentinel.next.next.prev=sentinel.next;
        size += 1;
    }

    public T getFirst() {
        return sentinel.next.item;
    }

    public void addLast(T x) {
        size += 1;
        sentinel.prev=new Intnode(sentinel.prev,x,sentinel);
        sentinel.prev.prev.next=sentinel.prev;

    }
    public boolean isEmpty(){
        if(size==0){
            return true;
        }
        else{
            return false;
        }
    }
    public void printDeque(){
        for(int i=1;i<=size;i++){
            Intnode p= sentinel;
            p=p.next;
            System.out.println(p.item);//may need to check this
        }
    }
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T first=sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;  //may need to check this
        size -= 1;
        return first;

    }
    public T removeLast(){
            if(isEmpty()) {
                return null;
            }
            T last=sentinel.prev.item;
            sentinel.prev=sentinel.prev.prev;
            sentinel.prev.next=sentinel;  //may need to check this
            size-=1;
            return last;
            }
    public T get(int x){
        Intnode p= sentinel;
        for(int i=1;i<=x;i++){

            p=p.next;
        }
        return p.item;
    }
    /*
    public int getRecursive(int x){
        if(x==0){
            return item;
        }
        return sentinel.next.getRecursive(x-1); // not sure what's wrong
        }

     */

    public int size() {
        return size;
    }
}


