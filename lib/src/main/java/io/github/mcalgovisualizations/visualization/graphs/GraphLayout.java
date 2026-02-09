package io.github.mcalgovisualizations.visualization.graphs;

import net.minestom.server.coordinate.Pos;

public interface GraphLayout {
    Pos[] compute(Graph graph, Pos origin);
}
