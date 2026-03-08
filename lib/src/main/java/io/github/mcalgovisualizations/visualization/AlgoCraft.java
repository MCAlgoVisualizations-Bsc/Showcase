package io.github.mcalgovisualizations.visualization;


import io.github.mcalgovisualizations.visualization.algorithms.IPlayerSort;
import io.github.mcalgovisualizations.visualization.models.Data;
import io.github.mcalgovisualizations.visualization.models.SortingCollection;
import io.github.mcalgovisualizations.visualization.ui.AlgorithmUI;
import io.github.mcalgovisualizations.visualization.ui.IAlgorithmUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;

import static io.github.mcalgovisualizations.visualization.Tags.ALGO_CLEAR_TAG;
import static io.github.mcalgovisualizations.visualization.Tags.ALGO_ID_TAG;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class AlgoCraft {

    private record AlgorithmEntry(IPlayerSort algorithm, SortingCollection<?> collection) { }
    private IAlgorithmUI ui = new AlgorithmUI();

    public final VisualizationManager visualizationManager = new VisualizationManager();

    private final InstanceContainer instanceContainer;
    public AlgoCraft(InstanceContainer instanceContainer) {
        this.instanceContainer = instanceContainer;
    }


    private final Map<String, AlgorithmEntry> algorithms = new HashMap<>();

    public <T extends Comparable<T>> void registerAlgorithm(
            String id,
            Supplier<? extends IPlayerSort> ctor,
            List<Data<T>> lst
    ) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(ctor, "ctor");

        algorithms.put(id, new AlgorithmEntry(ctor.get(), new SortingCollection<>(lst)));

    }

    public void defineWorkArea(String algorithmType, Pos pos) {
        visualizationManager.defineWorkArea(algorithmType, pos);
    }

    public void setSelectorUI(IAlgorithmUI ui) {
        this.ui = ui;
    }

    public void selectAlgorithm(Player player) {
        var inventory = ui.openSelector(algorithms.keySet());
        MinecraftServer.getGlobalEventHandler().addListener(InventoryPreClickEvent.class, event -> {
            if (event.getPlayer() != player) return;
            if (event.getInventory() != inventory) return;

            event.setCancelled(true); // Prevent taking items

            ItemStack clickedItem = event.getClickedItem();
            if (clickedItem.isAir()) return;

            // Handle clear option
            if (clickedItem.getTag(ALGO_CLEAR_TAG) != null) {
                player.closeInventory();
                player.sendMessage(Component.text("Visualization cleared!", NamedTextColor.YELLOW));
                return;
            }


            // Find which algorithm was clicked
            for (String algorithm : algorithms.keySet()) {
                var algo_id = clickedItem.getTag(ALGO_ID_TAG);
                if (algo_id == null) continue;
                if (algo_id.equals(algorithm)) {
                    visualizationManager.assignVisualization(player, algo_id, instanceContainer, algorithms.get(algorithm).collection, algorithms.get(algorithm).algorithm);
                    ui.applyRunningLayout(player);
                    player.closeInventory();
                    return;
                }
            }
        });
        player.openInventory(inventory);
    }

}