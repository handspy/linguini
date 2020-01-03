package pt.up.hs.linguini.utils;

import java.util.Objects;

/**
 * Pair of items of the same type.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class UnorderedPair<T extends Comparable<T>> implements Comparable<UnorderedPair<T>> {
    private final T a;
    private final T b;

    public UnorderedPair(T a, T b) {
        this.a = a;
        this.b = b;
    }

    public T getFirst() {
        return a;
    }

    public T getSecond() {
        return b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnorderedPair)) return false;
        UnorderedPair<?> that = (UnorderedPair<?>) o;
        return (Objects.equals(a, that.a) && Objects.equals(b, that.b)) ||
                (Objects.equals(a, that.b) && Objects.equals(b, that.a));
    }

    @Override
    public String toString() {
        return "(" + a + "," + b + ")";
    }

    @Override
    public int compareTo(UnorderedPair<T> o) {
        return equals(o) ? 0 : -1;
    }
}
