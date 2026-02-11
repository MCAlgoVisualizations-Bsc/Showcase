package io.github.mcalgovisualizations.visualization.models;

/**
 * Represents a generic data source for a visualization layout.
 * <p>
 * Implementations define how many elements (e.g., nodes, values, vertices)
 * should be rendered by a layout.
 */
public interface DataModel {

    /**
     * Returns the number of elements contained in this model.
     * <p>
     * This value is typically used by layout implementations to determine
     * how many visual elements to generate and position.
     *
     * @return the total number of elements in the model
     */
    int size();
}
