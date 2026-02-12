package io.github.mcalgovisualizations.visualization.render;

import io.github.mcalgovisualizations.visualization.SnapShot;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.models.DataModel;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public interface Renderer {

    void render(SnapShot snapShot, Block block);

    void setLayout(Layout layout);

    /**
     * Used when destroying the whole visualization
     */
    void cleanup();

}
