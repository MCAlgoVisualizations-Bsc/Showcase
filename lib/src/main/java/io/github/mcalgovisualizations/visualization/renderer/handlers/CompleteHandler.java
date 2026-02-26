package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Complete;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.ISceneOps;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;

public class CompleteHandler implements IAnimationHandler<Complete> {
    @Override
    public AnimationPlan handle(Complete event, RenderContext ctx) {
        return AnimationPlan.builder()
                .step(0, ISceneOps::stopAnimations)
                .step(0, sceneOps -> sceneOps.playEffect(2, "SUCCESS"))
                .build();
    }
}
