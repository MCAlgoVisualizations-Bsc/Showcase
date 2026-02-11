package io.github.mcalgovisualizations.visualization.algorithms.sorting;

import io.github.mcalgovisualizations.visualization.algorithms.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.layouts.FloatingLinearLayout;
import io.github.mcalgovisualizations.visualization.layouts.Layout;
import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.models.Graph;
import io.github.mcalgovisualizations.visualization.models.IntList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortingState {
    // Algorithm state
    public int historyIndex = -1;
    public int currentIndex = 1;      // The element we're currently inserting
    public int compareIndex = -1;     // Current position during insertion

    private final List<Integer> highlights = new ArrayList<>();

    public void setHighlights(int v) {
        this.highlights.add(v);
    }

    public void clearHighlights() {
        this.highlights.clear();
    }

    // TODO : potentially make this an array instead of list?
    public List<Integer> getHighlights() {
        return List.copyOf(this.highlights);
    }

    public enum sortingOperations {
        SWAP, COMPARE
    }
}
