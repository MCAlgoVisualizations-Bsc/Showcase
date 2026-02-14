package io.github.mcalgovisualizations.visualization;

import java.io.Serializable;

public interface Snapshot extends Serializable {
    int[] values();
    int[] highlights();

}
