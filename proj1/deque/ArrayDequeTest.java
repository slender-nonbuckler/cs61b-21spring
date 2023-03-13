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


        Integer[] expected = {9, 6, 4, 5, 7, 8, 10, 11};

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
                    System.out.println("remove last"+A.removeLast());
                } else if (operationNumber == 2) {

                    // addfirst
                    int randVal = StdRandom.uniform(0, 100);
                    A.addFirst(randVal);
                } else if (operationNumber == 3) {
                    System.out.println("remove first"+A.removeFirst());
                }

            }
        }
        for(int i=0;i<A.size();i++){
        System.out.println(A.get(i));}
        System.out.println("size is"+A.size());

    }
}




