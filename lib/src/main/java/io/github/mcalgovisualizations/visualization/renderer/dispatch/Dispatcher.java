package io.github.mcalgovisualizations.visualization.renderer.dispatch;

import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.handlers.AnimationHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Dispatcher {

    private final Map<Class<? extends AlgorithmEvent>, AnimationHandler<?>> handlers = new HashMap<>();

    public <E extends AlgorithmEvent> void register(Class<E> eventType, AnimationHandler<E> handler) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(handler, "handler");
        handlers.put(eventType, handler);
    }


    public AnimationPlan dispatch(AlgorithmEvent event, RenderContext ctx) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(ctx, "ctx");

        var handler = handlers.get(event.getClass());

        return invokeUnchecked(handler, event, ctx);
    }

    @SuppressWarnings("unchecked")
    private static <E extends AlgorithmEvent> AnimationPlan invokeUnchecked(
            AnimationHandler<?> raw,
            AlgorithmEvent event,
            RenderContext ctx
    ) {
        return ((AnimationHandler<E>) raw).handle((E) event, ctx);
    }
}

