package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.renderer.DisplayValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

import java.util.Objects;

public abstract class TextDisplay implements DisplayValue {
    protected final Instance instance;
    public final Pos pos;

    public TextDisplay(Instance instance, Pos pos) {
        Objects.requireNonNull(instance);
        Objects.requireNonNull(pos);
        this.instance = instance;
        this.pos = pos;
    }

}
