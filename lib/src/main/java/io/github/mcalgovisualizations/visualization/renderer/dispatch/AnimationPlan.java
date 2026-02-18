package io.github.mcalgovisualizations.visualization.renderer.dispatch;

import io.github.mcalgovisualizations.visualization.renderer.SceneOps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class AnimationPlan {

    public static final class Step {
        private final int ticks; // how long to wait AFTER running the op (can be 0)
        private final Consumer<SceneOps> op;

        Step(int ticks, Consumer<SceneOps> op) {
            if (ticks < 0) throw new IllegalArgumentException("ticks must be >= 0");
            this.ticks = ticks;
            this.op = Objects.requireNonNull(op, "op");
        }

        public int ticks() { return ticks; }
        public Consumer<SceneOps> op() { return op; }
    }

    private final List<Step> steps;

    private AnimationPlan(List<Step> steps) {
        this.steps = List.copyOf(steps);
    }

    public List<Step> steps() {
        return Collections.unmodifiableList(steps);
    }

    public boolean isEmpty() {
        return steps.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AnimationPlan instant(Consumer<SceneOps> op) {
        return builder().step(0, op).build();
    }

    public static final class Builder {
        private final List<Step> steps = new ArrayList<>();

        /**
         * Runs {@code op} once, then waits {@code ticks} executor ticks before the next step.
         */
        public Builder step(int ticks, Consumer<SceneOps> op) {
            steps.add(new Step(ticks, op));
            return this;
        }

        public AnimationPlan build() {
            return new AnimationPlan(steps);
        }
    }
}
