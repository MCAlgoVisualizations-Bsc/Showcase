package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.Snapshot;

public interface AlgorithmStepper {

    /**
     * Execute one step forward in the algorithm.
     */
    Snapshot step();

    /**
     * Go back one step in the algorithm history.
     */
    Snapshot back();

    Snapshot randomize();

    boolean isDone();

    Snapshot onStart();
}
