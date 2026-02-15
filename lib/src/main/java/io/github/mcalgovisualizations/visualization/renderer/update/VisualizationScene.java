package io.github.mcalgovisualizations.visualization.renderer.update;

import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.renderer.BlockDisplay;
import io.github.mcalgovisualizations.visualization.renderer.DisplayValue;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class VisualizationScene implements SceneOps {

    final Set<Integer> highlightsById = new HashSet<>();

    @Override
    public void onStart() {

    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void setValue(int slot, int value) {

    }

    @Override
    public void setHighlighted(int slot, boolean highlighted) {

    }

    @Override
    public void clearHighlights() {

    }

    @Override
    public void moveSlotTo(int slot, Object position) {

    }

    @Override
    public void swapSlots(int a, int b) {

    }

    @Override
    public void playEffect(int slot, String effectId) {

    }

    public void onCleanup() {

    }
}
