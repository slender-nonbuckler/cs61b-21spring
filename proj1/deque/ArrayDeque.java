package deque;

public class ArrayDeque<T> {
    private T[] items;
    //Next first and Next Last index
    private int NF;
    private int NL;
    private int size;
    private static int size0=8;

    /** Creates an empty list size of 8. */
    public ArrayDeque() {
        items = (T[]) new Object[size0];
        size = 0;
        NF=0;
        NL=0;
    }

    public boolean isEmpty(){
        if(size==0){
            return true;
        }
        else{
            return false;
        }
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        for(int i=1;i<=size;i++){
            System.out.println(items[i]);//may need to check this
        }
    }
    /*help function- minusone; return the index before the give index
    If the given index is 0, the next first is the first empty item.
     */
    public int minusone(int index){
        if (index == 0) {
            return items.length-1;
        }
        return index - 1;
    }
    /*help function- plusone; return the index after the give index
    If the given index is length-1, the next  is 0.
     */
    public int plusone(int index){
        if (index == items.length-1) {
            return 0;
        }
        return index + 1;
    }

    public void resizeup(int ca) {

        T[] a = (T[]) new Object[ca];
        System.arraycopy(items, NF+1, a, 0, size-NF-1);
        System.arraycopy(items,0,a,size-NF-1,NF+1);
        items = a;
    }
    public void resizedown() {
        T[] a = (T[]) new Object[items.length / 2];
        if (NF < NL) {
            System.arraycopy(items, NF+1, a, 0, size);
        } else {
            System.arraycopy(items, NF+1, a, 0, size-NF-1);
            System.arraycopy(items,0,a,size-NF-1,NF+1);

        }
        items=a;
    }
    public void addFirst(T x) {
        if (size == 0) {
            items[0] = x;
            NF=minusone(0);
            size++;
        }
        else {
            // check whether array full
            items[NF] = x;
            size++;
            if(size== items.length) {
                resizeup(items.length * 2);
            }
            //find the next first index
            while (items[NF] != null) {
                NF = minusone(NF);
            }
            //find the next last index
            while (items[NL] != null) {
                NL = plusone(NL);
            }
        }

    }

    public void addLast(T x) {
        if (size == 0) {
            items[0] = x;
            NL=plusone(0);
            size++;
        }
        else {
            // check whether array full

            items[NL] = x;
            size++;
            if(size== items.length) {
                resizeup(items.length * 2);
            }
            //find the next first index
            while (items[NF] != null) {
                NF = minusone(NF);
            }
            //find the next last index
            while (items[NL] != null) {
                NL = plusone(NL);
            }
        }

    }
    public T removeFirst(){
        if(size==0){
            return null;
        }
        else{
            NF = plusone(NF);
            T Returnitem = items[NF];
            items[NF] = null;
            size -= 1;
            if (items.length>size0 && size<items.length/4){
                resizedown();
            }
            return Returnitem;
        }
    }

    public T removeLast(){
        if(size==0){
            return null;
        }
        NL=minusone(NL);
        T Returnitem=items[NL];
        items[NL]=null;
        size-=1;
        if(items.length>size0 && size<items.length/4){
            resizedown();
        }
        return Returnitem;

    }
    public T get(int i){
        return items[i];
    }




    }

