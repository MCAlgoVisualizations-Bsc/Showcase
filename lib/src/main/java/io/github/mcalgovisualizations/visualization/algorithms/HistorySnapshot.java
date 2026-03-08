package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.algorithms.events.IAlgorithmEvent;
import io.github.mcalgovisualizations.visualization.models.Data;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record HistorySnapshot<T extends Comparable<T>>(
        @Nullable Data<T>[] values,
        @Nullable List<IAlgorithmEvent> events,
        int[] highlights,
        int currentIndex,
        int compareIndex
) implements ISnapshot<T> {
    @SuppressWarnings("unchecked")
    public Data<T>[] values() { return values == null ? new Data[0] : values; }
    public List<IAlgorithmEvent> events() { return events == null ? List.of() : List.copyOf(events); }
}
