package io.github.mcalgovisualizations.gui;

import net.minestom.server.item.Material;

/**
 * Record to store algorithm display information.
 */
public record AlgorithmInfo(
        String displayName,
        Material material,
        String description1,
        String description2,
        String complexity
) {}
