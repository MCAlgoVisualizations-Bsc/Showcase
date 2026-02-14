package io.github.mcalgovisualizations.visualization.algorithms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

public record PlayerMessenger(Player player) {
    // Main
    public void sendReturnToHubMessage() {
        message("Returned to hub!", NamedTextColor.LIGHT_PURPLE);
    }
    public void sendRandomizeMessage() {
        message("Values randomized!", NamedTextColor.AQUA);
    }
    public void sendStartMessage() {
        message("Visualization started!", NamedTextColor.GREEN);
    }
    public void sendStopMessage() {
        message("Visualization stopped!", NamedTextColor.RED);
    }
    public void sendStepMessage() {
        message("Stepped forward", NamedTextColor.YELLOW);
    }
    public void sendBackMessage() {
        message("Stepped back", NamedTextColor.GOLD);
    }
    public void sendVisualizationError() {
        message("No visualization assigned! Use the Algorithm Selector first.", NamedTextColor.RED);
    }



    // Private helper
    private void message(String message, NamedTextColor color) { player.sendMessage(Component.text(message, color)); }
}
