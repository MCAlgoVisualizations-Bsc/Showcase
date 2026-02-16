package io.github.mcalgovisualizations.visualization.renderer.update.dispatch;

import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;
import io.github.mcalgovisualizations.visualization.renderer.update.RenderContext;

@FunctionalInterface
public interface AnimationHandler<E extends AlgorithmEvent> {
    AnimationPlan handle(E event, RenderContext ctx);
}
