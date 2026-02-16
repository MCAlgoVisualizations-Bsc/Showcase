package io.github.mcalgovisualizations.visualization.renderer.update;

import io.github.mcalgovisualizations.visualization.renderer.BlockDisplay;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public final class VisualizationScene implements SceneOps {

    private final Instance instance;
    private final Pos origin;

    // Stable identity mapping (slot -> display wrapper/entity)
    private final Map<Integer, BlockDisplay> displaysBySlot =
            new HashMap<>();

    // Visual state
    private final Set<Integer> highlightedSlots = new HashSet<>();

    private boolean started = false;

    public VisualizationScene(Instance instance, Pos origin) {
        this.instance = instance;
        this.origin = origin;
    }

    @Override
    public void onStart() {
        this.started = true;
    }

    @Override
    public void cleanUp() {
        // Despawn/remove everything owned by this Scene
        for (var display : displaysBySlot.values()) {
            safeRemove(display);
        }
        displaysBySlot.clear();
        highlightedSlots.clear();
        started = false;
    }

    @Override
    public void setValue(int slot, int value) {
        assertStarted();
        var display = requireDisplay(slot);

        // Assumption: your BlockDisplay wrapper can render an int value.
        // Rename this to whatever your actual API is (setText, setValue, setBlock, etc.)
        display.setValue(value);
    }

    @Override
    public void setHighlighted(int slot, boolean highlighted) {
        assertStarted();
        var display = requireDisplay(slot);

        if (highlighted) {
            highlightedSlots.add(slot);
        } else {
            highlightedSlots.remove(slot);
        }

        display.setHighlighted(highlighted);
    }

    @Override
    public void clearHighlights() {
        assertStarted();

        // Turn off highlight visuals for all currently highlighted slots
        for (int slot : new HashSet<>(highlightedSlots)) {
            var display = displaysBySlot.get(slot);
            if (display != null) {
                display.setHighlighted(false);
            }
        }
        highlightedSlots.clear();
    }

    @Override
    public void moveSlotTo(int slot, Pos pos) {
        assertStarted();
        var display = requireDisplay(slot);

        // Assumption: teleport / setPosition exists
        display.teleport(pos);
    }

    @Override
    public void swapSlots(int a, int b) {
        assertStarted();

        var da = requireDisplay(a);
        var db = requireDisplay(b);

        displaysBySlot.put(a, db);
        displaysBySlot.put(b, da);

        setHighlighted(a, true);
        setHighlighted(b, true);

        // Swap highlight state too, so highlights stick to the *logical slot* or the *entity*?
        // Here we make highlights follow the entity (display), which is usually what you want during swaps.
        boolean aHighlighted = highlightedSlots.contains(a);
        boolean bHighlighted = highlightedSlots.contains(b);



        if (aHighlighted != bHighlighted) {
            if (aHighlighted) {
                highlightedSlots.remove(a);
                highlightedSlots.add(b);
            } else {
                highlightedSlots.remove(b);
                highlightedSlots.add(a);
            }
        }
    }

    @Override
    public void playEffect(int slot, String effectId) {
        assertStarted();
        // Keep this intentionally thin.
        // Effects should be implemented as Scene capabilities (particles, sounds, etc.)
        // and triggered by handlers via SceneOps.
        //
        // Example directions:
        // - instance.playSound(...)
        // - spawn particle at current display pos
        //
        // For now: no-op stub so handlers can call it safely.
    }

    /**
     * Ensures there is exactly one display per slot in [0, n).
     * Spawns missing, removes extras.
     *
     * This is a Scene responsibility (entity set correctness).
     */
    public void ensureSize(int n) {
        assertStarted();

        // Remove slots >= n
        var toRemove = new HashSet<Integer>();
        for (int slot : displaysBySlot.keySet()) {
            if (slot >= n) toRemove.add(slot);
        }
        for (int slot : toRemove) {
            var display = displaysBySlot.remove(slot);
            safeRemove(display);
            highlightedSlots.remove(slot);
        }

        // Create missing slots [0, n)
        for (int slot = 0; slot < n; slot++) {
            if (!displaysBySlot.containsKey(slot)) {
                // Spawn at origin initially; renderer will position it via moveSlotTo / base sync
                displaysBySlot.put(slot, createDisplay(origin));
            }
        }
    }

    private BlockDisplay createDisplay(Pos spawnPos) {
        // Assumption: BlockDisplay is your wrapper and can be constructed this way.
        // If not, adapt this factory.
        return new BlockDisplay(instance, spawnPos, Block.GLASS, "Hello");
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
