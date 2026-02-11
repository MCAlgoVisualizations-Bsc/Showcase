package io.github.mcalgovisualizations.visualization;


import io.github.mcalgovisualizations.visualization.algorithms.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.algorithms.StepperFactory;
import io.github.mcalgovisualizations.visualization.engine.VisualizationController;
import io.github.mcalgovisualizations.visualization.layouts.FloatingLinearLayout;
import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.models.IntList;
import io.github.mcalgovisualizations.visualization.refactor.Visualization;
import io.github.mcalgovisualizations.visualization.render.VisualizationRenderer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages visualizations for all players.
 * Each player can have their own active visualization instance.
 */
public class VisualizationManager {
    private static final Map<UUID, Visualization> playerVisualizations = new HashMap<>();
    private static final Map<String, Pos> areaLocations = new HashMap<>();
    private static final Map<String, Class<? extends Visualization>> visualizations = new HashMap<>();

    private static final Map<String, Class<? extends AlgorithmStepper>> steppers = new HashMap<>();
    private static final Map<UUID, VisualizationController> playerSteppers = new HashMap<>();

    static {
        // Define area locations for different visualization types
        areaLocations.put("sorting", new Pos(5, 42, 5));
        areaLocations.put("pathfinding", new Pos(-100, 42, 0));
        areaLocations.put("trees", new Pos(0, 42, 100));
        areaLocations.put("bfs", new Pos(50, 42, 50));
    }

    /**
     * Assign a visualization to a player based on the type.
     * Cleans up any existing visualization for the player.
     *
     * @param player   The player to assign the visualization to
     * @param type     The type of visualization (e.g., "sorting", "insertionsort")
     * @param instance The game instance
     */
    public static void assignVisualization(Player player, String type, InstanceContainer instance) {
        // Clean up existing visualization
        removeVisualization(player);

        // Calculate origin position for this player's visualization
        // Offset each player's visualization to avoid overlap
        Pos baseOrigin = areaLocations.getOrDefault(type.toLowerCase(), new Pos(0, 42, 0));
        int playerOffset = playerVisualizations.size() * 20; // 20 blocks apart
        Pos origin = baseOrigin.add(0, 0, playerOffset);

        final DataModel model = createModelFor(type, player);
        final AlgorithmStepper stepper = StepperFactory.create(type, model);

        // TODO : Let players control size of layout!
        var layout = new FloatingLinearLayout();

        var renderer = new VisualizationRenderer(instance);
        renderer.setLayout(layout);
        var controller = new VisualizationController(stepper, renderer);

        playerSteppers.put(player.getUuid(), controller);
    }

    public static void addVisualization(String name, Class<? extends Visualization> vis) {
        visualizations.put(name.toLowerCase(), vis);
    }

    /**
     * Get the current visualization for a player.
     *
     * @param player The player
     * @return The visualization, or null if none assigned
     */
    public static VisualizationController getVisualization(Player player) {
        return playerSteppers.get(player.getUuid());
    }

    /**
     * Remove and cleanup the visualization for a player.
     *
     * @param player The player
     */
    public static void removeVisualization(Player player) {
        Visualization vis = playerVisualizations.remove(player.getUuid());
        if (vis != null) {
            vis.cleanup();
        }
    }

    /**
     * Get the location of a visualization area.
     *
     * @param area The area name
     * @return The position, or null if not found
     */
    public static Pos getAreaLocation(String area) {
        return areaLocations.get(area.toLowerCase());
    }

    // TODO : this can potentially take a parameter for the size of a list -> player can choose the size in hotbar?
    private static DataModel createModelFor(String type, Player player) {
        return switch (type.toLowerCase()) {
            case "sorting", "insertionsort", "insertion" -> new IntList(new int[10]);
            // case "bfs" -> new Graph(...);  // if Graph implements DataModel
            default -> new IntList(new int[10]); // or throw if unknown
        };
    }

}