package org.example;

import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

public class PlayerConfig {
    public void onPLayerConfig(AsyncPlayerConfigurationEvent event){
        final Player player = event.getPlayer();

    }
}
