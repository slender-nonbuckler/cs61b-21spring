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


        Integer[] expected = {9,6,4, 5, 7, 8, 10, 11};

        for (int i=0;i<expected.length;i++) {
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

            System.out.println("Removed item: " +A.removeFirst()); // Debugging line


            System.out.println("Removed item: " + A.removeFirst()); // Debugging line

            A.removeLast();
            A.removeLast();
            A.removeFirst();
            A.removeFirst();
            A.removeLast();
            A.removeFirst();


            Integer[] expected = {null};

            for (int i=0;i<expected.length;i++) {
                assertEquals("should have the same value", expected[i], A.get(i));
            }
        }


    }


