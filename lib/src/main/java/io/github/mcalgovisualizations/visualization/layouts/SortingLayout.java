package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.IntList;
import net.minestom.server.coordinate.Pos;

public interface SortingLayout extends Layout {
    Pos[] compute(IntList model, Pos origin);
}
