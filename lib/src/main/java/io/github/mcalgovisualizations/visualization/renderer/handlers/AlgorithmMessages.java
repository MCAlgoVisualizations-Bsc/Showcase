package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Message.MessageType;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Provides algorithm-specific message text and color overrides.
 * Each algorithm implements this to configure what players see in chat/hologram.
 */
public interface AlgorithmMessages {

    /** Text shown when the algorithm starts. */
    String onStart();

    /** Text shown when the algorithm completes. */
    String onComplete();

    /** Color override for a given MessageType. Defaults cover all cases. */
    default NamedTextColor colorFor(MessageType type) {
        return type.color();
    }
}
