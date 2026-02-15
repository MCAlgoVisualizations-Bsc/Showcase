package io.github.mcalgovisualizations.visualization.renderer;

import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.time.Duration;
import java.util.HashMap;

public class VisualizationScene {

    // stable id
    private final HashMap<Integer, DisplayValue> nodes = new HashMap<>();
    private final Pos origin;
    private final Instance instance;
    private final Layout layout;

    public VisualizationScene(Instance instance, Pos origin, Layout layout) {
        this.instance = instance;
        this.origin = origin;
        this.layout = layout;
    }

    /**
     * Spawns all the entities on algorithm startup
     * @param snapShot original layout
     */
    public void onStart(Snapshot snapShot) {
        final var values = snapShot.values();
        final var entries = layout.compute(values, origin);

        for (var layoutEntry : entries) {
            var dv = new BlockDisplay(layoutEntry.pos(), Block.GRANITE, Integer.toString(layoutEntry.value()));

            nodes.put(layoutEntry.value(), dv);
            dv.setInstance(instance);

            MinecraftServer.getSchedulerManager()
                    .buildTask(() -> dv.teleport(dv.getPos()))
                    .delay(Duration.ofMillis(100))
                    .schedule();
        }
    }

    public void render(Snapshot snapShot) {
        final var events = snapShot.events();

        // handle events, nodes are keyed to the value
    }

    public void cleanup() {
        for (var node : nodes.values()) node.remove();
        this.nodes.clear();
    }

}
