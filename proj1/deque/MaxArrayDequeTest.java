package deque;

import jh61b.junit.In;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;
public class MaxArrayDequeTest {

    @Test

    public void comparatorstringlength() {
        Comparator<String>stringlength= Comparator.comparing(String::length);
        MaxArrayDeque<String> ad1 = new MaxArrayDeque<>(stringlength);
        ad1.addFirst("apple");
        ad1.addFirst("banana");
        ad1.addFirst("watermelon");
        assertEquals("should be the longest string", ad1.max(),"watermelon");

    }
    @Test

    public void comparatorstringinitial() {

        MaxArrayDeque<String> ad1 = new MaxArrayDeque<>(String.CASE_INSENSITIVE_ORDER);
        ad1.addFirst("apple");
        ad1.addFirst("banana");
        ad1.addFirst("cherry");
        assertEquals("should be the longest string", ad1.max(),"cherry");

    }
    @Test

    public void comparatorintmax() {
        Comparator<Integer> intmax = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return (o1-o2);
            }
        };
        MaxArrayDeque<Integer> ad1 = new MaxArrayDeque<>(intmax);
        for (int i = 0; i < 10000; i++) {
            ad1.addLast(i);
        }
        assertEquals("should be the longest string", (int)ad1.max(),9999);

    }

}
