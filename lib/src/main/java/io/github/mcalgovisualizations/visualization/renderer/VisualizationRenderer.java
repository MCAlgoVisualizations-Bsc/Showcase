package io.github.mcalgovisualizations.visualization.renderer;

import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class VisualizationRenderer implements Renderer {

    private final Instance instance;
    private final Pos origin;
    private Layout layout;

    // index = stable identity
    private final Map<Integer, DisplayValue> activeValues = new HashMap<>();

    public VisualizationRenderer(Instance instance, Pos origin, Layout layout) {
        if (layout == null) { throw new IllegalStateException("Layout is null"); }

        this.instance = instance;
        this.layout = layout;
        this.origin = origin;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public void render(Snapshot snapshot, Block block) {

        System.out.println(snapshot.events().size());
        for(var e : snapshot.events()) {
            System.out.println(e.toString());
        }

        final int[] model = snapshot.values();
        final var entries = layout.compute(model, origin);
        final int length = entries.length;

        // highlights should be indices
        final Set<Integer> highlighted = new HashSet<>();
        for (int h : snapshot.highlights()) {
            highlighted.add(h);
        }

        // Remove extra entities if array shrank
        for (Iterator<Map.Entry<Integer, DisplayValue>> it = activeValues.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, DisplayValue> e = it.next();
            if (e.getKey() >= length) {
                e.getValue().remove();
                it.remove();
            }
        }

        // Spawn missing (or first time)
        for (int i = 0; i < length; i++) {
            DisplayValue dv = activeValues.get(i);
            if (dv == null) {
                dv = new BlockDisplay(instance, entries[i].pos(), block, Integer.toString(entries[i].value()));
                //dv = new CircleDisplay(entries[i].pos(), 5, 20, block, 0);
                dv.setInstance();

                final Pos spawnPos = entries[i].pos();
                final DisplayValue spawnDv = dv;

                activeValues.put(i, dv);
            }
        }

        // Update all active entities (no respawn; no delayed teleports)
        for (int i = 0; i < length; i++) {
            var dv = (BlockDisplay) activeValues.get(i);

            dv.updateBlock(block);                 // safe if block changes; no-op if same
            dv.setValue(entries[i].value());
            dv.setHighlighted(highlighted.contains(i));
            dv.teleport(entries[i].pos());
        }
    }

    @Override
    public void cleanup() {
        for (DisplayValue dv : activeValues.values()) {
            dv.remove();
        }
        activeValues.clear();
    }


}
