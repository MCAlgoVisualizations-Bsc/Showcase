package io.github.mcalgovisualizations;

import io.github.mcalgovisualizations.commands.Gamemode;
import io.github.mcalgovisualizations.commands.Greet;
import io.github.mcalgovisualizations.commands.Spawn;
import io.github.mcalgovisualizations.commands.Teleport;
import io.github.mcalgovisualizations.gui.AlgorithmSelectorGUI;
import io.github.mcalgovisualizations.items.VisualizationItems;
import io.github.mcalgovisualizations.visualization.engine.VisualizationController;
import io.github.mcalgovisualizations.visualization.VisualizationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

import static io.github.mcalgovisualizations.config.WorldConfig.createMainInstance;


public final class Main {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        InstanceContainer instance = createMainInstance();

        // Sets the game time
        instance.setTimeRate(0);  // Stops time
        instance.setTime(6000);   // Sets time to noon

        //VisualizationManager.addVisualization("insertionsort", InsertionSortVisualization.class);
        //VisualizationManager.addVisualization("bfs", BFSVisualization.class);

        registerListeners(instance);
        registerCommands(MinecraftServer.getCommandManager());

        visualizationControls(instance); // to be moved?

        server.start("0.0.0.0", 25565);
    }

    private static void registerListeners(InstanceContainer instance) {
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
            player.sendMessage(Component.text("Welcome to Algorithm Visualizations!", NamedTextColor.GREEN));
            player.sendMessage(Component.text("Right-click the Nether Star to select an algorithm to visualize!", NamedTextColor.YELLOW));
        });

        // Cleanup visualization when player disconnects
        globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            VisualizationManager.removeVisualization(event.getPlayer());
        });

    }

    private static void visualizationControls(InstanceContainer instance) {
        final var globalEventHandler = MinecraftServer.getGlobalEventHandler();
        // Handle item right-clicks for visualization control
        globalEventHandler.addListener(PlayerUseItemEvent.class, event -> {
            Player player = event.getPlayer();
            Material material = event.getItemStack().material();

            // Handle algorithm selector (Nether Star) - available to everyone
            if (material == Material.NETHER_STAR) {
                AlgorithmSelectorGUI.openSelector(player, instance);
                return;
            }

            // Handle compass (return to hub) - available to everyone
            if (material == Material.COMPASS) {
                player.teleport(new Pos(0, 42, 0));
                player.sendMessage(Component.text("Returned to hub!", NamedTextColor.LIGHT_PURPLE));
                return;
            }

            // All other items require an active visualization
            VisualizationController vis = VisualizationManager.getVisualization(player);
            if (vis == null) {
                player.sendMessage(Component.text("No visualization assigned! Use the Algorithm Selector first.", NamedTextColor.RED));
                return;
            }

            if (material == Material.ENDER_PEARL) {
                event.setCancelled(true); // Prevent teleportation
                vis.randomize();
                player.sendMessage(Component.text("Values randomized!", NamedTextColor.AQUA));
            } else if (material == Material.LIME_DYE) {
                vis.start(player); // TODO : look into if messages can be sent through another channel?
                player.sendMessage(Component.text("Visualization started!", NamedTextColor.GREEN));
            } else if (material == Material.RED_DYE) {
                vis.stop();
                player.sendMessage(Component.text("Visualization stopped!", NamedTextColor.RED));
            } else if (material == Material.ARROW) {
                vis.step();
                player.sendMessage(Component.text("Stepped forward", NamedTextColor.YELLOW));
            } else if (material == Material.SPECTRAL_ARROW) {
                vis.back();
                player.sendMessage(Component.text("Stepped back", NamedTextColor.GOLD));
            }
        });
    }

    private static void registerCommands(CommandManager cm) {
        cm.register(new Greet());
        cm.register(new Teleport());
        cm.register(new Gamemode());
        cm.register(new Spawn());
    }
}