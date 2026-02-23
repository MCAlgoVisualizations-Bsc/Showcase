package io.github.mcalgovisualizations.visualization.algorithms.sorting;

import io.github.mcalgovisualizations.visualization.algorithms.events.Message.MessageType;
import io.github.mcalgovisualizations.visualization.renderer.handlers.AlgorithmMessages;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Insertion Sort specific messages and color configuration.
 */
public class InsertionSortMessages implements AlgorithmMessages {

    @Override
    public String onStart() {
        return "Starting Insertion Sort!";
    }

    @Override
    public String onComplete() {
        return "Insertion Sort complete!";
    }

    @Override
    public NamedTextColor colorFor(MessageType type) {
        return switch (type) {
            case SUCCESS -> NamedTextColor.GREEN;
            case ERROR   -> NamedTextColor.RED;
            case INFO    -> NamedTextColor.GRAY;
            case HINT    -> NamedTextColor.AQUA;
        };
    }
}
