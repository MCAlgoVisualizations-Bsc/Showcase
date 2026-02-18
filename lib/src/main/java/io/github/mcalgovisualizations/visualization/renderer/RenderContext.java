package io.github.mcalgovisualizations.visualization.renderer;

import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;

import java.util.List;

public record RenderContext(
        SceneOps sceneOps,
        List<AlgorithmEvent> event
) { }

