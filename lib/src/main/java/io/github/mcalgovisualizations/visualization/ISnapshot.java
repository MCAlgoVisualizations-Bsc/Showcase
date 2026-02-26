package io.github.mcalgovisualizations.visualization;

import io.github.mcalgovisualizations.visualization.algorithms.events.IAlgorithmEvent;

import java.util.List;

public interface ISnapshot {
    int[] values();
    int[] highlights();
    List<IAlgorithmEvent> events();
    boolean completed();
}
