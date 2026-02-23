package io.github.mcalgovisualizations;

import io.github.mcalgovisualizations.commands.Gamemode;
import io.github.mcalgovisualizations.commands.Greet;
import io.github.mcalgovisualizations.commands.Spawn;
import io.github.mcalgovisualizations.commands.Teleport;
import io.github.mcalgovisualizations.items.VisualizationItems;
import io.github.mcalgovisualizations.visualization.VisualizationManager;
import io.github.mcalgovisualizations.visualization.renderer.handlers.MessageHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
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

        // Register visualization control listeners (item interactions)
        io.github.mcalgovisualizations.listeners.VisualizationControls.register(MinecraftServer.getGlobalEventHandler(), instance);

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
            MessageHandler.send(player, MessageHandler.WELCOME);
            MessageHandler.send(player, MessageHandler.SELECT_ALGORITHM_HINT);
        });

        // Cleanup visualization when player disconnects
        globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            VisualizationManager.removeVisualization(event.getPlayer());
        });

    }



    private static void registerCommands(CommandManager cm) {
        cm.register(new Greet());
        cm.register(new Teleport());
        cm.register(new Gamemode());
        cm.register(new Spawn());
    }
}