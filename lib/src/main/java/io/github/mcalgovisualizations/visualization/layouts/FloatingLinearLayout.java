package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.render.DisplayValue;
import io.github.mcalgovisualizations.visualization.utils.Utils;
import net.minestom.server.coordinate.Pos;

/**
 * A {@link Layout} implementation that arranges elements in a straight
 * horizontal line along the positive X-axis.
 * <p>
 * All elements share a constant Y-coordinate ("floating"), meaning their
 * vertical position does not depend on the element value.
 * <p>
 * Elements are spaced evenly using the configured {@code spacing}.
 *
 * @param spacing distance in blocks between consecutive elements (must be > 0)
 * @param yOffset vertical offset added to {@code origin.y()}
 * @param zOffset depth offset added to {@code origin.z()}
 */
public record FloatingLinearLayout(double spacing, double yOffset, double zOffset) implements Layout {

    /**
     * Creates a floating linear layout with default configuration:
     * <ul>
     *     <li>spacing = 2.0</li>
     *     <li>yOffset = 1.0</li>
     *     <li>zOffset = 0.0</li>
     * </ul>
     */
    public FloatingLinearLayout() {
        this(2.0, 1.0, 0.0);
    }

    /**
     * Compact constructor with validation.
     *
     * @throws IllegalArgumentException if {@code spacing <= 0}
     */
    public FloatingLinearLayout {
        if (spacing <= 0)
            throw new IllegalArgumentException("spacing must be > 0");
    }

    /**
     * Computes a linear layout for the given {@link DataModel}.
     * <p>
     * Elements are placed sequentially along the +X axis starting from
     * {@code origin.x()}, spaced evenly by {@code spacing}.
     * The Y and Z coordinates remain constant (based on offsets).
     *
     * @param model  the data model defining how many elements to render
     * @param origin the starting position of the layout
     * @return an array of {@link DisplayValue} positioned in a straight line
     */
    @Override
    public DisplayValue[] compute(DataModel model, Pos origin) {
        int n = model.size();
        DisplayValue[] values = new DisplayValue[n];

        double y = origin.y() + yOffset;
        double z = origin.z() + zOffset;

        for (int i = 0; i < n; i++) {
            double x = origin.x() + (i * spacing);
            var pos = new Pos(x, y, z);
            values[i] = new DisplayValue(
                    pos,
                    Utils.getBlockForValue(i),
                    Integer.toString(i)
            );
        }

        return values;
    }

    @Override
    public DisplayValue[] random(DataModel model, Pos origin) {
        throw new RuntimeException("Not implemented");
    }

}
