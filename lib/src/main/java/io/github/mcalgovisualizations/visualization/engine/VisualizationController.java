package io.github.mcalgovisualizations.visualization.engine;

import io.github.mcalgovisualizations.visualization.algorithms.HistorySnapshot;
import io.github.mcalgovisualizations.visualization.algorithms.IAlgorithmStepper;
import io.github.mcalgovisualizations.visualization.algorithms.IPlayerSort;
import io.github.mcalgovisualizations.visualization.algorithms.SortingCollection;
import io.github.mcalgovisualizations.visualization.algorithms.sorting.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.renderer.VisualizationRenderer;
import io.github.mcalgovisualizations.visualization.renderer.handlers.SystemMessages;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.Objects;

/**
 * A controller of time so forwards, back, adjusting speed belongs here.
 */
public class VisualizationController {

    private final IAlgorithmStepper stepper;
    private final VisualizationRenderer renderer;

    private Audience audience = Audience.empty();
    private int ticksPerStep = 20;
    private boolean IS_RUNNING = false;
    private Task runningTask = null;

    public VisualizationController(IPlayerSort algorithm, VisualizationRenderer renderer) {
        this.stepper = new AlgorithmStepper(algorithm);
        this.renderer = renderer;
    }

    public void setAudience(Audience audience) {
        this.audience = Objects.requireNonNullElse(audience, Audience.empty());
        renderer.setAudience(audience);  // propagate down
    }





    public void onStart() {
        var snapshot = stepper.onStart();
        renderer.onStart(snapshot);
    }

    public void start() {
        if(IS_RUNNING) return;
        IS_RUNNING = true;

//        if (stepper.isDone()) {
//            SystemMessages.sendTo(audience, SystemMessages.ALGORITHM_COMPLETE);
//            return;
//        }

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
        final var snapshot = (HistorySnapshot) stepper.step();

        renderer.render(snapshot);

    }

    public void back() {
        final var snapshot = (HistorySnapshot) stepper.back();

        renderer.render(snapshot);

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