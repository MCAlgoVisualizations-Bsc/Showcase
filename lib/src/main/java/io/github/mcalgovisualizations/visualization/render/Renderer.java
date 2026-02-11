package io.github.mcalgovisualizations.visualization.render;

import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.models.DataModel;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public interface Renderer {

    void render(Pos[] values, Block block);


    void addHighlight(int id);
    void addHighlights(int[] ids);
    void setLayout(Layout layout);



    /**
     * Used when destroying the whole visualization
     */
    void cleanup();

    /**
     * clears the current visualization
     */
    void clear();
}
