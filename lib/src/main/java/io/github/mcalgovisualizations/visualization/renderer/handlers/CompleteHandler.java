package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Complete;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;

import java.awt.event.ActionEvent;

public class CompleteHandler implements AnimationHandler<Complete>{
    @Override
    public AnimationPlan handle(Complete event, RenderContext ctx) {
        return null;
    }
}
