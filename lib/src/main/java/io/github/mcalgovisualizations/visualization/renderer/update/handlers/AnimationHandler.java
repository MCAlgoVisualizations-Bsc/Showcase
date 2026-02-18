package io.github.mcalgovisualizations.visualization.renderer.update.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;
import io.github.mcalgovisualizations.visualization.renderer.update.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.update.dispatch.AnimationPlan;

@FunctionalInterface
public interface AnimationHandler<E extends AlgorithmEvent> {
    AnimationPlan handle(E event, RenderContext ctx);
}
