package org.example

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.InstanceContainer
import java.util.function.Consumer

class PlayerConfig(private val spawnInstance: InstanceContainer) : Consumer<AsyncPlayerConfigurationEvent> {
    override fun accept(event: AsyncPlayerConfigurationEvent) {
        val player = event.player

        // 1. Set Gamemode when spawned
        player.gameMode = GameMode.CREATIVE

        // 2. Set Instance
        event.spawningInstance = spawnInstance

        // 3. Set Respawn Point
        player.respawnPoint = Pos(0.0, 42.0, 0.0)
    }
}