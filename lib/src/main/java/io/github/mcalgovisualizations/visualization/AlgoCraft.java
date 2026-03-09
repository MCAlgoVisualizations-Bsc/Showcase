package io.github.mcalgovisualizations.visualization;


import io.github.mcalgovisualizations.visualization.algorithms.IAlgorithmStepper;

import io.github.mcalgovisualizations.visualization.algorithms.StepperFactory;
import io.github.mcalgovisualizations.visualization.engine.VisualizationController;
import io.github.mcalgovisualizations.visualization.models.IDataModel;
import io.github.mcalgovisualizations.visualization.models.IntList;
import io.github.mcalgovisualizations.visualization.ui.AlgorithmUI;
import io.github.mcalgovisualizations.visualization.ui.IAlgorithmUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;

import java.util.HashMap;

import java.util.Map;

import static io.github.mcalgovisualizations.visualization.Tags.*;


public class AlgoCraft {
    private IAlgorithmUI ui = new AlgorithmUI();

    private final Map<String , Class<? extends IAlgorithmStepper>> algorithms = new HashMap<>();
    private final VisualizationManager visualizationManager = new VisualizationManager();

    private final InstanceContainer instanceContainer;
    public AlgoCraft(InstanceContainer instanceContainer) {
        this.instanceContainer = instanceContainer;
    }

    public void addListeners(GlobalEventHandler handler) {
        handler.addListener(PlayerUseItemEvent.class, event -> {
            Player player = event.getPlayer();
            VisualizationController vis = visualizationManager.getVisualization(player);
            ItemStack itemStack = event.getItemStack();
            event.setCancelled(true); // Prevent teleportation
            if (itemStack.hasTag(ALGO_SELECTOR_TAG)) {
                selectAlgorithm(player);
                return;
            }

            if (itemStack.hasTag(ALGO_INTERACTION_TAG))
                switch (itemStack.getTag(ALGO_INTERACTION_TAG)) {
                    case RANDOMIZE -> vis.randomize();
                    case START -> vis.start(player);
                    case STOP -> vis.stop();
                    case FORWARD -> vis.step();
                    case BACKWARD -> vis.back();
                }
        });
    }

    public void registerAlgorithm(String id, Class<? extends IDataModel> modelType, Class<? extends IAlgorithmStepper> algorithm) {
        this.algorithms.put(id, algorithm);
        StepperFactory.register(id, modelType, (model) -> {
            try {
                return algorithm.getDeclaredConstructor(IntList.class).newInstance(model);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
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
            /*
            if (clickedItem.getTag(ALGO_INTERACTION_TAG)) {
                player.closeInventory();
                player.sendMessage(Component.text("Visualization cleared!", NamedTextColor.YELLOW));
                return;
            }
            */

            // Find which algorithm was clicked
            for (String algorithm : algorithms.keySet()) {
                var algo_id = clickedItem.getTag(ALGO_ID_TAG);
                if (algo_id == null) continue;
                if (algo_id.equals(algorithm)) {
                    visualizationManager.assignVisualization(player, algo_id, instanceContainer);
                    ui.applyRunningLayout(player);
                    player.closeInventory();
                    return;
                }
            }
        });
        player.openInventory(inventory);
    }
} 