package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.render.LayoutEntry;
import net.minestom.server.coordinate.Pos;

/**
 * Defines a strategy for positioning visual elements based on a {@link DataModel}.
 * <p>
 * Implementations determine how elements are spatially arranged relative to
 * a given origin point.
 */
public interface Layout {
    /**
     * Computes the positions and visual representations for the given data model.
     *
     * @param origin the reference position used as the layout's anchor or center
     * @return an array of {@link Pos} objects representing positioned elements
     */
    LayoutEntry[] compute(int[] model, Pos origin);

    /**
     * Computes a randomized layout for the given {@link DataModel}.
     * <p>
     * Implementations may use randomness to determine element positions,
     * typically relative to the provided origin.
     *
     * @param model  the data model defining how many elements to generate
     * @param origin the reference position used as the layout's anchor
     * @return an array of {@link Pos} objects positioned randomly
     */
    LayoutEntry[] random(int[] model, Pos origin);

}
