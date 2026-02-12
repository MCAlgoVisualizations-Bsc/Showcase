package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.SnapShot;

public interface AlgorithmStepper {

    /**
     * Execute one step forward in the algorithm.
     */
    SnapShot step();

    /**
     * Go back one step in the algorithm history.
     */
    SnapShot back();

    SnapShot randomize();

    boolean isDone();

}
