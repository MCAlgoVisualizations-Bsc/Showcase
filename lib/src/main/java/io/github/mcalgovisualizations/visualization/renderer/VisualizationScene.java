package io.github.mcalgovisualizations.visualization.renderer;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Scene = Minestom world state.
 *
 * Owns:
 *  - entity creation/removal
 *  - stable identity mapping slot -> display entity
 *  - current visual flags (highlight)
 *
 * Does NOT own:
 *  - layout math
 *  - algorithm events
 *  - scheduling/tick loop
 */
public final class VisualizationScene implements ISceneOps {

    private final Instance instance;
    private final Pos origin;
    public final List<Player> viewers = new ArrayList<>();
    private Audience audience = Audience.empty();

    public void setAudience(Audience audience) {
        this.audience = Objects.requireNonNullElse(audience, Audience.empty());
    }

    // Stable identity mapping (slot -> display wrapper/entity)
    private final Map<Integer, BlockDisplay> displaysBySlot =
            new HashMap<>();

    // Floating hologram above the visualization
    private HologramDisplay hologram;

    // Visual state
    private final Set<Integer> highlightedSlots = new HashSet<>();

    private boolean started = false;

    public VisualizationScene(@NotNull Instance instance, @NotNull Pos origin) {
        this.instance = instance;
        this.origin = origin;
    }

    @Override
    public void onStart(LayoutResult[] layoutResults) {
        this.started = true;

        for(var layout : layoutResults) {
            var pos = layout.pos();
            var block = net.minestom.server.instance.block.Block.GRANITE;
            var value = Integer.toString(layout.value());

            var dv = new BlockDisplay(instance, pos, block, value);
            displaysBySlot.put(layout.value(), dv);
            dv.setInstance();
            dv.teleport(pos);
        }

        // Create hologram floating 6 blocks above the origin
        hologram = new HologramDisplay(instance, origin.add(10, 5, 0));

        // Add viewers after all displays have been created
        displaysBySlot.values().forEach(display -> viewers.forEach(display::addViewer));
        viewers.forEach(hologram::addViewer);
    }

    @Override
    public void cleanUp() {
        // Despawn/remove everything owned by this Scene
        for (var display : displaysBySlot.values()) {
            safeRemove(display);
        }
        if (hologram != null) {
            hologram.remove();
            hologram = null;
        }
        clearGlowing();
        displaysBySlot.clear();
        started = false;
    }

    @Override
    public void setValue(int slot, int value) {
        assertStarted();
        var display = requireDisplay(slot);
        display.setValue(value);
    }

    @Override
    public void setHighlighted(int slot, boolean highlighted) {
        assertStarted();
        var display = requireDisplay(slot);

        highlightedSlots.add(slot);
        display.setGlowing(highlighted);
    }

    @Override
    public void clearGlowing() {
        assertStarted();

        // Turn off highlight visuals for all currently highlighted slots
        for (int slot : new HashSet<>(highlightedSlots)) {
            var display = displaysBySlot.get(slot);
            if (display != null) {
                display.setGlowing(false);
            }
        }
        highlightedSlots.clear();
    }

    @Override
    public void moveSlotTo(int slot, Pos pos) {
        assertStarted();
        var display = requireDisplay(slot);
        display.teleport(pos);
    }

    @Override
    public void swapSlots(int a, int b) {
        assertStarted();
        var da = requireDisplay(a);
        var db = requireDisplay(b);

        var posA = da.getPos();
        var posB = db.getPos();
        da.teleport(posB);
        db.teleport(posA);
    }

    @Override
    public void playEffect(int slot, String effectId) {
        assertStarted();

        viewers.forEach(viewer -> viewer.playSound(Sound.sound(
                Key.key("minecraft:block.note_block.pling"), Sound.Source.MASTER, 1.0f, 1.0f
        )));
    }


    @Override
    public void sendMessage(Component message) {
        audience.sendMessage(message);
    }

    @Override
    public void showHologram(Component text) {
        assertStarted();
        if (hologram != null) {
            hologram.setText(text);
        }
    }

    public void hoverDisplay(int slot, boolean hover) {
        assertStarted();
        var dv = requireDisplay(slot);
        if (hover) {
            dv.teleport(dv.getPos().add(0, 1, 0));
        } else {
            dv.teleport(dv.getPos().add(0, -1, 0));
        }
    }

    @Override
    public void stopAnimations() {
        clearGlowing();
        clearHologram();
        audience.sendMessage(Component.text("Algorithm complete."));
    }


    // -------------------------
    // Internals
    // -------------------------

    private BlockDisplay requireDisplay(int slot) {
        var display = displaysBySlot.get(slot);
        if (display == null) {
            // This is a usage bug: renderer should have called ensureSize first
            throw new IllegalStateException("No display for slot " + slot + ". Did you forget ensureSize(n)?");
        }
        return display;
    }

    private void safeRemove(BlockDisplay display) {
        if (display == null) return;
        try {
            display.remove();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void assertStarted() {
        if (!started) {
            throw new IllegalStateException("Scene not started. Call onStart() before using SceneOps.");
        }
    }
}
