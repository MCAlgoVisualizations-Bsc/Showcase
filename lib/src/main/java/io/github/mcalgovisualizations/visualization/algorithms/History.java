package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.algorithms.events.IAlgorithmEvent;
import io.github.mcalgovisualizations.visualization.models.Data;
import io.github.mcalgovisualizations.visualization.models.SortingCollection;

import java.util.ArrayList;
import java.util.List;

public class History {
    private final IPlayerSort algorithm;
    private final SortingCollection<?> collection;
    private final List<IAlgorithmEvent> events;
    private int currentIndex = 0;

    public History(IPlayerSort algorithm, SortingCollection<?> collection) {
        this.algorithm = algorithm;
        this.collection = collection;
        this.events = new ArrayList<>(collection.events());
    }

    public Data<?>[] onStart() {
        algorithm.sort(collection);
        return collection.data().toArray(Data<?>[]::new);
    }

    public IAlgorithmEvent step() {
        return events.get(currentIndex++);
    }

    public IAlgorithmEvent back() {
        if (currentIndex == 0) return events.getFirst();
        return events.get(currentIndex--);
    }

    public void cleanUp() {

    }
}
