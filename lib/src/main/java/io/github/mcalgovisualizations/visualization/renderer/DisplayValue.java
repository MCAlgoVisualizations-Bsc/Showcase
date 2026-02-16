package io.github.mcalgovisualizations.visualization.renderer;

import net.minestom.server.coordinate.Pos;

public interface DisplayValue {
    void setInstance();
    void remove();
    void teleport(Pos pos);
    void setHighlighted(boolean highlighted);
    boolean isSpawned();
}
