package io.github.mcalgovisualizations.visualization.algorithms.sorting;

import io.github.mcalgovisualizations.visualization.algorithms.*;
import io.github.mcalgovisualizations.visualization.algorithms.events.*;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;


// TODO: remove insertion sort from AlgorithmStepper
public class AlgorithmStepper implements IAlgorithmStepper {
    private final ArrayList<HistorySnapshot> history = new ArrayList<>();
    private int historyPointer = 0;
    private final SortingState state = new SortingState();
    private final IPlayerSort algorithm;
    //private boolean ALGORITHM_COMPLETE = false;
    
    public AlgorithmStepper(IPlayerSort algorithm) {
        this.algorithm = algorithm;
    }

    public ISnapshot onStart() {
        history.add(getHistorySnapshot());

        return history.get(historyPointer);
    }

    @Override
    public ISnapshot step() {

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
    public @Nullable ISnapshot back() {
        if ((historyPointer - 1) < 0) return null;
        historyPointer--;
        return history.get(historyPointer);
    }

    private HistorySnapshot getHistorySnapshot() {
        return new HistorySnapshot(
                null,
                state.highlights(),
                new ArrayList<>(),// collection.events,
                state.currentIndex(),
                state.compareIndex(),
                false
        );
    }



}
