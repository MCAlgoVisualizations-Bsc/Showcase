package io.github.mcalgovisualizations.visualization;

public record HistorySnapshot(
        int[] values,
        int[] highlights,
        int currentIndex,
        int compareIndex,
        boolean completed
) implements Snapshot { }
