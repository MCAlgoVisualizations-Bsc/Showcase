package io.github.mcalgovisualizations.visualization.renderer.update;

import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.renderer.DisplayValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

import java.util.HashMap;
import java.util.Map;

public class VisualizationRenderer {

    private final Instance instance;
    private final Pos origin;
    private Layout layout;
    private VisualizationRenderer renderer;
    private SceneOps sceneOps;

    // index = stable identity
    private final Map<Integer, EventDispatcher> activeValues = new HashMap<>();

    public VisualizationRenderer(Instance instance, Pos origin, Layout layout) {
        this.instance = instance;
        this.origin = origin;
        this.layout = layout;
        this.sceneOps = new VisualizationScene();
    }

    public void onStart(Snapshot snapshot) {
        var v = snapshot.values();
        var computedLayout = layout.compute(v, origin);
        // compute should return a layoutResult
        var ctx = new RenderContext(sceneOps, new LayoutResult(), snapshot);
        // renderer.onStart(ctx);
    }

    public void step(Snapshot snapshot) {
        // renderer.step(ctx);
    }

    public void back() {
        // renderer.back();
    }

    public void onCleanup() {
        renderer.onCleanup();
        activeValues.clear();
    }
}
