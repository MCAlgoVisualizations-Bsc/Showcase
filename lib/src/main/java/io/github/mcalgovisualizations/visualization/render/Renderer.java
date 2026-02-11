package io.github.mcalgovisualizations.visualization.render;

import io.github.mcalgovisualizations.visualization.models.DataModel;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public interface Renderer {
    /**
     *
     */
    void render(Instance instance, DisplayValue[] values);

    /**
     * Used when destroying the whole visualization
     */
    void cleanup();

    /**
     * clears the current visualization
     */
    void clear();
}
