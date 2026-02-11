package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.render.DisplayValue;
import io.github.mcalgovisualizations.visualization.utils.Utils;
import net.minestom.server.coordinate.Pos;

/**
 * Places array elements in a straight line along +X.
 * "Floating" = constant Y (no value-based height).
 */
public final class FloatingLinearLayout implements Layout {

    private final double spacing;
    private final double yOffset;
    private final double zOffset;

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
    public DisplayValue[] compute(DataModel model, Pos origin) {
        int n = model.size();
        DisplayValue[] values = new DisplayValue[n];

        double y = origin.y() + yOffset;
        double z = origin.z() + zOffset;

        for (int i = 0; i < n; i++) {
            double x = origin.x() + (i * spacing);
            var pos = new Pos(x, y, z);
            values[i] = new DisplayValue(pos, Utils.getBlockForValue(i), Integer.toString(i));
        }

        return values;
    }

}
