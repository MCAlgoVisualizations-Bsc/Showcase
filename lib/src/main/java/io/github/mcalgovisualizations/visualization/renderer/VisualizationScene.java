package io.github.mcalgovisualizations.visualization.renderer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
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

    private Entity statusHologram;

    private boolean started = false;

    public VisualizationScene(Instance instance, Pos origin) {
        this.instance = instance;
        this.origin = origin;
    }

    @Override
    public void onStart(LayoutResult[] layoutResults) {
        this.started = true;

        this.statusHologram = new Entity(EntityType.TEXT_DISPLAY);
        var meta = (TextDisplayMeta) this.statusHologram.getEntityMeta();
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);;
        meta.setBackgroundColor(0x40000000); // semi-transparent black
        meta.setScale(new Vec(3.0)); // Make text bigger
        meta.setHasNoGravity(true); // Prevent falling

        // Position higher up
        this.statusHologram.setInstance(instance, origin.add(9, 6, 0));

        for(var layout : layoutResults) {
            var pos = layout.pos();
            var block = Block.GRANITE;
            var value = Integer.toString(layout.value());

            var dv = new BlockDisplay(instance, pos, block, value);
            displaysBySlot.put(layout.value(), dv);
            dv.setInstance();
            dv.teleport(pos);
        }
    }

    @Override
    public void cleanUp() {
        if (statusHologram != null) {
            statusHologram.remove();
            statusHologram = null;
        }

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

        var posA = da.getPos();
        var posB = db.getPos();

        da.teleport(posB);
        db.teleport(posA);

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

    public void sendMessage(String message, NamedTextColor color) {
        // player.sendMessage(Component.text(msg.message(), color));
    }

    @Override
    public void setStatusText(String text, NamedTextColor color) {
        assertStarted();
        if (statusHologram != null) {
            var meta = (TextDisplayMeta) statusHologram.getEntityMeta();
            meta.setText(Component.text(text, color));
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
