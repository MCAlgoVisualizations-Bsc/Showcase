package io.github.mcalgovisualizations.visualization;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record HistorySnapshot(
        int[] values,
        int[] highlights,
        List<AlgorithmEvent> events,
        int currentIndex,
        int compareIndex,
        boolean completed
) implements SnapShot {

}
