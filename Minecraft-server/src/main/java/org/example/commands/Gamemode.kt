package org.example.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.command.builder.arguments.ArgumentEnum
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Default
import revxrsal.commands.annotation.Command

class Gamemode {

    @Command("gamemode creative", "gmc")
    fun creative(@Default("me") sender: Player) {
        sender.setGameMode(GameMode.CREATIVE)
    }

    @Command("gamemode adventure", "gma")
    fun adventure(@Default("me") sender: Player) {
        sender.setGameMode(GameMode.ADVENTURE)
    }

    @Command("gamemode spectactor", "gmsp")
    fun spectactrr(@Default("me") sender: Player) {
        sender.setGameMode(GameMode.SPECTATOR)
    }

    @Command("gamemode survival", "gmsu")
    fun survival(@Default("me") sender: Player) {
        sender.setGameMode(GameMode.SURVIVAL)
    }

}