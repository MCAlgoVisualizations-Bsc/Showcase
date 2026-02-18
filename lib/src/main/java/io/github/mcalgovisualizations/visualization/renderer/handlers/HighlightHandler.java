package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;
import io.github.mcalgovisualizations.visualization.algorithms.events.Highlight;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;

public class HighlightHandler implements AnimationHandler<Highlight> {

    @Override
    public AnimationPlan handle(Highlight event, RenderContext ctx) {
        return AnimationPlan.instant(scene -> {
            scene.setHighlighted(event.x(), true);
        });
    }
}
