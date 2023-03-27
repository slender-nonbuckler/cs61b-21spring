package deque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by JC.
 */


public class ArrayDequeTest {
    // YOUR TESTS HERE

    @Test
    public void add() {
        /* assertEquals for comparison of ints takes two arguments:
        assertEquals(expected, actual).
        if it is false, then the assertion will be false,
        and this test will fail.
        */
        ArrayDeque<Integer> A = new ArrayDeque<>();
        A.addLast(4);

        A.addLast(5);

        A.addFirst(6);
        A.addLast(7);
        A.addLast(8);
        A.addFirst(9);
        A.addLast(10);
        A.addLast(11);
        for(int i=0;i<A.size();i++){

            A.removeFirst();
            A.addLast(i);
        }

        Integer[] expected = { 6, 4, 5, 7, 8, 10, 11,9};

        for (int i = 0; i < expected.length; i++) {
            assertEquals("should have the same value", expected[i], A.get(i));
        }
    }

    @Test
    public void fillupandempty() {
        /* assertEquals for comparison of ints takes two arguments:
        assertEquals(expected, actual).
        if it is false, then the assertion will be false,
        and this test will fail.
        */
        ArrayDeque<Integer> A = new ArrayDeque<>();
        A.addLast(4);

        A.addLast(5);

        A.addFirst(6);
        A.addLast(7);
        A.addLast(8);
        A.addFirst(9);
        A.addLast(10);
        A.addFirst(11);


        System.out.println("Removed item: " + A.removeFirst()); // Debugging line


        System.out.println("Removed item: " + A.removeFirst()); // Debugging line

        A.removeLast();
        A.removeLast();
        A.removeFirst();
        A.removeFirst();
        A.removeLast();
        A.removeFirst();


        Integer[] expected = {null};

        for (int i = 0; i < expected.length; i++) {
            assertEquals("should have the same value", expected[i], A.get(i));
        }
    }


    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> A = new ArrayDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                A.addLast(randVal);


            } else if (operationNumber == 1) {
                //removeLast
                if (A.size() != 0) {
                    System.out.println("remove last" + A.removeLast());
                } else if (operationNumber == 2) {

                    // addfirst
                    int randVal = StdRandom.uniform(0, 100);
                    A.addFirst(randVal);
                } else if (operationNumber == 3) {
                    System.out.println("remove first" + A.removeFirst());
                }

            }
        }
        for (int i = 0; i < A.size(); i++) {
            System.out.println(A.get(i));
        }
        ;
        System.out.println("size is" + A.size());

    }

    @Test
    public void ADOBJECTEQUAL() {


        ArrayDeque<Integer> lld2 = new ArrayDeque<Integer>();

        ArrayDeque<Integer> o = new ArrayDeque<Integer>();
        int o1 = 1;
        ArrayDeque<Integer> o2 = new ArrayDeque<Integer>();
        assertEquals("Should have the same value1", lld2.equals(o1), false);
        assertEquals("Should have the same value2", lld2.equals(o2), true);
        System.out.print(lld2.equals(o));
        assertEquals("Should have the same value3", lld2.equals(o), true);
        lld2.addFirst(5);
        lld2.addFirst(23);
        lld2.addLast(42);
        o.addFirst(23);
        o.addLast(42);
        o.addLast(12);
        assertEquals("Should have the same value", lld2.equals(o), false);
    }
    @Test

    public void ADITeratortest() {


        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(5);
        lld1.addFirst(23);
        lld1.addLast(42);
        int pos = 7;
        //iteration
        for (int i : lld1) {
            System.out.println(i);
            if(pos==8){
                pos=0;
            }
            System.out.println(lld1.get(pos));
            assertEquals("Should have the same value", i, (int) lld1.get(pos));
            pos++;
        }
    }
}




