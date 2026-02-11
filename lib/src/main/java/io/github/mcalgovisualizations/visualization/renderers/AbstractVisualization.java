package io.github.mcalgovisualizations.visualization.renderers;

import io.github.mcalgovisualizations.visualization.SnapshotManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;

import io.github.mcalgovisualizations.visualization.models.IntListModel;
import io.github.mcalgovisualizations.visualization.layouts.SortingLayout;

import java.time.Duration;
import java.util.*;

/**
 * Abstract base class for all visualizations.
 * Provides common functionality like scheduling, history management, and state tracking.
 */
public abstract class AbstractVisualization<T extends Comparable<T>> implements Visualization {
    protected final String name;
    protected final Pos origin;
    protected boolean algorithmComplete = false;
    private final Random random = new Random();

    protected int ticksPerStep = 20; // 1 second default
    protected boolean running = false;
    protected Task runningTask = null;
    protected List<DisplayValue<T>> values = new ArrayList<>();
    protected final List<List<DisplayValue<T>>> history = new ArrayList<>();
    protected int historyIndex = -1;

    /** Optional layout strategy for positioning elements (primarily for sorting visualizations). */
    protected SortingLayout layout = null;

    // Visual spacing constants
    private static final double SPACING = 2.0;           // Horizontal space between elements
    private static final double BASE_HEIGHT = 1.0;       // Base Y offset (above ground)
    private static final double HEIGHT_MULTIPLIER = 0.5; // How much height per value unit

    protected final UUID uuid;

    public AbstractVisualization(String name, UUID uuid, List<DisplayValue<T>> values, Pos origin, InstanceContainer instance) {
        this.name = name;
        this.origin = origin;
        this.values = values;
        this.uuid = uuid;
    }

    /** Configure a layout strategy for positioning values. If null, the default linear spacing layout is used. */
    public void setLayout(SortingLayout layout) {
        this.layout = layout;
    }

    @Override
    public void start(Player player) {
        if (running) return;
        running = true;

        runningTask = MinecraftServer.getSchedulerManager()
                .buildTask(() -> {
                    stepForward();
                    SnapshotManager.getInstance().saveSnapshot(player.getUuid());
                })
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
    public void setSpeed(Player player, int ticksPerStep) {
        this.ticksPerStep = Math.max(1, ticksPerStep);
        // If running, restart with new speed
        if (running) {
            if (runningTask != null) {
                runningTask.cancel();
            }
            runningTask = MinecraftServer.getSchedulerManager()
                    .buildTask(() -> {
                        stepForward();
                        SnapshotManager.getInstance().saveSnapshot(player.getUuid());
                    })
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

    public void randomize() {
        stop();
        algorithmComplete = false;

        history.clear();
        historyIndex = -1;

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
        SnapshotManager.getInstance().saveSnapshot(uuid);
    }

    /**
     * Render the given state visually in the world.
     */
    protected void renderState() {
        // If a custom layout is configured (and values are Integer), defer positioning to it.
        if (layout != null && !values.isEmpty() && values.get(0).getValue() instanceof Integer) {
            int n = values.size();
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = (Integer) values.get(i).getValue();
            }

            Pos[] positions = layout.compute(new IntListModel(arr), origin);
            int limit = Math.min(positions.length, n);

            for (int i = 0; i < limit; i++) {
                var displayVal = values.get(i);
                Pos p = positions[i];
                if (p == null) continue;

                MinecraftServer.getSchedulerManager()
                        .buildTask(() -> displayVal.teleport(p))
                        .delay(Duration.ofMillis(100))
                        .schedule();
            }
            return;
        }

        // Default fallback layout (original behavior)
        for (int i = 0; i < values.size(); i++) {
            var displayVal = values.get(i);

            double x = origin.x() + (i * SPACING);
            // double y = origin.y() + BASE_HEIGHT + (displayVal.getValue() * HEIGHT_MULTIPLIER);
            double y = origin.y() + BASE_HEIGHT;
            double z = origin.z();

            MinecraftServer.getSchedulerManager()
                    .buildTask(() -> displayVal.teleport(new Pos(x, y, z)))
                    .delay(Duration.ofMillis(100))
                    .schedule();
        }
    }

    private void clearRenderState() {
        for (DisplayValue<T> value : values) {
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

    @Override
    public void stepBack(UUID uuid) {
        var vis = SnapshotManager.getInstance().loadLatestSnapshot(uuid);

    }

    public void swap(int idx1, int idx2) {
        Collections.swap(values, idx1, idx2);

        MinecraftServer.getSchedulerManager()
                .buildTask(this::renderState)
                .delay(Duration.ofMillis(200))
                .schedule();
    }

    @Override
    public void Render() {
        renderState();
    }
}
