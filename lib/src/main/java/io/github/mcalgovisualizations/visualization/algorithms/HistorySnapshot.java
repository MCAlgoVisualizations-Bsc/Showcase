package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.algorithms.events.IAlgorithmEvent;

import java.util.List;

public record HistorySnapshot(
        int[] values,
        int[] highlights,
        List<IAlgorithmEvent> events,
        int currentIndex,
        int compareIndex,
        boolean completed
) implements ISnapshot {

}
