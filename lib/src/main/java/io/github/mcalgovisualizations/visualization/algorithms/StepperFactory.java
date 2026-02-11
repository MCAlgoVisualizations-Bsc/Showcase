package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.algorithms.sorting.InsertionSortStepper;
import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.models.IntList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class StepperFactory {
    private StepperFactory() {}



    private record Entry(
            Class<? extends DataModel> modelType,
            Function<? super DataModel, ? extends AlgorithmStepper> ctor
    ) {}

    private static final Map<String, Entry> registry = new HashMap<>();

    public static <M extends DataModel> void register(
            String key,
            Class<M> modelType,
            Function<? super M, ? extends AlgorithmStepper> ctor
    ) {
        registry.put(key.toLowerCase(), new Entry(
                modelType,
                (DataModel m) -> ctor.apply(modelType.cast(m))
        ));
    }

    // register the algorithms here with their model
    static {
        register("insertionsort", IntList.class, InsertionSortStepper::new);
    }

    public static AlgorithmStepper create(String key, DataModel model) {
        Entry e = registry.get(key.toLowerCase());
        if (e == null) throw new IllegalArgumentException("No stepper registered for key: " + key);

        if (!e.modelType.isInstance(model)) {
            throw new IllegalArgumentException(
                    "Stepper '" + key + "' expects model " + e.modelType.getSimpleName()
                            + " but got " + model.getClass().getSimpleName()
            );
        }

        return e.ctor.apply(model);
    }
}
