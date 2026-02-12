package io.github.mcalgovisualizations.visualization.algorithms.sorting;

import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.List;

public final class SortingState {
    private int historyIndex = -1;
    private int currentIndex = 1;
    private int compareIndex = -1;

    private final List<Integer> highlights = new ArrayList<>();
    private final List<SortingOperation> events = new ArrayList<>();

    public void beginStep() {
        highlights.clear();
        events.clear();
    }

    @Override
    public String toString() {
        return historyIndex + " " + currentIndex + " " + compareIndex + " " + highlights.size() + " " + events.size();
    }

    public void reset() {
        currentIndex = 0;
        compareIndex = -1;
        highlights.clear();
        events.clear();
    }


    public int historyIndex() { return historyIndex; }
    public int currentIndex() { return currentIndex; }
    public int compareIndex() { return compareIndex; }

    public void setCompareIndex(int i) { compareIndex = i; }
    public void incrementCurrentIndex() { currentIndex++; }
    public void decrementHistoryIndex() { historyIndex--; }
    public void setHistoryIndex(int i) { historyIndex = i; }

    public void highlightIndex(int index) { highlights.add(index); }
    public int[] highlights() { return Ints.toArray(highlights); }

    public void addEvent(SortingOperation op) { events.add(op); }
    public List<SortingOperation> events() { return List.copyOf(events); }

    public enum SortingOperation { SWAP, COMPARE }


}

