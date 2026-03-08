package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.algorithms.events.IAlgorithmEvent;
import io.github.mcalgovisualizations.visualization.models.Data;

import java.util.Iterator;
import java.util.List;

public interface ISnapshot<T extends Comparable<T>> {
    Data<T>[] values();
    int[] highlights();
    List<IAlgorithmEvent> events();
    boolean completed();
}
