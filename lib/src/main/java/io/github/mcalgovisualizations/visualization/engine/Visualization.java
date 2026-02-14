package io.github.mcalgovisualizations.visualization.engine;

import io.github.mcalgovisualizations.visualization.algorithms.PlayerMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

public class Visualization implements VisualizationLifeCycle {
    private final Player p;
    private final PlayerMessenger pm;
    private final VisualizationController c;

    public Visualization(Player player, VisualizationController visualizationController) {
        this.p = player;
        this.pm = new PlayerMessenger(player);
        this.c = visualizationController;
    }

    @Override
    public void onStart() {
        if (c.isRunning())
            return;

        p.sendMessage(Component.text("Algorithm complete! Use randomize to restart.", NamedTextColor.YELLOW));
    }
    @Override
    public void onStop() {
        c.stop();
    }

    @Override
    public void onCleanup() {
        c.cleanup();
    }
}
