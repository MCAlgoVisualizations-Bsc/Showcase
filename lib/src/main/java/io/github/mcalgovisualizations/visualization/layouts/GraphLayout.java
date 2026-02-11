package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.Graph;
import net.minestom.server.coordinate.Pos;

public interface GraphLayout extends Layout {
    Pos[] compute(Graph graph, Pos origin);
}
