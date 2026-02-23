package io.github.mcalgovisualizations.visualization;

import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;

import java.util.List;

public interface Snapshot {
    int[] values();
    int[] highlights();
    List<AlgorithmEvent> events();
}
