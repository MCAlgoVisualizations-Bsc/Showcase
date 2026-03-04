package io.github.mcalgovisualizations.visualization.algorithms;

@FunctionalInterface
public interface IPlayerSort {
    SortingCollection<?> sort(SortingCollection<?> values);
}
