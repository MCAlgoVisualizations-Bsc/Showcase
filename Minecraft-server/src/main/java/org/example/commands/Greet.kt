package org.example.commands

import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor

class Greet {
    @Command("greet")
    fun greet(actor: MinestomCommandActor, target: Player){
        actor.reply("Hello, ${target.name}!")
    }
}