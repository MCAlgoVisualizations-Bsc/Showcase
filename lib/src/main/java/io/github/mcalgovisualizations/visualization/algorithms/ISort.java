package io.github.mcalgovisualizations.visualization.algorithms;

public interface ISort<T extends Comparable<T>> {
    void swap(int i, int j);
    boolean compare(int i, int j);
    T get(int i);
}
