package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.MessageEvent;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;

public class MessageHandler implements AnimationHandler<MessageEvent>{
    @Override
    public AnimationPlan handle(MessageEvent event, RenderContext ctx) {
        return null;
    }
}
