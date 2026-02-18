package io.github.mcalgovisualizations.visualization.renderer;

import io.github.mcalgovisualizations.visualization.algorithms.events.Message;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;

public interface SceneOps {

    // lifecycle
    void onStart(LayoutResult[] model);

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

    void sendMessage(String message, NamedTextColor color);
}

