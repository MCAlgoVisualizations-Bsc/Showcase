package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Compare;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;

public final class CompareHandler implements AnimationHandler<Compare> {

    @Override
    public AnimationPlan handle(Compare event, RenderContext ctx) {
        return AnimationPlan.builder()
                .step(5, sceneOps -> {
                    sceneOps.hoverDisplay(event.x(), true);
                    sceneOps.hoverDisplay(event.y(), true);
                })
                .build();
    }
}
