package io.github.mcalgovisualizations.visualization.algorithms.sorting;

import io.github.mcalgovisualizations.visualization.HistorySnapshot;
import io.github.mcalgovisualizations.visualization.SnapShot;
import io.github.mcalgovisualizations.visualization.algorithms.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.models.IntList;
import net.minestom.server.snapshot.Snapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InsertionSortStepper implements AlgorithmStepper {

    private final IntList model;
    private final SortingState state = new SortingState();
    private boolean ALGORITHM_COMPLETE = false;

    public InsertionSortStepper(IntList model) {
        this.model = model;
    }

    public SnapShot start() {
        return getHistorySnapshot();
    }

    @Override
    public SnapShot step() {
        if (ALGORITHM_COMPLETE)
            return getHistorySnapshot();


        state.beginStep();
        if (state.currentIndex() >= model.size()) {
            ALGORITHM_COMPLETE = true;
            return getHistorySnapshot();
        }

        if (state.compareIndex() == -1) {
            state.setCompareIndex(state.currentIndex());
        }

        if (state.compareIndex() > 0) {
            state.highlightIndex(state.compareIndex());
            state.highlightIndex(state.compareIndex() - 1);
            state.addEvent(SortingState.SortingOperation.COMPARE);
        }

        int j = state.compareIndex();
        if (j > 0 && model.data()[j - 1] > model.data()[j]) {
            model.swap(j, j - 1);
            state.addEvent(SortingState.SortingOperation.SWAP);
            state.setCompareIndex(j - 1);
        } else {
            state.incrementCurrentIndex();
            state.setCompareIndex(-1);
        }

        return getHistorySnapshot();
    }

    @Override
    public SnapShot back() {
        return step(); // TODO : fix when history works
    }

    private HistorySnapshot getHistorySnapshot() {
        return new HistorySnapshot(
                model.toArray(),
                state.highlights(),
                new ArrayList<>(),
                state.currentIndex(),
                state.compareIndex(),
                ALGORITHM_COMPLETE
        );
    }

    @Override
    public SnapShot randomize() {
        // Fisher–Yates shuffle
        int[] data = model.data();

        for (int i = data.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));

            int temp = data[i];
            data[i] = data[j];
            data[j] = temp;
        }

        state.reset();
        ALGORITHM_COMPLETE = false;

        return getHistorySnapshot();
    }


    public boolean isDone() { return ALGORITHM_COMPLETE; }
}
