package visualization.core;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
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
public abstract class AbstractVisualization<T extends Comparable<T>> implements Visualization {
    protected final String name;
    protected final Pos origin;
    protected final InstanceContainer instance;

    protected int ticksPerStep = 20; // 1 second default
    protected boolean running = false;
    protected Task runningTask = null;
    protected List<DisplayValue<T>> values = new ArrayList<>();
    protected List<List<DisplayValue<T>>> history = new ArrayList<>();
    protected int historyIndex = -1;

    // Visual spacing constants
    private static final double SPACING = 2.0;           // Horizontal space between elements
    private static final double BASE_HEIGHT = 1.0;       // Base Y offset (above ground)
    private static final double HEIGHT_MULTIPLIER = 0.5; // How much height per value unit


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

    @Override
    public void cleanup() {
        stop();
        for (DisplayValue entity : values) {
            entity.remove();
        }
        values.clear();
    }

    /**
     * Execute one step of the algorithm.
     * Called automatically when running or manually via stepForward().
     */
    protected abstract void executeStep();

    /**
     * Save the current state to history.
     */
    protected void saveState() {
        // Clear future history if we stepped back and then started forward again
        while (history.size() > historyIndex + 1) {
            history.remove(history.size() - 1);
        }

        // Save a snapshot of the current values (not the DisplayValue objects themselves)
        List<DisplayValue<T>> snapshot = new ArrayList<>();
        for (DisplayValue<T> dv : values) {
            // We only care about the integer value for history
            // The entities stay the same
            snapshot.add(dv);
        }

        history.add(new ArrayList<>(values));
        historyIndex = history.size() - 1;
    }

    /**
     * Render the given state visually in the world.
     * @param state The array state to render
     */
    protected void renderState(List<DisplayValue<Integer>> state) {
        // DO NOT call cleanup() here!
        // Cleanup should only be used when destroying the whole visualization.

        for (int i = 0; i < state.size(); i++) {
            var displayVal = state.get(i);

            double x = origin.x() + (i * SPACING);
            double y = origin.y() + BASE_HEIGHT + (displayVal.getValue() * HEIGHT_MULTIPLIER);
            double z = origin.z();

            // Use teleport or edit position
            displayVal.teleport(new Pos(x, y, z));

            // updateMetadata(entity, i);
        }
    }
}
