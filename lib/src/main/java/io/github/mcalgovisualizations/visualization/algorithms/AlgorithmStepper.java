package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.algorithms.sorting.SortingState;
import io.github.mcalgovisualizations.visualization.layouts.CircleLayout;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.models.DataModel;

public interface AlgorithmStepper {

    /**
     * Execute one step forward in the algorithm.
     */
    SortingState step();

    /**
     * Go back one step in the algorithm history.
     */
    SortingState back();

    int[] getRender();

    boolean isDone();
}
