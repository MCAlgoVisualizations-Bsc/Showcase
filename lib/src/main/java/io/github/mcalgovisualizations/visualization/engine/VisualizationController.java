package io.github.mcalgovisualizations.visualization.engine;

import io.github.mcalgovisualizations.visualization.algorithms.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.algorithms.sorting.SortingState;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.render.Renderer;
import io.github.mcalgovisualizations.visualization.render.VisualizationRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;

import java.time.Duration;

/**
 * A controller of time so forwards, back, adjusting speed belongs here.
 */
public class VisualizationController {

    private final AlgorithmStepper stepper;
    private final Renderer renderer;

    private int ticksPerStep = 20;
    private boolean IS_RUNNING = false;
    private Task runningTask = null;

    public VisualizationController(AlgorithmStepper stepper, Layout layout) {
        this.stepper = stepper;
        this.renderer = new VisualizationRenderer(layout);
    }

    void start(Player player) {
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

    void stop() {
        IS_RUNNING = false;
        if(runningTask != null) {
            runningTask.cancel();
            runningTask = null;
        }
    }

    void step() {
        SortingState state = stepper.step();

        // handle rendering im thinking something like converting the state to a DisplayValue[]
        // renderer.render( SortingState converted to DisplayValue[] );

        // handle history with snapshots
        // make the state into a snapshot which can be stored
    }

    void back() {
        stepper.back();
        // handle visual
    }

    void setSpeed(int ticksPerStep) {
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

    void cleanup() {
        stop();
        renderer.cleanup();

    }



}

