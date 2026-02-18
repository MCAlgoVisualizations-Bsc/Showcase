package io.github.mcalgovisualizations.visualization.renderer.update;

import io.github.mcalgovisualizations.visualization.Snapshot;
import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;
import io.github.mcalgovisualizations.visualization.renderer.DisplayValue;

import java.util.List;
import java.util.Map;

public record RenderContext(
        SceneOps sceneOps,
        List<AlgorithmEvent> event
) { }

