package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.Data;
import io.github.mcalgovisualizations.visualization.renderer.LayoutResult;
import net.minestom.server.coordinate.Pos;

public interface ILayout {
    <T extends Comparable<T>> LayoutResult[] compute(Data<T>[] model, Pos origin);
}
