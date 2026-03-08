package io.github.mcalgovisualizations.visualization.renderer;

import io.github.mcalgovisualizations.visualization.models.Data;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

public record LayoutResult<T extends Comparable<T>>(Data<T> value, Pos pos) {
    @Override
    public @NotNull String toString() {
        return "LayoutEntry{" + "pos=" + pos + ", idx=" + value.value() + '}';
    }
}
