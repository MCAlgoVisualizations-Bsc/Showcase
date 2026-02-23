package io.github.mcalgovisualizations.visualization.renderer;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

public record LayoutEntry(Pos pos, int value) {
    @Override
    public @NotNull String toString() {
        return "LayoutEntry{" + "pos=" + pos + ", idx=" + value + '}';
    }
}
