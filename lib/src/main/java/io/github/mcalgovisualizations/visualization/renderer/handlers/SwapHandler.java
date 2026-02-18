package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Swap;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;

public final class SwapHandler implements AnimationHandler<Swap> {
    @Override
    public AnimationPlan handle(Swap event, RenderContext ctx) {

        int a = event.x();
        int b = event.y();

//        var layout = ctx.layout();
//
//        var posA = layout.positionOf(a);
//        var posB = layout.positionOf(b);
//
//        return AnimationPlan.builder()
//
//                // Frame 1: lift
//                .frame(5, ops -> {
//                    ops.translateY(a, +1.0);
//                    ops.translateY(b, +1.0);
//                })
//
//                // Frame 2: cross
//                .frame(10, ops -> {
//                    ops.moveTo(a, posB);
//                    ops.moveTo(b, posA);
//                })
//
//                // Frame 3: drop
//                .frame(5, ops -> {
//                    ops.translateY(a, -1.0);
//                    ops.translateY(b, -1.0);
//                })
//
//                .build();'
        return null;
    }
}
