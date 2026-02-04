package org.example;

import io.github.mcalgovisualizations.Library;
import net.minestom.server.MinecraftServer;

public class Main {
    public static void main(String[] args) {
        // Initialize the server
        MinecraftServer minecraftServer = MinecraftServer.init();

        System.out.println(Library.someLibraryMethod());

        // Start the server
        minecraftServer.start("0.0.0.0", 25565);
    }
}
