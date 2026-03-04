package io.github.mcalgovisualizations.visualization.algorithms;

import io.github.mcalgovisualizations.visualization.models.IDataModel;

public abstract class AbstractAlgorithm implements IPlayerSort {
    protected final IDataModel data;
    public AbstractAlgorithm(IDataModel data) {
        this.data = data;
    }

}
