package io.github.mcalgovisualizations.visualization.algorithms.events;

import org.jetbrains.annotations.NotNull;

public record Validate() implements AlgorithmEvent {
    @Override
    public @NotNull String toString() {
        return "Validate()";
    }
}
