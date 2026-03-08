package io.github.mcalgovisualizations.visualization.renderer;

import io.github.mcalgovisualizations.visualization.algorithms.ISnapshot;
import io.github.mcalgovisualizations.visualization.algorithms.events.*;
import io.github.mcalgovisualizations.visualization.layouts.ILayout;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.Dispatcher;
import io.github.mcalgovisualizations.visualization.renderer.handlers.*;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public final class VisualizationRenderer {

    private final VisualizationScene scene;
    private final Dispatcher dispatcher = new Dispatcher();
    private final Executor executor;
    private final Pos origin;
    private final ILayout layout;
    private boolean started = false;

    public VisualizationRenderer(
            @NotNull Instance instance,
            @NotNull Pos origin,
            @NotNull ILayout layout
    ) {

        this.scene = new VisualizationScene(instance, origin);
        this.layout = layout;
        this.origin = origin;
        this.executor = new Executor(scene);
    }

    @SuppressWarnings("unchecked")
    public void onStart(ISnapshot<?> snapshot) {
        if (started) return;
        started = true;
        dispatcher.register(Compare.class, new CompareHandler());
        dispatcher.register(Swap.class, new SwapHandler());
        dispatcher.register(Complete.class, new CompleteHandler());
        dispatcher.register(Message.class, new MessageHandler());
        dispatcher.register(Validate.class, new ValidateHandler());

        final var layoutResult = this.layout.compute(snapshot.values(), origin);
        scene.onStart(layoutResult);
    }

    /**
     * Stop animation activity but keep the scene alive so you can resume.
     * Typical use: controller.pause().
     */
    public void onStop() {
        if (!started) return;
        executor.pause();

    }

    public void  render(ISnapshot<?> snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        if (snapshot.events().contains(new Complete()) || snapshot.events().isEmpty()) return;

        final var events = snapshot.events();
        final var ctx = new RenderContext(scene, events);

        for (final var e : events) {
            final var plan = dispatcher.dispatch(e, ctx);
            executor.add(plan);
        }
        executor.startIfIdle();
    }

    public boolean isIdle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public void hardReset(ISnapshot<?> snapshot) {
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

    public void setAudience(Audience audience) {
        scene.setAudience(audience);
    }
}
