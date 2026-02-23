package io.github.mcalgovisualizations.visualization.algorithms.events;

import org.jetbrains.annotations.NotNull;

public record Compare(int x, int y) implements AlgorithmEvent {
    @Override
    public @NotNull String toString() {
        return "Swap(" + x + "," + y + ")";
    }
}
