package io.github.mcalgovisualizations.visualization.layouts;

import net.minestom.server.coordinate.Pos;

public record CircleLayout(double radius, double yOffset) implements Layout {
    public CircleLayout() {
        this(2.0, 2.0);
    }

    public CircleLayout (double radius) {
        this(radius, 2.0);
    }

    @Override
    public Pos[] compute(int size, Pos origin) {
        Pos[] positions = new Pos[size];

        double y = origin.y() + yOffset;

        if (size == 0) return positions;

        if (size == 1) {
            positions[0] = new Pos(origin.x(), y, origin.z());
            return positions;
        }

        for (int i = 0; i < size; i++) {
            double angle = (2.0 * Math.PI * i) / size;
            double x = origin.x() + (Math.cos(angle) * radius);
            double z = origin.z() + (Math.sin(angle) * radius);
            positions[i] = new Pos(x, y, z);
        }

        return positions;
    }

    @Override
    public Pos[] random(int size, Pos origin) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
