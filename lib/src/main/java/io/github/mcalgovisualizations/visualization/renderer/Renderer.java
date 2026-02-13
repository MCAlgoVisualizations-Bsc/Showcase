package io.github.mcalgovisualizations.visualization.renderer;

import io.github.mcalgovisualizations.visualization.SnapShot;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import net.minestom.server.instance.block.Block;

public interface Renderer {

    void render(SnapShot snapShot, Block block);

    void setLayout(Layout layout);

    /**
     * Used when destroying the whole visualization
     */
    void cleanup();

}
