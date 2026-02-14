package io.github.mcalgovisualizations.visualization;

import net.minestom.server.instance.block.Block;

import java.util.List;

public interface SnapShot {
    int[] values();
    int[] highlights();
    List<AlgorithmEvent> events();
}
