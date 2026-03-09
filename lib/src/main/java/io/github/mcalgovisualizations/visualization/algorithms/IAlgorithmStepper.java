package io.github.mcalgovisualizations.visualization.algorithms;

public interface IAlgorithmStepper {
    ISnapshot step();
    ISnapshot back();
    ISnapshot onStart();
}
