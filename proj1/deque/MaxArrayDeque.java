package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> cmp;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        cmp = c;
    }
    /* in this case, you define the comparing rule(the constructor when you create the arraydeque.
    and then you just use this rule to compare.
    eg. MaxArrayDeque<String> deque = new MaxArrayDeque<>(String.CASE_INSENSITIVE_ORDER);
    deque.add("apple");
    deque.add("banana");
    deque.add("Cherry");
    deque.max() would give you cherry.

     */

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T maxItem = this.get(0);
        for (T i: this) {
            if (cmp.compare(i, maxItem) > 0) {
                maxItem = i;
            }

        }
        return maxItem;
    }
    /*
    This case, you can use other comparing rules different
    from the one you used when creating the arraydeque.
      Comparator<String> lengthComparator = Comparator.comparing(String::length);
        String maxLength = deque.max(lengthComparator);
        this will give you banana since you changed the rule to lengthcomparator.

     */
    public T max(Comparator<T> c) {
        if (this.size() == 0) {
            return null;
        }
        T maxItem = this.get(0);
        for (T i: this) {
            if (c.compare(i,maxItem) > 0) {
                maxItem = i;
            }
        }
        return maxItem;
    }



}
