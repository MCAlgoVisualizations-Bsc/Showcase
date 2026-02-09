package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.AdjacencyList;
import net.minestom.server.coordinate.Pos;

public interface GraphLayout extends Layout {
    Pos[] compute(AdjacencyList adjacencyList, Pos origin);
}
