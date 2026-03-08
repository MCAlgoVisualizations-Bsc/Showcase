package io.github.mcalgovisualizations.visualization.models;

public interface ISort<T extends Comparable<T>> {
    void swap(int i, int j);
    int compare(int i, int j);
    T get(int i);
}
