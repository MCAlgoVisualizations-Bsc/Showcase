package io.github.mcalgovisualizations.visualization.algorithms.events;

public record MessageEvent(String message, MessageType type) implements AlgorithmEvent {
    public enum MessageType {
        INFO,
        ERROR,
        SUCCESS,
        HINT
    }
}
