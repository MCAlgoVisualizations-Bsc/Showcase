package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Swap;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;

import java.time.Duration;

public final class SwapHandler implements AnimationHandler<Swap> {
    @Override
    public AnimationPlan handle(Swap event, RenderContext ctx) {
        return AnimationPlan.instant(sceneOps -> {
            sceneOps.setHighlighted(event.x(), true);
            sceneOps.setHighlighted(event.y(), true);
            sceneOps.swapSlots(event.x(), event.y());
            sceneOps.setHighlighted(event.x(), false);
            sceneOps.setHighlighted(event.y(), false);
        });
    }
}
