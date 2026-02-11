package io.github.mcalgovisualizations.visualization.layouts;

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
     * @param size   number of elements to render
     * @param origin starting position of the layout
     * @return array of {@link Pos} positions in a straight line
     */
    @Override
    public Pos[] compute(int size, Pos origin) {
        Pos[] positions = new Pos[size];

        double y = origin.y() + yOffset;
        double z = origin.z() + zOffset;

        for (int i = 0; i < size; i++) {
            double x = origin.x() + (i * spacing);
            positions[i] = new Pos(x, y, z);
        }

        return positions;
    }

    @Override
    public Pos[] random(int size, Pos origin) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
