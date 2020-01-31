package pt.up.hs.linguini.utils;

import java.util.Objects;

/**
 * Pair of items.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Pair<T extends Comparable<T>, V extends Comparable<V>> implements Comparable<Pair<T, V>> {
    private T first;
    private V second;

    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) &&
                second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public int compareTo(Pair<T, V> o) {
        if (first.compareTo(o.first) == 0) {
            return second.compareTo(o.second);
        }
        return first.compareTo(o.first);
    }
}
