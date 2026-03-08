package io.github.mcalgovisualizations;

import io.github.mcalgovisualizations.algorithms.PlayerInsertion;
import io.github.mcalgovisualizations.commands.*;
import io.github.mcalgovisualizations.gui.AlgorithmUIGUI;
import io.github.mcalgovisualizations.items.VisualizationItems;
import io.github.mcalgovisualizations.visualization.AlgoCraft;
import io.github.mcalgovisualizations.visualization.VisualizationManager;
import io.github.mcalgovisualizations.visualization.engine.VisualizationController;
import io.github.mcalgovisualizations.visualization.models.Data;
import io.github.mcalgovisualizations.visualization.models.SortingCollection;
import io.github.mcalgovisualizations.visualization.renderer.handlers.SystemMessages;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.github.mcalgovisualizations.config.WorldConfig.createMainInstance;


public final class Main {
    private static AlgoCraft algo = null;

    static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        InstanceContainer instance = createMainInstance();

        // Sets the game time
        instance.setTimeRate(0);  // Stops time
        instance.setTime(6000);   // Sets time to noon

        algo = new AlgoCraft(instance);

        var integerCollection1 = new ArrayList<>(Arrays.asList(
                new Data<>(3),
                new Data<>(7),
                new Data<>(8),
                new Data<>(1),
                new Data<>(6),
                new Data<>(4),
                new Data<>(9),
                new Data<>(5),
                new Data<>(2)
        ));

        var integerCollection2 = new ArrayList<>(Arrays.asList(
                new Data<>(8),
                new Data<>(3),
                new Data<>(1)
        ));

        var stringCollection1 = new ArrayList<>(Arrays.asList(
                new Data<>("a"),
                new Data<>("b"),
                new Data<>("k"),
                new Data<>("x"),
                new Data<>("d"),
                new Data<>("h"),
                new Data<>("a"),
                new Data<>("b"),
                new Data<>("e")
        ));

        algo.registerAlgorithm("insertion sort", PlayerInsertion::new, stringCollection1);
        // TODO : I cannot add multiple insertion sorts at in the instance, with different collections.

        //VisualizationManager.addVisualization("insertionsort", InsertionSortVisualization.class);
        //VisualizationManager.addVisualization("bfs", BFSVisualization.class);

        // Register visualization control listeners (item interactions)
        registerListeners(instance);
        registerControls(instance, algo.visualizationManager);
        registerCommands(MinecraftServer.getCommandManager());

        server.start("0.0.0.0", 25565);
    }

    static void registerControls(InstanceContainer instance, VisualizationManager manager) {
        final var globalEventHandler = MinecraftServer.getGlobalEventHandler();
        // Handle item right-clicks for visualization control
        globalEventHandler.addListener(PlayerUseItemEvent.class, event -> {
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
            VisualizationController vis =  manager.getVisualization(player);

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

    static void registerListeners(InstanceContainer instance) {
        final var globalEventHandler = MinecraftServer.getGlobalEventHandler();

        // Player configuration - set spawn instance and respawn point
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        // Player spawn - give items and assign visualization (player is now fully in the world)
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            if (!event.isFirstSpawn()) return; // Only on first spawn

            Player player = event.getPlayer();

            // Give fly access to player
            player.setAllowFlying(true);

            // Give only the algorithm selector and spawn item by default
            player.getInventory().setItemStack(0, VisualizationItems.algorithmSelectorItem());
            player.getInventory().setItemStack(8, VisualizationItems.spawnItem());

            // Send welcome message
            SystemMessages.sendTo(player, SystemMessages.WELCOME);
            SystemMessages.sendTo(player, SystemMessages.SELECT_ALGORITHM_HINT);
        });

        // Cleanup visualization when player disconnects
        globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            // VisualizationManager.removeVisualization(event.getPlayer());
        });

    }



    static void registerCommands(CommandManager cm) {
        cm.register(new Greet());
        cm.register(new Teleport());
        cm.register(new Gamemode());
        cm.register(new Spawn());
        cm.register(new testCommand(algo));
    }
}