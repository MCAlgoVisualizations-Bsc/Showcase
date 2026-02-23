package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Validate;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;

public class ValidateHandler implements AnimationHandler<Validate> {
    @Override
    public AnimationPlan handle(Validate event, RenderContext ctx) {
        return null;
    }
}
