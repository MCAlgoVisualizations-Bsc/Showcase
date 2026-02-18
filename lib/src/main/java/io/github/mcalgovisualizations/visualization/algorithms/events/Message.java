package io.github.mcalgovisualizations.visualization.algorithms.events;

public record Message(String message, MessageType type) implements AlgorithmEvent {
    public enum MessageType {
        INFO,
        ERROR,
        SUCCESS,
        HINT
    }
}
