package io.github.mcalgovisualizations.visualization.engine;

import io.github.mcalgovisualizations.visualization.algorithms.HistorySnapshot;
import io.github.mcalgovisualizations.visualization.algorithms.IAlgorithmStepper;
import io.github.mcalgovisualizations.visualization.algorithms.IPlayerSort;
import io.github.mcalgovisualizations.visualization.algorithms.events.Complete;
import io.github.mcalgovisualizations.visualization.algorithms.sorting.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.models.Data;
import io.github.mcalgovisualizations.visualization.models.ISort;
import io.github.mcalgovisualizations.visualization.models.SortingCollection;
import io.github.mcalgovisualizations.visualization.renderer.VisualizationRenderer;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * A controller of time so forwards, back, adjusting speed belongs here.
 */
public class VisualizationController {

    private final AlgorithmStepper stepper;
    private final VisualizationRenderer renderer;

    private int ticksPerStep = 20;
    private boolean IS_RUNNING = false;
    private Task runningTask = null;

    public VisualizationController(
            IPlayerSort algorithm,
            VisualizationRenderer renderer,
            SortingCollection<?> collection
    ) {
        this.stepper = new AlgorithmStepper(algorithm, collection);
        this.renderer = renderer;
    }

    public void setAudience(Audience audience) {
        renderer.setAudience(audience);
    }

    public void onStart() {
        var event = stepper.onStart();
        renderer.onStart(event);
        //var values = collection.data().toArray(Data[]::new);

        // we just need to display the initial values
//        var snapshot = new HistorySnapshot<>(
//                values,
//                null
//        );

        // renderer.onStart(snapshot);
    }

    public void start() {
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
        renderer.onStop();
    }

    public void step() {
        final var event = stepper.step();
        renderer.render(event);
    }

    public void back() {
        final var event = stepper.back();
        renderer.render(event);
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
//        stop();
//        final var snapshot = stepper.randomize();
//        renderer.hardReset(snapshot);
    }

}