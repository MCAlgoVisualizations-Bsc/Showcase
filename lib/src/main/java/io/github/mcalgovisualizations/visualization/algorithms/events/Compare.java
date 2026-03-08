package io.github.mcalgovisualizations.visualization.algorithms.events;

import org.jetbrains.annotations.NotNull;

public record Compare(int x, int y) implements IAlgorithmEvent {
    @Override
    public @NotNull String toString() {
        return "Compare(" + x + "," + y + ")";
    }
}
