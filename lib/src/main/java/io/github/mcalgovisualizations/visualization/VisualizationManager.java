package io.github.mcalgovisualizations.visualization;

import io.github.mcalgovisualizations.visualization.algorithms.IAlgorithmStepper;
import io.github.mcalgovisualizations.visualization.algorithms.StepperFactory;
import io.github.mcalgovisualizations.visualization.engine.VisualizationController;
import io.github.mcalgovisualizations.visualization.layouts.FloatingLinearLayout;
import io.github.mcalgovisualizations.visualization.layouts.ILayout;
import io.github.mcalgovisualizations.visualization.models.IDataModel;
import io.github.mcalgovisualizations.visualization.models.IntList;
import io.github.mcalgovisualizations.visualization.renderer.VisualizationRenderer;
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
    private final Map<String, Pos> areaLocations = new HashMap<>();
    private final Map<UUID, VisualizationController> playerSteppers = new HashMap<>();

    public VisualizationManager(){
        // Define default area locations for different visualization types
        areaLocations.put("sorting", new Pos(5, 42, 5));
        areaLocations.put("pathfinding", new Pos(-100, 42, 0));
        areaLocations.put("trees", new Pos(0, 42, 100));
        areaLocations.put("bfs", new Pos(50, 42, 50));
    }

    public void defineWorkArea(String algorithmType, Pos pos) {
        areaLocations.put(algorithmType, pos);
    }

    /**
     * Assign a visualization to a player based on the type.
     * Cleans up any existing visualization for the player.
     *
     * @param player   The player to assign the visualization to
     * @param type     The type of visualization (e.g., "sorting", "insertionsort")
     * @param instance The game instance
     */
    public void assignVisualization(Player player, String type, InstanceContainer instance) {
        // Clean up existing visualization
        removeVisualization(player);

        // TODO : Let players control Layout and model's size n!
        final IDataModel model = createModelFor(type, player, 10);
        final IAlgorithmStepper stepper = StepperFactory.create(type, model);

        ILayout layout = new FloatingLinearLayout();
        var origin = getAreaLocation("sorting");


        var renderer = new VisualizationRenderer(instance, origin, layout);
        var controller = new VisualizationController(stepper, renderer);
        controller.setAudience(player);

        controller.onStart();

        playerSteppers.put(player.getUuid(), controller);
    }

    /**
     * Get the current visualization for a player.
     *
     * @param player The player
     * @return The visualization, or null if none assigned
     */
    public VisualizationController getVisualization(Player player) {
        return playerSteppers.get(player.getUuid());
    }

    /**
     * Remove and cleanup the visualization for a player.
     *
     * @param player The player
     */
    public void removeVisualization(Player player) {
        VisualizationController vis = playerSteppers.remove(player.getUuid());
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
    public Pos getAreaLocation(String area) {
        return areaLocations.get(area.toLowerCase());
    }

    // TODO : this can potentially take a parameter for the size of a list -> player can choose the size in hotbar?
    private IDataModel createModelFor(String type, Player player, int n) {
        return switch (type.toLowerCase()) {
            case "sorting", "insertion sort", "insertion" -> {
                var out = new IntList(new int[n]);

                for (int i = 0; i < out.length(); i++) {
                    out.set(i, i);
                }

                yield out;

            }
            // case "bfs" -> new Graph(...);  // if Graph implements DataModel
            default -> new IntList(new int[10]); // or throw if unknown
        };
    }
}