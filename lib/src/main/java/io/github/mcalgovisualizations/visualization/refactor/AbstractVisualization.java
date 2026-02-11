package io.github.mcalgovisualizations.visualization.refactor;


import io.github.mcalgovisualizations.visualization.render.DisplayValue;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for all visualizations.
 * Provides common functionality like scheduling, history management, and state tracking.
 */
public abstract class AbstractVisualization<T extends Comparable<T>> /* implements Visualization  */{
    protected final String name;
    protected final net.minestom.server.coordinate.Pos origin;
    protected final InstanceContainer instance;
    protected boolean algorithmComplete = false;
    private final Random random = new Random();


    protected int ticksPerStep = 20; // 1 second default
    protected boolean running = false;
    protected Task runningTask = null;
    protected List<DisplayValue> values = new ArrayList<>();
    protected final List<List<DisplayValue>> history = new ArrayList<>();
    protected int historyIndex = -1;

    // Visual spacing constants
    private static final double SPACING = 2.0;           // Horizontal space between elements
    private static final double BASE_HEIGHT = 1.0;       // Base Y offset (above ground)
    private static final double HEIGHT_MULTIPLIER = 0.5; // How much height per value unit


    public AbstractVisualization(String name, List<DisplayValue> values, net.minestom.server.coordinate.Pos origin, InstanceContainer instance) {
        this.name = name;
        this.origin = origin;
        this.instance = instance;
        this.values = values;
    }

    // @Override
    public void start(Player player) {
        if (running) return;
        running = true;

        runningTask = MinecraftServer.getSchedulerManager()
                .buildTask(this::stepForward)
                .repeat(Duration.ofMillis(ticksPerStep * 50L))
                .schedule();
    }

    // @Override
    public void stop() {
        running = false;
        if (runningTask != null) {
            runningTask.cancel();
            runningTask = null;
        }
    }

    // @Override
    public void setSpeed(int ticksPerStep) {
        this.ticksPerStep = Math.max(1, ticksPerStep);
        // If running, restart with new speed
        if (running) {
            if (runningTask != null) {
                runningTask.cancel();
            }
            runningTask = MinecraftServer.getSchedulerManager()
                    .buildTask(this::stepForward)
                    .repeat(Duration.ofMillis(this.ticksPerStep * 50L))
                    .schedule();
        }
    }

    // @Override
    public boolean isRunning() {
        return running;
    }

    // @Override
    public String getName() {
        return name;
    }

    // @Override
    public void cleanup() {
        stop();
        for (DisplayValue entity : values) {
            entity.remove();
        }
        values.clear();
    }


    public void randomize() {
        stop();
        algorithmComplete = false;

        history.clear();
        historyIndex = -1;
        // for (var v : values) v.remove();

        Collections.shuffle(values);

        saveState(); // Save initial state
        renderState();
    }

    /**
     * Get a colored wool block based on the value.
     * Higher values get "warmer" colors.
     */
    protected static Block getBlockForValue(int value) {
        return switch (value % 10) {
            case 0 -> Block.RED_WOOL;
            case 1 -> Block.WHITE_WOOL;
            case 2 -> Block.LIGHT_GRAY_WOOL;
            case 3 -> Block.YELLOW_WOOL;
            case 4 -> Block.ORANGE_WOOL;
            case 5 -> Block.PINK_WOOL;
            case 6 -> Block.MAGENTA_WOOL;
            case 7 -> Block.PURPLE_WOOL;
            case 8 -> Block.BLUE_WOOL;
            case 9 -> Block.CYAN_WOOL;
            default -> Block.BLACK_WOOL;
        };
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
        List<DisplayValue> snapshot = new ArrayList<>();
        for (DisplayValue dv : values) {
            // We only care about the integer value for history
            // The entities stay the same
            snapshot.add(dv);
        }

        history.add(new ArrayList<>(values));
        historyIndex = history.size() - 1;
    }

    /**
     * Render the given state visually in the world.
     */
    private void renderState() {
        // DO NOT call cleanup() here!
        // Cleanup should only be used when destroying the whole visualization.

        for (int i = 0; i < values.size(); i++) {
            var displayVal = values.get(i);

            double x = origin.x() + (i * SPACING);
            // TODO: maybe add the height difference back
            // double y = origin.y() + BASE_HEIGHT + (displayVal.getValue() * HEIGHT_MULTIPLIER);
            double y = origin.y() + BASE_HEIGHT;
            double z = origin.z();

            // Use teleport or edit position
            MinecraftServer.getSchedulerManager()
                    .buildTask(
                            () -> {
                                displayVal.teleport(new net.minestom.server.coordinate.Pos(x, y, z));
                            }
                    )
                    .delay(Duration.ofMillis(100))
                    .schedule();

            // updateMetadata(entity, i);
        }
    }

    private void clearRenderState() {
        for (DisplayValue value : values) {
            value.setHighlighted(false);
        }
    }

    public void stepForward() {
        if (!algorithmComplete) {
            clearRenderState();
            executeStep();
            renderState();
        }
    }

    // @Override
    public void stepBack() {
        if (historyIndex > 0) {
            historyIndex--;
            this.values = new ArrayList<>(history.get(historyIndex));

            // Reset algorithm state - we'd need to recalculate,
            // but for visualization purposes just re-render
            algorithmComplete = false;
            renderState();
        }
    }

    public void swap(int idx1, int idx2) {
        Collections.swap(values, idx1, idx2);

        MinecraftServer.getSchedulerManager()
                .buildTask(this::renderState)
                .delay(Duration.ofMillis(200))
                .schedule();
    }
}