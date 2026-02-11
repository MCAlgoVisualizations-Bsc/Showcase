package io.github.mcalgovisualizations.visualization.layouts;


import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.render.DisplayValue;
import net.minestom.server.coordinate.Pos;

public interface Layout {
    DisplayValue[] compute(DataModel model, Pos origin);
}
