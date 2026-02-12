package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.models.IntList;
import io.github.mcalgovisualizations.visualization.render.LayoutEntry;
import net.minestom.server.coordinate.Pos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public record FloatingLinearLayout(
        double spacing,
        double yOffset,
        double zOffset
) implements Layout {

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
        if (spacing <= 0) {
            throw new IllegalArgumentException("spacing must be > 0");
        }
    }

    /**
     * Computes a linear layout for {@code size} elements.
     *
     * @param origin starting position of the layout
     * @return array of {@link Pos} positions in a straight line
     */
    @Override
    public LayoutEntry[] compute(int[] model, Pos origin) {
        final var size = model.length;
        final var out = new LayoutEntry[size];

        final double y = origin.y() + yOffset;
        final double z = origin.z() + zOffset;

        for (int i = 0; i < size; i++) {
            final double x = origin.x() + (i * spacing);
            final var pos = new Pos(x, y, z);

            out[i] = new LayoutEntry(pos, model[i]);
        }

        return out;
    }

    @Override
    public LayoutEntry[] random(int[] model, Pos origin) {
        LayoutEntry[] layout = compute(model, origin);

        List<LayoutEntry> list = new ArrayList<>(Arrays.asList(layout));
        Collections.shuffle(list);

        return list.toArray(new LayoutEntry[0]);
    }
}
