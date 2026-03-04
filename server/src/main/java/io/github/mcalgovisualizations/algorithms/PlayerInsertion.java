package io.github.mcalgovisualizations.algorithms;

import io.github.mcalgovisualizations.visualization.algorithms.AbstractAlgorithm;
import io.github.mcalgovisualizations.visualization.algorithms.SortingCollection;
import io.github.mcalgovisualizations.visualization.models.IDataModel;
import io.github.mcalgovisualizations.visualization.models.IntList;

import java.util.Arrays;

public class PlayerInsertion extends AbstractAlgorithm {

    public PlayerInsertion(IDataModel data) {
        super(data);
    }

    public void sort(SortingCollection<?> values) {
        System.out.println(values.size());

        int n = values.size();
        for (int i = 0; i < n - 1; i++) {

            // Assume the current position holds
            // the minimum element
            int min_idx = i;

            // Iterate through the unsorted portion
            // to find the actual minimum
            for (int j = i + 1; j < n; j++) {
                if (values.compare(i, j)) {

                    // Update min_idx if a smaller element
                    // is found
                    min_idx = j;
                }
            }

            // Move minimum element to its
            // correct position
            values.swap(i, min_idx);
        }

        values.events.forEach(System.out::println);
    }


    static void main(String[] args) {
        var data = new IntList(new int[]{1231, 39, 0, -1, 478, 56, 20});
        var algorithm = new PlayerInsertion(data);
        var collection = new SortingCollection<>();

        algorithm.sort(collection);

        collection.printEvents();

        System.out.println(Arrays.toString(data.data()));
    }
}
