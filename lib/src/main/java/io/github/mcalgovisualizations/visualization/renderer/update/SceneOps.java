package io.github.mcalgovisualizations.visualization.renderer.update;

import net.minestom.server.coordinate.Pos;

public interface SceneOps {

    // lifecycle
    void onStart();

    void cleanUp();

    // state
    void setValue(int slot, int value);

    void setHighlighted(int slot, boolean highlighted);

    void clearHighlights();

    // movement primitives
    void moveSlotTo(int slot, Pos position);

    void swapSlots(int a, int b);

    // optional extension point
    void playEffect(int slot, String effectId);
}

