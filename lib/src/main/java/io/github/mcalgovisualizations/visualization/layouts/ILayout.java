package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.Data;
import io.github.mcalgovisualizations.visualization.renderer.LayoutResult;
import net.minestom.server.coordinate.Pos;

public interface ILayout {
    /**
     * Computes the positions and visual representations for the given data model.
     *
     * @param origin the reference position used as the layout's anchor or center
     * @return an array of {@link Pos} objects representing positioned elements
     */
    <T extends Comparable<T>> LayoutResult[] compute(Data<T>[] model, Pos origin);
}
