package io.github.mcalgovisualizations.visualization.renderer;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public interface DisplayValue {
    void setInstance(Instance instance);
    void remove();
    void teleport(Pos pos);
    void setHighlighted(boolean highlighted);
    boolean isSpawned();
}
