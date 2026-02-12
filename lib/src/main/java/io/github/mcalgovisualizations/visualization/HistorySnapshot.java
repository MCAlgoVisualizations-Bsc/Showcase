package io.github.mcalgovisualizations.visualization;

import java.util.HashSet;
import java.util.Set;

public record HistorySnapshot(
        int[] values,
        int[] highlights,
        int currentIndex,
        int compareIndex,
        boolean completed
) implements SnapShot { }
