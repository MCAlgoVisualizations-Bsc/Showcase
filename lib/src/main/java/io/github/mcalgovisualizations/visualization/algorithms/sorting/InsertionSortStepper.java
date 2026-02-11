package io.github.mcalgovisualizations.visualization.algorithms.sorting;

import io.github.mcalgovisualizations.visualization.algorithms.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.models.IntList;

public class InsertionSortStepper implements AlgorithmStepper {
    private final IntList model;

    private SortingState state;
    private boolean ALGORITHM_COMPLETE;

    public InsertionSortStepper(IntList model) {
        this.model = model;
        state = new SortingState();
        ALGORITHM_COMPLETE = false;
    }

    @Override
    public SortingState step() {
        if (ALGORITHM_COMPLETE) return this.state;

        /*
        for (DisplayValue<Integer> v : values) {
            v.setHighlighted(false);
        }
         */

        if (state.currentIndex >= model.size()) {
            ALGORITHM_COMPLETE = true;
            return this.state;
        }

        if (state.compareIndex == -1) {
            state.compareIndex = state.currentIndex;
        }

        if (state.compareIndex > 0) {
           /* TODO : set highlighted blocks?
            values.get(compareIndex).setHighlighted(true);
            values.get(compareIndex - 1).setHighlighted(true);
            */
        }

        if (state.compareIndex > 0 && model.data()[state.compareIndex - 1] > model.data()[state.compareIndex]) {
            model.swap(state.compareIndex, state.compareIndex - 1);
            state.compareIndex--;

            /* TODO : Fix plays sounds once swapping.
            instance.playSound(
                    Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_BANJO, Sound.Source.RECORD, 1.0f, 1.2f),
                    origin
            );
             */
            // saveState(); moved to controller;
        } else {
            state.currentIndex++;
            state.compareIndex = -1;
        }

        return state;
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
