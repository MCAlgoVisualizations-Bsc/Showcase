package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.IntListModel;
import net.minestom.server.coordinate.Pos;

/**
 * Places array elements in a straight line along +X.
 * "Floating" = constant Y (no value-based height).
 */
public final class FloatingLinearLayout implements SortingLayout {

    private final double spacing;
    private final double yOffset;
    private final double zOffset;

    /** Matches your old AbstractVisualization defaults: spacing=2.0, yOffset=1.0, zOffset=0.0 */
    public FloatingLinearLayout() {
        this(2.0, 1.0, 0.0);
    }

    public FloatingLinearLayout(double spacing, double yOffset, double zOffset) {
        if (spacing <= 0) throw new IllegalArgumentException("spacing must be > 0");
        this.spacing = spacing;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    @Override
    public Pos[] compute(IntListModel model, Pos origin) {
        int n = model.size();
        Pos[] out = new Pos[n];

        double y = origin.y() + yOffset;
        double z = origin.z() + zOffset;

        for (int i = 0; i < n; i++) {
            double x = origin.x() + (i * spacing);
            out[i] = new Pos(x, y, z);
        }

        return out;
    }
}
