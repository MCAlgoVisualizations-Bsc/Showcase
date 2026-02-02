package visualization.core;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all visualizations.
 * Provides common functionality like scheduling, history management, and state tracking.
 */
public abstract class AbstractVisualization implements Visualization {
    protected final String name;
    protected final Pos origin;
    protected final InstanceContainer instance;

    protected int ticksPerStep = 20; // 1 second default
    protected boolean running = false;
    protected Task runningTask = null;
    protected List<int[]> history = new ArrayList<>();
    protected int historyIndex = -1;

    public AbstractVisualization(String name, Pos origin, InstanceContainer instance) {
        this.name = name;
        this.origin = origin;
        this.instance = instance;
    }

    @Override
    public void start(Player player) {
        if (running) return;
        running = true;

        runningTask = MinecraftServer.getSchedulerManager()
                .buildTask(this::executeStep)
                .repeat(Duration.ofMillis(ticksPerStep * 50L))
                .schedule();
    }

    @Override
    public void stop() {
        running = false;
        if (runningTask != null) {
            runningTask.cancel();
            runningTask = null;
        }
    }

    @Override
    public void setSpeed(int ticksPerStep) {
        this.ticksPerStep = Math.max(1, ticksPerStep);
        // If running, restart with new speed
        if (running) {
            if (runningTask != null) {
                runningTask.cancel();
            }
            runningTask = MinecraftServer.getSchedulerManager()
                    .buildTask(this::executeStep)
                    .repeat(Duration.ofMillis(this.ticksPerStep * 50L))
                    .schedule();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Execute one step of the algorithm.
     * Called automatically when running or manually via stepForward().
     */
    protected abstract void executeStep();

    /**
     * Save the current state to history.
     */
    protected abstract void saveState();

    /**
     * Render the given state visually in the world.
     * @param state The array state to render
     */
    protected abstract void renderState(int[] state);
}
