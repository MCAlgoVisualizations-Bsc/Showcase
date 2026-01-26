package org.example.commands

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import org.example.WorldConfig
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Optional

class Teleport {

    @Command("tp @a")
    fun teleportHere(sender: Player) {
        for (entity in WorldConfig.instanceContainer.players) {
            entity.teleport(sender.position)
        }
    }

    @Command("tp player")
    fun teleportTo(sender: Player, target: String) {
        // Check if the player you're targeting exists on the server
        val targetPlayer = WorldConfig.instanceContainer.players.find { it.name.toString() == target }
        targetPlayer?.let { sender.teleport(it.position) }
    }

}
