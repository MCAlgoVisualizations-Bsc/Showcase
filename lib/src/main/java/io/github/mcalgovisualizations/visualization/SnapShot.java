package io.github.mcalgovisualizations.visualization;

import java.io.Serializable;

public interface SnapShot extends Serializable {
    int[] values();
    int[] highlights();

}
