package io.github.mcalgovisualizations.visualization.renderer;

import io.github.mcalgovisualizations.visualization.algorithms.events.IAlgorithmEvent;

import java.util.List;

public record RenderContext(
        ISceneOps sceneOps,
        List<IAlgorithmEvent> event
) { }

