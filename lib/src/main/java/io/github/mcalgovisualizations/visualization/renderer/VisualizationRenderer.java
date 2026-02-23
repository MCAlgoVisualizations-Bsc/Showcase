package io.github.mcalgovisualizations.visualization.renderer;

import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.algorithms.events.*;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.Dispatcher;
import io.github.mcalgovisualizations.visualization.renderer.handlers.*;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class VisualizationRenderer {

    private final VisualizationScene scene;
    private final Dispatcher dispatcher = new Dispatcher();
    private final Executor executor;
    private final Pos origin;

    public Layout layout;
    private boolean started = false;
    private boolean firstRender = true;

    public VisualizationRenderer(
            @NotNull Instance instance,
            @NotNull Pos origin
    ) {
        this.scene = new VisualizationScene(instance, origin);
        this.origin = origin;
        this.executor = new Executor(scene);
    }

    public void onStart() {
        if (started) return;
        started = true;
        dispatcher.register(Compare.class, new CompareHandler());
        dispatcher.register(Complete.class, new CompleteHandler());
        dispatcher.register(Message.class, new MessageHandler());
        dispatcher.register(Validate.class, new ValidateHandler());
        dispatcher.register(Swap.class, new SwapHandler());

    }

    /**
     * Stop animation activity but keep the scene alive so you can resume.
     * Typical use: controller.pause().
     */
    public void onStop() {
        if (!started) return;
        executor.pause();

    }


    public void render(Snapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        if (snapshot.events().contains(new Complete()) || snapshot.events().isEmpty()) return;
        if (firstRender && layout != null) {
            final var layoutResult = this.layout.compute(snapshot.values(), origin);
            scene.onStart(layoutResult);
            firstRender = false;
        }


        final var events = snapshot.events();
        var ctx = new RenderContext(scene, events);

        for (var e : events) {
            var plan = dispatcher.dispatch(e, ctx);
            executor.add(plan);
        }

        executor.setSpeed(2);
        executor.startIfIdle();

    }

    public boolean isIdle() {
        return true;
    }

    public void hardReset(Snapshot snapshot) {
        executor.onCleanup();
        scene.cleanUp();
        final var layoutResult = this.layout.compute(snapshot.values(), origin);
        scene.onStart(layoutResult);
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
}
