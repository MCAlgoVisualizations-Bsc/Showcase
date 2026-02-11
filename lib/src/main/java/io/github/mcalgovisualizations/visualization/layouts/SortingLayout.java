package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.IntListModel;
import net.minestom.server.coordinate.Pos;

import java.io.Serializable;

public interface SortingLayout extends Serializable {
    Pos[] compute(IntListModel model, Pos origin);
}
