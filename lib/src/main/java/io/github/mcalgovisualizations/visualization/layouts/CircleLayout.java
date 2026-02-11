package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.AdjacencyList;
import net.minestom.server.coordinate.Pos;

public final class CircleLayout implements GraphLayout {

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
    public Pos[] compute(AdjacencyList adjacencyList, Pos origin) {
        int n = adjacencyList.size();
        Pos[] pos = new Pos[n];

        double y = origin.y() + yOffset;

        // Avoid division by zero; also makes single-node graph sane
        if (n <= 1) {
            pos[0] = new Pos(origin.x(), y, origin.z());
            return pos;
        }

        for (int v = 0; v < n; v++) {
            double angle = (2.0 * Math.PI * v) / n;
            double x = origin.x() + (Math.cos(angle) * radius);
            double z = origin.z() + (Math.sin(angle) * radius);
            pos[v] = new Pos(x, y, z);
        }

        return pos;
    }

}
