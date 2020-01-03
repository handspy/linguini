package pt.up.hs.linguini.utils;

import java.util.Arrays;

/**
 * Utilities to deal with arrays.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ArrayUtils {

    public static <T> T[] concat(T[]...as) {
        if (as.length == 0) {
            throw new IllegalArgumentException("Nothing to concatenate.");
        }
        int i = 0;
        while (i < as.length && as[i].length == 0) i++;
        if (i >= as.length) {
            return as[0];
        }
        final int arrlen = Arrays.stream(as).mapToInt(a -> a.length).sum();
        final T[] result = (T[]) java.lang.reflect.Array.
                newInstance(as[i][0].getClass(), arrlen);
        int offset = 0;
        for (T[] a: as) {
            if (a.length == 0) {
                continue;
            }
            System.arraycopy(a, 0, result, offset, a.length);
            offset += a.length;
        }
        return result;
    }

    public static int[] concat(int[]...as) {
        if (as.length == 0) {
            throw new IllegalArgumentException("Nothing to concatenate.");
        }
        final int arrlen = Arrays.stream(as).mapToInt(a -> a.length).sum();
        final int[] result = new int[arrlen];
        int offset = 0;
        for (int[] a: as) {
            System.arraycopy(a, 0, result, offset, a.length);
            offset += a.length;
        }
        return result;
    }

    @SafeVarargs
    public static <T> T[] add(T[] arr, T...as) {
        if (as.length == 0) {
            return arr;
        }
        final int len = arr.length + as.length;
        final T[] result = (T[]) java.lang.reflect.Array.
                newInstance(as[0].getClass(), len);
        if (arr.length > 0) {
            System.arraycopy(arr, 0, result, 0, arr.length);
        }
        int offset = arr.length;
        for (T a: as) {
            System.out.println(a);
            result[offset++] = a;
        }
        return result;
    }

    public static int[] add(int[] arr, int...as) {
        final int len = arr.length + as.length;
        final int[] result = new int[len];
        System.arraycopy(arr, 0, result, 0, arr.length);
        int offset = arr.length;
        for (int a: as) {
            result[offset++] = a;
        }
        return result;
    }

    public static int[] remove(int[] a, int index) {
        if (a == null || index < 0 || index >= a.length) {
            return a;
        }

        int[] result = new int[a.length - 1];

        System.arraycopy(a, 0, result, 0, index);
        System.arraycopy(a, index + 1, result, index, a.length - index - 1);

        return result;
    }

    public static <T> T[] remove(T[] a, int index) {
        if (a == null || index < 0 || index >= a.length) {
            return a;
        }

        T[] result = (T[]) java.lang.reflect.Array.
                newInstance(a[0].getClass().getComponentType(), a.length - 1);

        System.arraycopy(a, 0, result, 0, index);
        System.arraycopy(a, index + 1, result, index, a.length - index - 1);

        return result;
    }
}
