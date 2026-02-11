package io.github.mcalgovisualizations.visualization.renderers;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

        if (visualizations.containsKey(type.toLowerCase())) {
            Visualization vis = null;
            try {
                // 1. You must tell Java the CLASS TYPES of the parameters first
                vis = visualizations.get(type.toLowerCase())
                        .getDeclaredConstructor(UUID.class, Pos.class, InstanceContainer.class)
                        .newInstance(player.getUuid(), origin, instance); // 2. Then pass the actual values

                playerVisualizations.put(player.getUuid(), vis);
            } catch (NoSuchMethodException e) {
                System.out.println("Error: The class for " + type + " does not have a (Pos, InstanceContainer) constructor.");
            } catch (Exception e) {
                System.out.println("test");
                e.printStackTrace();
            }
            playerVisualizations.put(player.getUuid(), vis);
            SnapshotManager.getInstance().assignVisualization(player.getUuid(), vis);
        }
    }

    public static void addVisualization(String name, Class<? extends Visualization> vis) {
        visualizations.put(name.toLowerCase(), vis);
    }

    /**
     * Remove and cleanup the visualization for a player.
     *
     * @param player The player
     */
    public static void removeVisualization(Player player) {
        SnapshotManager.getInstance().remove(player.getUuid());
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

    public static void randomize(Player player) {
        var vis = playerVisualizations.get(player.getUuid());
        if (vis == null) {
            player.sendMessage(Component.text("No visualization assigned! Use the Algorithm Selector first.", NamedTextColor.RED));
            return;
        }
        vis.randomize();
    }

    public static void start(Player player) {
        var vis = playerVisualizations.get(player.getUuid());
        if (vis == null) {
            player.sendMessage(Component.text("No visualization assigned! Use the Algorithm Selector first.", NamedTextColor.RED));
            return;
        }
        vis.start(player);
    }

    public static void stop(Player player) {
        var vis = playerVisualizations.get(player.getUuid());
        if (vis == null) {
            player.sendMessage(Component.text("No visualization assigned! Use the Algorithm Selector first.", NamedTextColor.RED));
            return;
        }
        vis.stop();
    }

    public static void stepForward(Player player) {
        var vis = playerVisualizations.get(player.getUuid());
        if (vis == null) {
            player.sendMessage(Component.text("No visualization assigned! Use the Algorithm Selector first.", NamedTextColor.RED));
            return;
        }
        vis.stepForward();
        player.sendMessage(Component.text("Stepped forward", NamedTextColor.YELLOW));
    }

    public static void stepBack(Player player) {
        var visOld = playerVisualizations.get(player.getUuid());
        if (visOld == null) {
            player.sendMessage(Component.text("No visualization assigned! Use the Algorithm Selector first.", NamedTextColor.RED));
            return;
        }
        visOld.cleanup();

        var visNew = SnapshotManager.getInstance().loadLatestSnapshot(player.getUuid());
        playerVisualizations.put(player.getUuid(), visNew);
        visNew.Render();

    }
}