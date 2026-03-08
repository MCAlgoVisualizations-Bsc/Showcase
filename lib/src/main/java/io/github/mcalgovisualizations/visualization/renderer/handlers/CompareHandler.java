package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Compare;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class CompareHandler implements IAnimationHandler<Compare> {

    @Override
    public AnimationPlan handle(Compare event, RenderContext ctx) {
        return AnimationPlan.builder().build();
    }
}


