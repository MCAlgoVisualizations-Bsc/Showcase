package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.algorithms.events.Compare;
import io.github.mcalgovisualizations.visualization.algorithms.events.IAlgorithmEvent;
import io.github.mcalgovisualizations.visualization.algorithms.events.Swap;
import io.github.mcalgovisualizations.visualization.models.IDataModel;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SortingCollection<T extends Comparable<T>> extends AbstractCollection<T> implements ISort<T> {
    private final List<T> list = new ArrayList<>();
    public final List<IAlgorithmEvent> events = new ArrayList<>();

    private final IDataModel data;

    public SortingCollection(IDataModel data) {
        this.data = data;
    }

    public boolean add(T e) {
        return list.add(e);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public void swap(int i, int j) {
        events.add(new Swap(i, j));
        var ii = list.get(i);
        var jj = list.get(j);
        list.set(i, jj);
        list.set(j, ii);
    }

    @Override
    public boolean compare(int i, int j) {
        events.add(new Compare(i, j));
        return list.get(i).compareTo(list.get(j)) < 0;
    }

    @Override
    public T get(int i) {
        return list.get(i);
    }

    public void printEvents() {
        System.out.print("[");
        events.forEach(v -> System.out.print(v + ", "));
        System.out.println("]");
    }
}
