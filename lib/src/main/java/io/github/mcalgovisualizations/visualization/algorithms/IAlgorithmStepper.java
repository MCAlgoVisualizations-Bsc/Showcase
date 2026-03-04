package io.github.mcalgovisualizations.visualization.algorithms;

public interface IAlgorithmStepper {

    /**
     * Execute one step forward in the algorithm.
     */
    ISnapshot step();

    /**
     * Go back one step in the algorithm history.
     */
    ISnapshot back();

    ISnapshot randomize();

    boolean isDone();

    ISnapshot onStart();
}
