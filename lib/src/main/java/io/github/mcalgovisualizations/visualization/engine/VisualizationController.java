package io.github.mcalgovisualizations.visualization.engine;

import io.github.mcalgovisualizations.visualization.HistorySnapshot;
import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.algorithms.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;
import io.github.mcalgovisualizations.visualization.renderer.update.VisualizationRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A controller of time so forwards, back, adjusting speed belongs here.
 */
public class VisualizationController {

    private final AlgorithmStepper stepper;
    private final VisualizationRenderer renderer;
    private final List<Consumer<AlgorithmEvent>> eventListeners = new ArrayList<>();

    private int ticksPerStep = 20;
    private boolean IS_RUNNING = false;
    private Task runningTask = null;

    public VisualizationController(AlgorithmStepper stepper, VisualizationRenderer renderer) {
        this.stepper = stepper;
        this.renderer = renderer;
    }

    public void addEventListener(Consumer<AlgorithmEvent> listener) {
        eventListeners.add(listener);
    }

    public void onStart() {
        renderer.onStart();
        var snapshot = stepper.onStart();
        dispatchEvents(snapshot);
        renderer.render(snapshot);
    }

    public void start(Player player) {
        if (stepper.isDone()) {
            player.sendMessage(Component.text("Algorithm complete! Use randomize to restart.", NamedTextColor.YELLOW));
            return;
        }

        if(IS_RUNNING) return;
        IS_RUNNING = true;

        runningTask = MinecraftServer.getSchedulerManager()
                .buildTask(this::step)
                .repeat(Duration.ofMillis(this.ticksPerStep * 50L))
                .schedule();
    }

    public void stop() {
        IS_RUNNING = false;
        if(runningTask != null) {
            runningTask.cancel();
            runningTask = null;
        }
    }

    public void step() {
        final var snapshot = (HistorySnapshot) stepper.step();

        dispatchEvents(snapshot);
        renderer.render(snapshot);
        // TODO : handle history with snapshots
    }

    private void dispatchEvents(Snapshot snapshot) {
        for (AlgorithmEvent event : snapshot.events()) {
            for (Consumer<AlgorithmEvent> listener : eventListeners) {
                listener.accept(event);
            }
        }
    }

    public void back() {
        step();
//        final var snapshot = (HistorySnapshot) stepper.back();
//
//        if(renderer.isIdle())
//            renderer.render(snapshot);

        // TODO : handle history with snapshots
    }

    public void setSpeed(int ticksPerStep) {
        this.ticksPerStep = Math.max(1, ticksPerStep);
        // If running, restart with new speed
        if (IS_RUNNING) {
            if (runningTask != null) {
                runningTask.cancel();
            }
            runningTask = MinecraftServer.getSchedulerManager()
                    .buildTask(this::step)
                    .repeat(Duration.ofMillis(this.ticksPerStep * 50L))
                    .schedule();
        }
    }

    public void cleanup() {
        stop();
        renderer.onCleanup();
    }

    public void randomize() {
        stop();
        final var snapshot = stepper.randomize();
        renderer.render(snapshot);

        // TODO : handle history with snapshots
    }

}

