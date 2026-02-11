package io.github.mcalgovisualizations.visualization.models;

import java.util.Arrays;
import java.util.Random;

public record IntList(int[] data) implements DataModel {
    public IntList {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");
    }

    @Override
    public int size() {
        return data.length;
    }

    public void set(int i, int value) {
        data[i] = value;
    }

    public void swap(int i, int j) {
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    /**
     * Copy for snapshotting/history.
     */
    public int[] toArray() {
        return Arrays.copyOf(data, data.length);
    }

    public static IntList random(int n, int min, int maxInclusive, Random r) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = min + r.nextInt(maxInclusive - min + 1);
        }
        return new IntList(a);
    }
}
