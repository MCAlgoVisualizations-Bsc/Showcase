package io.github.mcalgovisualizations.visualization.models;

import java.util.Arrays;
import java.util.Random;

/**
 * A mutable wrapper around an {@code int[]} that implements {@link DataModel}.
 * <p>
 * This model represents a list of integer values and provides basic
 * mutation utilities such as setting and swapping elements.
 * <p>
 * Note: Although declared as a record, the underlying array is mutable.
 *
 * @param data the backing array storing the integer values
 */
public record IntList(int[] data) implements DataModel {

    /**
     * Compact constructor with validation.
     *
     * @throws IllegalArgumentException if {@code data} is {@code null}
     */
    public IntList {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the length of the backing array
     */
    @Override
    public int size() {
        return data.length;
    }

    /**
     * Sets the value at the specified index.
     *
     * @param i     the index to modify
     * @param value the new value
     * @throws ArrayIndexOutOfBoundsException if {@code i} is out of range
     */
    public void set(int i, int value) {
        data[i] = value;
    }

    /**
     *
     */
    public int get(int i) {
        return data[i];
    }

    public int length() {
        return data.length;
    }

    /**
     * Swaps the values at the specified indices.
     *
     * @param i first index
     * @param j second index
     * @throws ArrayIndexOutOfBoundsException if either index is out of range
     */
    public void swap(int i, int j) {
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    /**
     * Returns a defensive copy of the underlying array.
     * <p>
     * Useful for snapshotting or maintaining history without exposing
     * the internal mutable array.
     *
     * @return a copy of the backing array
     */
    public int[] toArray() {
        return data;
    }

    /**
     * Creates a new {@link IntList} containing random integers within a range.
     *
     * @param n            number of elements to generate
     * @param min          minimum value (inclusive)
     * @param maxInclusive maximum value (inclusive)
     * @param r            source of randomness
     * @return a new {@link IntList} filled with random values
     *
     * @throws IllegalArgumentException if {@code n < 0} or {@code min > maxInclusive}
     */
    public static IntList random(int n, int min, int maxInclusive, Random r) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (min > maxInclusive) throw new IllegalArgumentException("min must be <= maxInclusive");
        if (r == null) throw new IllegalArgumentException("r cannot be null");

        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = min + r.nextInt(maxInclusive - min + 1);
        }
        return new IntList(a);
    }
}
