package io.github.mcalgovisualizations.visualization.algorithms.sorting;

import io.github.mcalgovisualizations.visualization.algorithms.*;
import io.github.mcalgovisualizations.visualization.models.Data;
import io.github.mcalgovisualizations.visualization.models.SortingCollection;

import java.util.ArrayList;


// TODO: remove insertion sort from AlgorithmStepper
public class AlgorithmStepper<T extends Comparable<T>> implements IAlgorithmStepper {
    private final ArrayList<HistorySnapshot> history = new ArrayList<>();
    private int historyPointer = 0;
    private final SortingState state = new SortingState();
    private HistorySnapshot firstSnapshot;
    //private boolean ALGORITHM_COMPLETE = false;

    private final SortingCollection<T> collection;
    private final IPlayerSort algorithm;

    public AlgorithmStepper(IPlayerSort algorithm, SortingCollection<T> collection) {
        this.algorithm = algorithm;
        this.collection = collection;
    }

    public ISnapshot<T> onStart() {
        var values = collection.data().toArray(Data[]::new);

        var firstSnapshot = new HistorySnapshot<T>(
                values,
                collection.events(),
                state.highlights(),
                state.currentIndex(),
                state.compareIndex(),
                false

        );

        algorithm.sort(collection);

        this.firstSnapshot = firstSnapshot;
        history.add(firstSnapshot);
        return firstSnapshot;
    }

    @Override
    public ISnapshot<T> step() {

        // Check if step is old
        if ((historyPointer + 1) < history.size()) {
            historyPointer++;
            return history.get(historyPointer);
        }


//        // Check if done
//        if (ALGORITHM_COMPLETE) {
//            state.addEvent(new Complete());
//            return history.get(historyPointer);
//        }

        if (state.compareIndex() == -1) {
            state.setCompareIndex(state.currentIndex());
        }

        history.add(getHistorySnapshot());
        historyPointer++;
        return history.get(historyPointer);
    }

    @Override
    public HistorySnapshot<T> back() {
        if ((historyPointer - 1) < 0) return null;
        historyPointer--;
        return history.get(historyPointer);
    }

    private HistorySnapshot<T> getHistorySnapshot() {
        return new HistorySnapshot<>(
                null, // only used for onStart()
                collection.events(), // TODO : Fix this so each step produces a subset of the events
                state.highlights(),
                state.currentIndex(),
                state.compareIndex(),
                false
        );
    }



}
