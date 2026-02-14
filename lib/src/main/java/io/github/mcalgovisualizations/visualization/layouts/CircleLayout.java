package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.renderer.LayoutEntry;
import net.minestom.server.coordinate.Pos;

public record CircleLayout(double radius, double yOffset) implements Layout {
    public CircleLayout() {
        this(2.0, 2.0);
    }

    public CircleLayout (double radius) {
        this(radius, 2.0);
    }

    @Override
    public LayoutEntry[] compute(int[] model, Pos origin) {
        var size = model.length;
        var out = new LayoutEntry[size];

        double y = origin.y() + yOffset;

        if (size == 0) return new LayoutEntry[0];

        if (size == 1) {
            out[0] = new LayoutEntry(origin, model[0]);
            return out;
        }

        for (int i = 0; i < size; i++) {
            double angle = (2.0 * Math.PI * i) / size;
            double x = origin.x() + (Math.cos(angle) * radius);
            double z = origin.z() + (Math.sin(angle) * radius);
            final var pos = new Pos(x, y, z);
            out[i] = new LayoutEntry(pos, model[i]);
        }

        return out;
    }

    @Override
    public LayoutEntry[] random(int[] model, Pos origin) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
