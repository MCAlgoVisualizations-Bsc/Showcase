package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.IntListModel;
import net.minestom.server.coordinate.Pos;

public interface SortingLayout {
    Pos[] compute(IntListModel model, Pos origin);
}
