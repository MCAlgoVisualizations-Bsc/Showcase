package io.github.mcalgovisualizations.visualization.algorithms.sorting;

import io.github.mcalgovisualizations.visualization.algorithms.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.models.IntList;

import java.io.Serializable;

public class InsertionSortStepper implements AlgorithmStepper {
    
    private final IntList model;
    private final SortingState state;
    private boolean ALGORITHM_COMPLETE;

    public InsertionSortStepper(IntList model) {
        this.model = model;
        this.state = new SortingState();
        ALGORITHM_COMPLETE = false;
    }

    // Make this compatible with outside algorithms/operations.
    @Override
    public SortingState step() {
        if (ALGORITHM_COMPLETE) return this.state;

        if (state.currentIndex >= model.size()) {
            ALGORITHM_COMPLETE = true;
            return this.state;
        }

        if (state.compareIndex == -1) {
            state.compareIndex = state.currentIndex;
        }

        if (state.compareIndex > 0) {
            var x = model.data()[state.compareIndex];
            var y = model.data()[state.compareIndex - 1];
            state.setHighlights(x);
            state.setHighlights(y);
        }

        if (state.compareIndex > 0 && model.data()[state.compareIndex - 1] > model.data()[state.compareIndex]) {
            model.swap(state.compareIndex, state.compareIndex - 1);
            state.compareIndex--;
        } else {
            state.currentIndex++;
            state.compareIndex = -1;
        }

        return state;
    }

    public int[] getRender() {
        return this.model.toArray();
    }

    @Override
    public SortingState back() {
        if (state.historyIndex < 0) return state;
        state.historyIndex--;
        ALGORITHM_COMPLETE = false;
        return state;
    }


    public boolean isDone() { return ALGORITHM_COMPLETE; }
}
