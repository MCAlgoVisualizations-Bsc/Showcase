package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;

@FunctionalInterface
public interface AnimationHandler<E extends AlgorithmEvent> {
    AnimationPlan handle(E event, RenderContext ctx);
}
