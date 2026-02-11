package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.models.Graph;
import io.github.mcalgovisualizations.visualization.render.DisplayValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;

/**
 * A {@link Layout} implementation that arranges model elements
 * evenly distributed along the circumference of a circle.
 * <p>
 * Each element is positioned at equal angular intervals around the circle,
 * centered at the provided origin.
 */
public final class CircleLayout implements Layout {

    private final double radius;
    private final double yOffset;

    /**
     * Creates a new circular layout.
     *
     * @param radius  Radius of the circle in blocks.
     * @param yOffset Vertical offset added to {@code origin.y()}.
     *                For example, {@code 1.0} places nodes one block above the origin.
     */
    public CircleLayout(double radius, double yOffset) {
        this.radius = radius;
        this.yOffset = yOffset;
    }

    /**
     * Computes the circular layout for the given {@link DataModel}.
     * <p>
     * Nodes are placed evenly spaced around a circle centered at {@code origin},
     * with the specified radius and vertical offset.
     * <p>
     * If the model contains zero or one element, the single node is placed
     * directly at the origin (with vertical offset applied).
     *
     * @param model  The data model providing the number of elements to layout.
     * @param origin The center point of the circular layout.
     * @return An array of {@link DisplayValue} objects positioned in a circle.
     */
    @Override
    public DisplayValue[] compute(DataModel model, Pos origin) {
        int n = model.size();
        var values = new DisplayValue[n];
        Pos pos = null;

        double y = origin.y() + yOffset;

        // Avoid division by zero; also makes single-node graph sane
        if (n <= 1) {
            pos = new Pos(origin.x(), y, origin.z());
            values[0] = new DisplayValue(pos, Block.GRANITE, "0");
            return values;
        }

        for (int v = 0; v < n; v++) {
            double angle = (2.0 * Math.PI * v) / n;
            double x = origin.x() + (Math.cos(angle) * radius);
            double z = origin.z() + (Math.sin(angle) * radius);
            pos = new Pos(x, y, z);
            values[v] = new DisplayValue(pos, Block.GRANITE, Integer.toString(v));
        }

        return values;
    }

    @Override
    public DisplayValue[] random(DataModel model, Pos origin) {
        throw new RuntimeException("Not implemented");
    }

}
