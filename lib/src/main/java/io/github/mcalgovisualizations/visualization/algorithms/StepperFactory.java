package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.algorithms.sorting.AlgorithmStepper;
import io.github.mcalgovisualizations.visualization.models.IDataModel;
import io.github.mcalgovisualizations.visualization.models.IntList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class StepperFactory {
    private StepperFactory() {}

    private record Entry(
            Class<? extends IDataModel> modelType,
            Function<? super IDataModel, ? extends IAlgorithmStepper> ctor
    ) {}

    private static final Map<String, Entry> registry = new HashMap<>();

    public static <M extends IDataModel> void register(
            String key,
            Class<M> modelType,
            Function<? super M, ? extends IAlgorithmStepper> ctor
    ) {
        registry.put(key.toLowerCase(), new Entry(
                modelType,
                (IDataModel m) -> ctor.apply(modelType.cast(m))
        ));
    }

    public static IAlgorithmStepper create(String key, IDataModel model) {
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
