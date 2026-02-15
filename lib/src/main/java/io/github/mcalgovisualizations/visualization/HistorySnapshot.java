package io.github.mcalgovisualizations.visualization;

import java.util.List;

public record HistorySnapshot(
        int[] values,
        int[] highlights,
        List<AlgorithmEvent> events,
        int currentIndex,
        int compareIndex,
        boolean completed
) implements Snapshot {

}
