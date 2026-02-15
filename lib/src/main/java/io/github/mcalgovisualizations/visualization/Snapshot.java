package io.github.mcalgovisualizations.visualization;

import java.util.List;

public interface Snapshot {
    int[] values();
    int[] highlights();
    List<AlgorithmEvent> events();
}
