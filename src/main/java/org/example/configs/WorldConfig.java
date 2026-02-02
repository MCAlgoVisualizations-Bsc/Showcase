package org.example.configs;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

public final class WorldConfig {
    public static InstanceContainer createMainInstance() {
        InstanceContainer container = MinecraftServer.getInstanceManager().createInstanceContainer();
        container.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        container.setChunkSupplier(LightingChunk::new);
        return container;
    }
}

