package io.github.mcalgovisualizations.visualization.renderer.update;

import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.renderer.update.dispatch.AnimationPlan;
import io.github.mcalgovisualizations.visualization.renderer.update.dispatch.Dispatcher;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

import java.util.List;
import java.util.Objects;

public final class VisualizationRenderer {

    private final VisualizationScene scene;
    private final Layout layout;
    private final Dispatcher dispatcher;
    private final Executor executor;
    private final RenderSettings settings;

    private boolean started = false;

    public VisualizationRenderer(
            Instance instance,
            Pos origin, Layout layout,
            Dispatcher dispatcher,
            Executor executor,
            RenderSettings settings
    ) {
        this.scene = new VisualizationScene(instance, origin);
        this.layout = Objects.requireNonNull(layout, "layout");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
        this.executor = Objects.requireNonNull(executor, "executor");
        this.settings = settings != null ? settings : RenderSettings.defaults(); }

    public void onStart() {
        if (started) return;
        scene.onStart();
        executor.start();   // if your executor has a tick loop, start it here
        started = true;
    }

    /**
     * Stop animation activity but keep the scene alive so you can resume.
     * Typical use: controller.pause().
     */
    public void onStop() {
        if (!started) return;

        // Important: do NOT despawn entities here.
        // Just stop time-based activity and optionally clear queued work.
        executor.pause();        // or stopTickLoop()
        executor.clearQueue();   // optional; decide policy
    }

    public void render(Snapshot snapshot) {
        requireStarted();
        Objects.requireNonNull(snapshot, "snapshot");

        var values = snapshot.values();
        LayoutResult layoutResult = layout.compute(values);

        scene.ensureSize(values.length);

        for (int slot = 0; slot < values.length; slot++) {
            scene.setValue(slot, values[slot]);
        }

        scene.clearHighlights();
        for (int slot : snapshot.highlights()) {
            scene.setHighlighted(slot, true);
        }

        if (shouldHardSyncPositions(snapshot)) {
            for (int slot = 0; slot < values.length; slot++) {
                scene.moveSlotTo(slot, layoutResult.positionOf(slot));
            }
        }

        RenderContext ctx = new RenderContext(scene, layoutResult, snapshot, settings);

        for (AlgorithmEvent e : snapshot.events()) {
            AnimationPlan plan = dispatcher.dispatch(e, ctx);
            if (plan != null && !plan.isEmpty()) {
                executor.enqueue(plan);
            }
        }
    }

    public boolean isIdle() {
        return executor.isIdle();
    }

    /**
     * Full teardown. Not resumable.
     * Typical use: application shutdown / leaving visualization.
     */
    public void onCleanup() {
        if (!started) return;

        executor.stop();   // kill tick loop + clear queue
        scene.cleanUp();   // despawn entities
        started = false;
    }


    private boolean shouldHardSyncPositions(Snapshot snapshot) {
        // Keep this stupid-simple initially:
        // - hard sync if this is the first render
        // - hard sync if snapshot indicates reset/layoutSwap/backJump
        // You can add flags to Snapshot for these.
        // return snapshot.isFirstFrame() || snapshot.isHardReset(); // adapt to your snapshot flags
        return false;
    }
    private void requireStarted() { if (!started) throw new IllegalStateException("Renderer not started. Call onStart() first."); }

    // ... shouldHardSyncPositions + requireStarted unchanged ...
}
