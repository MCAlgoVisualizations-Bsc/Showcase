package io.github.mcalgovisualizations.visualization.renderer.dispatch;

public interface AnimationPlan {
    static AnimationPlan empty() { return EmptyPlan.INSTANCE; }

    final class EmptyPlan implements AnimationPlan {
        static final EmptyPlan INSTANCE = new EmptyPlan();
        private EmptyPlan() {}
    }



}
