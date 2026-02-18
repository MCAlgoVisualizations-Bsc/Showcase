package io.github.mcalgovisualizations.visualization.renderer.update.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Compare;
import io.github.mcalgovisualizations.visualization.renderer.update.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.update.dispatch.AnimationPlan;

public final class CompareHandler implements AnimationHandler<Compare> {

    @Override
    public AnimationPlan handle(Compare event, RenderContext ctx) {
        return null;
    }
}
