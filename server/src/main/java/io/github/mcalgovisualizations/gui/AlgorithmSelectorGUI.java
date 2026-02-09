package io.github.mcalgovisualizations.gui;

import io.github.mcalgovisualizations.items.VisualizationItems;
import io.github.mcalgovisualizations.visualization.renderers.VisualizationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * GUI for selecting which algorithm visualization to view.
 * Displays available algorithms in a chest inventory interface.
 */
public class AlgorithmSelectorGUI {
    private static final Map<String, AlgorithmInfo> ALGORITHMS = new HashMap<>();

    static {
        // Register available algorithms with their display information
        ALGORITHMS.put("insertionsort", new AlgorithmInfo(
                "Insertion Sort",
                Material.IRON_SWORD,
                "A simple sorting algorithm that builds",
                "the final sorted array one item at a time.",
                "Time: O(n²) | Space: O(1)"
        ));

        ALGORITHMS.put("bfs", new AlgorithmInfo(
                "bfs",
                Material.WIND_CHARGE,
                "",
                "",
                ""
        ));

        // Future algorithms can be added here
        // ALGORITHMS.put("bubblesort", new AlgorithmInfo(...));
        // ALGORITHMS.put("quicksort", new AlgorithmInfo(...));
        // ALGORITHMS.put("bfs", new AlgorithmInfo(...));
    }

    /**
     * Opens the algorithm selector GUI for a player.
     */
    public static void openSelector(Player player, InstanceContainer instance) {
        Inventory inventory = new Inventory(InventoryType.CHEST_3_ROW, Component.text("Select Algorithm", NamedTextColor.DARK_PURPLE));

        int slot = 10; // Start at centered position
        for (Map.Entry<String, AlgorithmInfo> entry : ALGORITHMS.entrySet()) {
            AlgorithmInfo info = entry.getValue();

            ItemStack item = ItemStack.builder(info.material())
                    .customName(Component.text(info.displayName(), NamedTextColor.GOLD)
                            .decoration(TextDecoration.ITALIC, false))
                    .lore(
                            Component.text(info.description1(), NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false),
                            Component.text(info.description2(), NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false),
                            Component.empty(),
                            Component.text(info.complexity(), NamedTextColor.YELLOW)
                                    .decoration(TextDecoration.ITALIC, false),
                            Component.empty(),
                            Component.text("Click to select!", NamedTextColor.GREEN)
                                    .decoration(TextDecoration.ITALIC, false)
                    )
                    .build();

            inventory.setItemStack(slot, item);
            slot += 2; // Skip one slot for spacing
        }

        // Add a "Clear" option to remove current visualization
        ItemStack clearItem = ItemStack.builder(Material.BARRIER)
                .customName(Component.text("Clear Visualization", NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false))
                .lore(
                        Component.text("Remove current visualization", NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("and clear your hotbar.", NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                )
                .build();
        inventory.setItemStack(22, clearItem);

        // Handle clicks using global event listener
        MinecraftServer.getGlobalEventHandler().addListener(InventoryPreClickEvent.class, event -> {
            if (event.getPlayer() != player) return;
            if (event.getInventory() != inventory) return;

            event.setCancelled(true); // Prevent taking items

            ItemStack clickedItem = event.getClickedItem();
            if (clickedItem.isAir()) return;

            // Handle clear option
            if (clickedItem.material() == Material.BARRIER) {
                clearPlayerVisualization(player);
                player.closeInventory();
                player.sendMessage(Component.text("Visualization cleared!", NamedTextColor.YELLOW));
                return;
            }

            // Find which algorithm was clicked
            for (Map.Entry<String, AlgorithmInfo> entry : ALGORITHMS.entrySet()) {
                if (clickedItem.material() == entry.getValue().material()) {
                    selectAlgorithm(player, entry.getKey(), instance);
                    player.closeInventory();
                    return;
                }
            }
        });

        player.openInventory(inventory);
    }

    /**
     * Assigns an algorithm visualization to the player and gives them control items.
     */
    private static void selectAlgorithm(Player player, String algorithmKey, InstanceContainer instance) {
        // Assign the visualization
        VisualizationManager.assignVisualization(player, algorithmKey, instance);

        // Clear inventory and give control items
        player.getInventory().clear();
        player.getInventory().setItemStack(0, VisualizationItems.randomizeItem());
        player.getInventory().setItemStack(1, VisualizationItems.startItem());
        player.getInventory().setItemStack(2, VisualizationItems.stopItem());
        player.getInventory().setItemStack(3, VisualizationItems.stepForwardItem());
        player.getInventory().setItemStack(4, VisualizationItems.stepBackItem());
        player.getInventory().setItemStack(7, VisualizationItems.algorithmSelectorItem());
        player.getInventory().setItemStack(8, VisualizationItems.spawnItem());

        AlgorithmInfo info = ALGORITHMS.get(algorithmKey);
        player.sendMessage(Component.text("Selected: ", NamedTextColor.GREEN)
                .append(Component.text(info.displayName(), NamedTextColor.GOLD)));
        player.sendMessage(Component.text("Use the items in your hotbar to control the visualization!", NamedTextColor.YELLOW));
    }

    /**
     * Clears the player's visualization and inventory.
     */
    private static void clearPlayerVisualization(Player player) {
        VisualizationManager.removeVisualization(player);
        player.getInventory().clear();
        player.getInventory().setItemStack(4, VisualizationItems.algorithmSelectorItem());
        player.getInventory().setItemStack(8, VisualizationItems.spawnItem());
    }

    /**
     * Registers a new algorithm to the selector.
     */
    public static void registerAlgorithm(String key, String displayName, Material icon,
                                        String desc1, String desc2, String complexity) {
        ALGORITHMS.put(key.toLowerCase(), new AlgorithmInfo(displayName, icon, desc1, desc2, complexity));
    }

    /**
     * Record to store algorithm display information.
     */
    private record AlgorithmInfo(
            String displayName,
            Material material,
            String description1,
            String description2,
            String complexity
    ) {}
}
