package io.github.mcalgovisualizations.visualization.renderer.update;

import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.algorithms.events.Compare;
import io.github.mcalgovisualizations.visualization.algorithms.events.Swap;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.renderer.update.dispatch.AnimationPlan;
import io.github.mcalgovisualizations.visualization.renderer.update.dispatch.Dispatcher;
import io.github.mcalgovisualizations.visualization.renderer.update.handlers.CompareHandler;
import io.github.mcalgovisualizations.visualization.renderer.update.handlers.SwapHandler;
import io.github.mcalgovisualizations.visualization.renderer.update.Executor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

import java.util.Objects;

public final class VisualizationRenderer {

    private final VisualizationScene scene;
    private final Layout layout;
    private final Dispatcher dispatcher;
    private final Executor executor;
    private final Pos origin;
    //private final Object settings;

    private boolean started = false;

    public VisualizationRenderer(
            Instance instance,
            Pos origin, Layout layout,
            Dispatcher dispatcher,
            Executor executor // TODO : introduce executor
            //Object settings // TODO : introduce settings for ease, speed, etc.
    ) {
        this.scene = new VisualizationScene(instance, origin);
        this.origin = origin;
        this.layout = Objects.requireNonNull(layout, "layout");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
        this.executor = Objects.requireNonNull(executor, "executor");
        //this.settings = settings != null ? settings : RenderSettings.defaults();
    }




    public void onStart() {
        if (started) return;
        started = true;
        dispatcher.register(Swap.class, new SwapHandler());
        dispatcher.register(Compare.class, new CompareHandler());
    }

    /**
     * Stop animation activity but keep the scene alive so you can resume.
     * Typical use: controller.pause().
     */
    public void onStop() {
        if (!started) return;

        executor.pause();        // or stopTickLoop()
        //executor.clearQueue();   // optional; decide policy
    }
    private boolean test = false;
    public void render(Snapshot snapshot) {
        requireStarted();
        Objects.requireNonNull(snapshot, "snapshot");
        if (!test) {
            // move to onstart()
            final var layoutResult = this.layout.compute(snapshot.values(), origin);
            scene.onStart(layoutResult);

            test = true;
        }


//        Controller.tick()
//            -> stepper.next()                           // algorithm produces events
//            -> dispatcher.toAnimationPlans(events)      // translate events -> AnimationPlan(s)


        final var events = snapshot.events();
        var ctx = new RenderContext(scene, events);

        for (var e : events) {
            var plan = dispatcher.dispatch(e, ctx);
            executor.add(plan);
        }

        executor.step();
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

        executor.onCleanup();   // kill tick loop + clear queue
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

}
