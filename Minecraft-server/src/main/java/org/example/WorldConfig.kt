package org.example

import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block

object WorldConfig {

    private var _instanceContainer: InstanceContainer? = null

    val instanceContainer: InstanceContainer
        get() = _instanceContainer!!

    fun createInstance(instanceManager: InstanceManager): InstanceContainer {
        val container = instanceManager.createInstanceContainer()

        // Kotlin uses braces {} for lambdas, no need for explicit 'Generator' wrapper
        container.setGenerator { unit ->
            unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
        }

        // Method references work fine in Kotlin if the types match
        container.setChunkSupplier(::LightingChunk)

        _instanceContainer = container
        return container
    }
}