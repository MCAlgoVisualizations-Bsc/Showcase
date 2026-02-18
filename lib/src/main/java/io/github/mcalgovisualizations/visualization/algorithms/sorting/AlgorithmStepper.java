package io.github.mcalgovisualizations.visualization.algorithms.sorting;

import io.github.mcalgovisualizations.visualization.HistorySnapshot;
import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.algorithms.IAlgorithmStepper;
import io.github.mcalgovisualizations.visualization.algorithms.events.*;
import io.github.mcalgovisualizations.visualization.models.IntList;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;


// TODO: remove insertion sort from AlgorithmStepper
public class AlgorithmStepper implements IAlgorithmStepper {
    private final ArrayList<HistorySnapshot> history = new ArrayList<>();
    private int historyPointer = 0;
    private final IntList model;
    private final SortingState state = new SortingState();
    private boolean ALGORITHM_COMPLETE = false;

    public AlgorithmStepper(IntList model) {
        this.model = model;
    }

    public Snapshot onStart() {
        state.addEvent(new MessageEvent("Starting Insertion Sort", MessageEvent.MessageType.INFO));
        history.add(getHistorySnapshot());
        return history.get(historyPointer);
    }

    @Override
    public Snapshot step() {
        // Check if step is old
        if ((historyPointer + 1) < history.size()) {
            historyPointer++;
            return history.get(historyPointer);
        }

        // Check if done
        if (ALGORITHM_COMPLETE) {
            state.addEvent(new Complete());
            System.out.println("Done");
            return history.get(historyPointer);
        }

        // calc next step
        state.beginStep();
        if (state.currentIndex() >= model.size()) {
            ALGORITHM_COMPLETE = true;
            state.addEvent(new MessageEvent("Sorting complete!", MessageEvent.MessageType.SUCCESS));
            state.addEvent(new Complete());
            history.add(getHistorySnapshot());
            historyPointer++;
            return history.get(historyPointer);
        }

        if (state.compareIndex() == -1) {
            state.setCompareIndex(state.currentIndex());
        }

        int j = state.compareIndex();
        if (j > 0) {
            state.addEvent(new Highlight(j));
            state.addEvent(new Highlight(j - 1));
            state.addEvent(new Compare(j, j - 1));
            state.addEvent(new MessageEvent("Comparing indices " + j + " and " + (j - 1), MessageEvent.MessageType.INFO));

            if (model.data()[j - 1] > model.data()[j]) {
                model.swap(j, j - 1);
                state.addEvent(new Swap(j - 1, j));
                state.addEvent(new MessageEvent("Swapped " + j + " and " + (j - 1), MessageEvent.MessageType.INFO));
                state.setCompareIndex(j - 1);
            } else {
                state.addEvent(new MessageEvent("Element in correct position", MessageEvent.MessageType.SUCCESS));
                state.incrementCurrentIndex();
                state.setCompareIndex(-1);
            }
        } else {
            state.incrementCurrentIndex();
            state.setCompareIndex(-1);
        }

        history.add(getHistorySnapshot());
        historyPointer++;
        return history.get(historyPointer);
    }

    @Override
    public @Nullable Snapshot back() {
        if ((historyPointer - 1) < 0) return null;
        historyPointer--;
        return history.get(historyPointer);
    }

    private HistorySnapshot getHistorySnapshot() {
        return new HistorySnapshot(
                model.toArray(),
                state.highlights(),
                state.events(),
                state.currentIndex(),
                state.compareIndex(),
                ALGORITHM_COMPLETE
        );
    }

    @Override
    public Snapshot randomize() {
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

        history.clear();
        history.add(getHistorySnapshot());
        historyPointer++;
        return history.get(historyPointer);
    }


    public boolean isDone() { return ALGORITHM_COMPLETE; }
}
