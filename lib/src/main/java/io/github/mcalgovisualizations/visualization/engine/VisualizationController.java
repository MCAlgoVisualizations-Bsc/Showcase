package io.github.mcalgovisualizations.visualization.engine;

import io.github.mcalgovisualizations.visualization.HistorySnapshot;
import io.github.mcalgovisualizations.visualization.algorithms.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.renderer.Renderer;
import io.github.mcalgovisualizations.visualization.renderer.VisualizationScene;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;

import java.time.Duration;

/**
 * A controller of time so forwards, back, adjusting speed belongs here.
 */
public class VisualizationController {

    private final AlgorithmStepper stepper;
    private final VisualizationScene renderer;

    private int ticksPerStep = 20;
    private boolean IS_RUNNING = false;
    private Task runningTask = null;

    public VisualizationController(AlgorithmStepper stepper, VisualizationScene renderer) {
        this.stepper = stepper;
        this.renderer = renderer;
    }

    public void onStart() {
        var snapshot = stepper.start();
        renderer.start(snapshot);
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

        renderer.render(snapshot);

        // handle history with snapshots
        // make the state into a snapshot which can be stored
    }

    public void back() {
        final var snapshot = (HistorySnapshot) stepper.back();

        renderer.render(snapshot);

        // handle history with snapshots
        // make the state into a snapshot which can be stored
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
        renderer.cleanup();
    }

    public void randomize() {
        stop();
        final var snapshot = stepper.randomize();
        renderer.render(snapshot);

        // TODO : Save snapshot in history !
    }

}

