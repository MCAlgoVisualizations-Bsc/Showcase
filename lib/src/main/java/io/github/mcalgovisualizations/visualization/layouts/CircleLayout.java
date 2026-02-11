package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.models.Graph;
import io.github.mcalgovisualizations.visualization.render.DisplayValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;

public final class CircleLayout implements Layout {

    private final double radius;
    private final double yOffset;

    /**
     * @param radius  radius of the circle in blocks
     * @param yOffset added to origin.y() (e.g. 1.0 puts nodes 1 block above origin)
     */
    public CircleLayout(double radius, double yOffset) {
        this.radius = radius;
        this.yOffset = yOffset;
    }

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

}
