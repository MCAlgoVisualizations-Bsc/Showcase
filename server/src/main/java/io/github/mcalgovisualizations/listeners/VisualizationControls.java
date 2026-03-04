package io.github.mcalgovisualizations.listeners;

import io.github.mcalgovisualizations.gui.AlgorithmUIGUI;
import io.github.mcalgovisualizations.visualization.VisualizationManager;
import io.github.mcalgovisualizations.visualization.engine.VisualizationController;
import io.github.mcalgovisualizations.visualization.renderer.handlers.SystemMessages;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.Material;

public class VisualizationControls {

    public static void register(GlobalEventHandler handler, InstanceContainer instance) {
        // Handle item right-clicks for visualization control
        handler.addListener(PlayerUseItemEvent.class, event -> {
            Player player = event.getPlayer();
            Material material = event.getItemStack().material();

            // Handle algorithm selector (Nether Star) - available to everyone
            if (material == Material.NETHER_STAR) {
                AlgorithmUIGUI.openSelector(player, instance);
                return;
            }

            // Handle compass (return to hub) - available to everyone
            if (material == Material.COMPASS) {
                player.teleport(new Pos(0, 42, 0));
                SystemMessages.sendTo(player, SystemMessages.RETURNED_TO_HUB);
                return;
            }

            // All other items require an active visualization
            VisualizationController vis = VisualizationManager.getVisualization(player);

            if (vis == null) {
                SystemMessages.sendTo(player, SystemMessages.NO_VISUALIZATION);
                return;
            }

            if (material == Material.ENDER_PEARL) {
                event.setCancelled(true); // Prevent teleportation
                vis.randomize();
                SystemMessages.sendTo(player, SystemMessages.RANDOMIZED);
            } else if (material == Material.LIME_DYE) {
                vis.start();
                SystemMessages.sendTo(player, SystemMessages.VISUALIZATION_STARTED);
            } else if (material == Material.RED_DYE) {
                vis.stop();
                SystemMessages.sendTo(player, SystemMessages.VISUALIZATION_STOPPED);
            } else if (material == Material.ARROW) {
                vis.step();
                SystemMessages.sendTo(player, SystemMessages.STEP_FORWARD);
            } else if (material == Material.SPECTRAL_ARROW) {
                vis.back();
                SystemMessages.sendTo(player, SystemMessages.STEP_BACKWARD);
            }
        });
    }
}
