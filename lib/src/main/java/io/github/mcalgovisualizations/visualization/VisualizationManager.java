package io.github.mcalgovisualizations.visualization;


import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    static {
        // Define area locations for different visualization types
        // Sorting is close to spawn so players can see it immediately
        areaLocations.put("sorting", new Pos(5, 42, 5));
        areaLocations.put("pathfinding", new Pos(-100, 42, 0));
        areaLocations.put("trees", new Pos(0, 42, 100));
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

        if (visualizations.containsKey(type.toLowerCase())) {
            Visualization vis = null;
            try {
                // 1. You must tell Java the CLASS TYPES of the parameters first
                vis = visualizations.get(type.toLowerCase())
                        .getDeclaredConstructor(Pos.class, InstanceContainer.class)
                        .newInstance(origin, instance); // 2. Then pass the actual values

                playerVisualizations.put(player.getUuid(), vis);
            } catch (NoSuchMethodException e) {
                System.out.println("Error: The class for " + type + " does not have a (Pos, InstanceContainer) constructor.");
            } catch (Exception e) {
                System.out.println("test");
                e.printStackTrace();
            }
            // TODO: fix this
            /*
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            */
            playerVisualizations.put(player.getUuid(), vis);
        }
        /*
        Visualization vis = switch () {
            case "sorting", "insertionsort" -> new InsertionSortVisualization(origin, instance, 8);
            // Future visualizations:
            // case "bubblesort" -> new BubbleSortVisualization(origin, instance, 8);
            // case "pathfinding", "astar" -> new AStarVisualization(origin, instance);
            // case "bfs" -> new BFSVisualization(origin, instance);
            // case "tree", "bst" -> new BinarySearchTreeVisualization(origin, instance);
            default -> null;
        };
        */
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
    public static Visualization getVisualization(Player player) {
        return playerVisualizations.get(player.getUuid());
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
}