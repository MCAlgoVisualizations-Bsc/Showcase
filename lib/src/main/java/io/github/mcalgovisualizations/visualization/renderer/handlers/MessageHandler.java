package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Message;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageHandler implements AnimationHandler<Message>{
    @Override
    public AnimationPlan handle(Message event, RenderContext ctx) {
        return AnimationPlan.instant(sceneOps -> sceneOps.sendMessage(event.message(), color(event)));
    }

    private NamedTextColor color(Message event) {
        return switch (event.type()) {
            case INFO -> NamedTextColor.GRAY;
            case SUCCESS -> NamedTextColor.GREEN;
            case ERROR -> NamedTextColor.RED;
            case HINT -> NamedTextColor.AQUA;
        };
    }
}
