package org.example.visualization.core;

import net.minestom.server.entity.Player;

/**
 * Base interface for all algorithm visualizations.
 * Each visualization represents a step-by-step demonstration of an algorithm.
 */
public interface Visualization {
    /**
     * Randomize the input values for the visualization.
     */
    void randomize();

    /**
     * Start the automatic visualization playback.
     * @param player The player who started the visualization
     */
    void start(Player player);

    /**
     * Stop the visualization playback.
     */
    void stop();

    /**
     * Execute one step forward in the algorithm.
     */
    void stepForward();

    /**
     * Go back one step in the algorithm history.
     */
    void stepBack();

    /**
     * Set the speed of the visualization.
     * @param ticksPerStep Number of ticks between each step (20 ticks = 1 second)
     */
    void setSpeed(int ticksPerStep);

    /**
     * Check if the visualization is currently running.
     * @return true if running, false otherwise
     */
    boolean isRunning();

    /**
     * Get the name of this visualization.
     * @return The display name
     */
    String getName();

    /**
     * Clean up resources (entities, tasks, etc.)
     */
    void cleanup();
}
