package io.github.mcalgovisualizations.visualization.renderer.update;

import io.github.mcalgovisualizations.visualization.Snapshot;

public record RenderContext(SceneOps scene, LayoutResult layout, Snapshot snapshot) {

}

