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
import commands.Gamemode;
import commands.Greet;
import commands.Spawn;
import commands.Teleport;
import configs.WorldConfig;
import items.VisualizationItems;
import visualization.core.Visualization;
import visualization.core.VisualizationManager;

public final class Main {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        InstanceContainer instance = WorldConfig.createMainInstance();

        // Sets the game time
        instance.setTimeRate(0);  // Stops time
        instance.setTime(6000);   // Sets time to noon

        registerListeners(instance);
        registerCommands(MinecraftServer.getCommandManager());

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
            
            // Give control items to the player
            player.getInventory().setItemStack(0, VisualizationItems.randomizeItem());
            player.getInventory().setItemStack(1, VisualizationItems.startItem());
            player.getInventory().setItemStack(2, VisualizationItems.stopItem());
            player.getInventory().setItemStack(3, VisualizationItems.stepForwardItem());
            player.getInventory().setItemStack(4, VisualizationItems.stepBackItem());
            player.getInventory().setItemStack(8, VisualizationItems.spawnItem());

            // Assign sorting visualization near the player
            VisualizationManager.assignVisualization(player, "sorting", instance);
            
            // Send welcome message
            player.sendMessage(Component.text("Welcome! Use the items in your hotbar to control the Insertion Sort visualization.", NamedTextColor.GREEN));
            player.sendMessage(Component.text("Look around - the armor stands with wool blocks represent the array values!", NamedTextColor.YELLOW));
        });

        // Handle item right-clicks for visualization control
        globalEventHandler.addListener(PlayerUseItemEvent.class, event -> {
            Player player = event.getPlayer();
            Material material = event.getItemStack().material();
            Visualization vis = VisualizationManager.getVisualization(player);

            // Handle compass (return to hub) even without a visualization
            if (material == Material.COMPASS) {
                player.teleport(new Pos(0, 42, 0));
                player.sendMessage(Component.text("Returned to hub!", NamedTextColor.LIGHT_PURPLE));
                return;
            }

            if (vis == null) {
                player.sendMessage(Component.text("No visualization assigned!", NamedTextColor.RED));
                return;
            }

            if (material == Material.ENDER_PEARL) {
                event.setCancelled(true); // Prevent teleportation
                vis.randomize();
                player.sendMessage(Component.text("Values randomized!", NamedTextColor.AQUA));
            } else if (material == Material.LIME_DYE) {
                vis.start(player);
                player.sendMessage(Component.text("Visualization started!", NamedTextColor.GREEN));
            } else if (material == Material.RED_DYE) {
                vis.stop();
                player.sendMessage(Component.text("Visualization stopped!", NamedTextColor.RED));
            } else if (material == Material.ARROW) {
                vis.stepForward();
                player.sendMessage(Component.text("Stepped forward", NamedTextColor.YELLOW));
            } else if (material == Material.SPECTRAL_ARROW) {
                vis.stepBack();
                player.sendMessage(Component.text("Stepped back", NamedTextColor.GOLD));
            }
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

