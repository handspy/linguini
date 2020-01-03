package pt.up.hs.linguini.analysis.ideadensity.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Representation of a tuple.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Tuple {
    private ArrayList<Object> items;

    public Tuple(Object...items) {
        this.items = new ArrayList<>(Arrays.asList(items));
    }

    public void add(Object item) {
        items.add(item);
    }

    public Object get(int index) {
        return items.get(index);
    }

    public int getInt(int index) {
        return (int) items.get(index);
    }

    public String getString(int index) {
        return (String) items.get(index);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "items=" + items +
                '}';
    }
}
