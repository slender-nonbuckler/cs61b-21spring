package deque;

import org.junit.Test;

import java.sql.SQLOutput;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {


        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }
    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void LDITeratortest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addFirst(5);
        lld1.addFirst(23);
        lld1.addLast(42);
        int pos = 1;
        //iteration
        for (int i : lld1) {
            System.out.println(i);

            System.out.println(lld1.get(pos));
            assertEquals("Should have the same value", i, (int) lld1.get(pos));
            pos++;
        }
    }
    @Test
        public void LDOBJECTEQUAL() {


            LinkedListDeque<Integer> lld2 = new LinkedListDeque<Integer>();

            LinkedListDeque<Integer> o = new LinkedListDeque<Integer>();
            int o1 = 1;
            ArrayDeque<Integer>o2=new ArrayDeque<Integer>();
            assertEquals("Should have the same value",lld2.equals(o1), false);
            assertEquals("Should have the same value",lld2.equals(o2), false);
            System.out.print(lld2.equals(o));
                assertEquals("Should have the same value",lld2.equals(o), true);
        lld2.addFirst(5);
        lld2.addFirst(23);
        lld2.addLast(42);
        o.addFirst(23);
        o.addLast(42);
        o.addLast(12);
        assertEquals("Should have the same value",lld2.equals(o), false);
            }

    @Test
    public void LLDgetrecursive() {
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<Integer>();
        assertEquals("Should have the same value",lld2.getRecursive(1), null);
        lld2.addFirst(5);
        assertEquals("Should have the same value",lld2.getRecursive(2), null);
        lld2.addFirst(23);
        lld2.addLast(42);
        assertEquals("Should have the same value",(int)lld2.getRecursive(3), 42);
        assertEquals("Should have the same value",lld2.getRecursive(-1), null);
    }




    }

