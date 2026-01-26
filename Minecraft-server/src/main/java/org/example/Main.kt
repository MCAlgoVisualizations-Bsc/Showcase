package org.example

import net.minestom.server.Auth
import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import org.example.commands.Gamemode
import org.example.commands.Greet
import org.example.commands.Teleport
import revxrsal.commands.minestom.MinestomLamp
import revxrsal.commands.minestom.MinestomLamp.builder


object Main{
    @JvmStatic
    fun main(args: Array<String>) {
        // 1. Initialize Server
        System.setProperty("minestom.use-new-chunk-sending", "true")
        val minecraftServer = MinecraftServer.init(Auth.Online())
        val instanceManager = MinecraftServer.getInstanceManager()

        // 2. Create the World (Using your helper class)
        val worldConfig = WorldConfig
        val mainInstance = worldConfig.createInstance(instanceManager)

        // 3. Register Player Join Logic (Passing the world we just created)
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler.addListener<AsyncPlayerConfigurationEvent?>(
            AsyncPlayerConfigurationEvent::class.java,
            PlayerConfig(mainInstance)
        )

        // 4. Register Commands


        val lamp = builder().build()
        lamp.register(Greet())
        lamp.register(Teleport())
        lamp.register(Gamemode())



        //commandManager.register(Gamemode())
        //commandManager.register(Teleport())


        // 5. Start
        println("Server starting on port 25565...")
        minecraftServer.start("0.0.0.0", 25565)
    }
}