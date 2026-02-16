package io.github.mcalgovisualizations.visualization.renderer.update.dispatch;

import io.github.mcalgovisualizations.visualization.algorithms.events.AlgorithmEvent;
import io.github.mcalgovisualizations.visualization.renderer.update.RenderContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Dispatcher {

    private final Map<Class<? extends AlgorithmEvent>, AnimationHandler<?>> handlers = new HashMap<>();
    private AnimationHandler<AlgorithmEvent> defaultHandler = (e, ctx) -> AnimationPlan.empty();

    public <E extends AlgorithmEvent> void register(Class<E> eventType, AnimationHandler<E> handler) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(handler, "handler");
        handlers.put(eventType, handler);
    }

    public void setDefaultHandler(AnimationHandler<AlgorithmEvent> handler) {
        this.defaultHandler = Objects.requireNonNull(handler, "handler");
    }

    public AnimationPlan dispatch(AlgorithmEvent event, RenderContext ctx) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(ctx, "ctx");

        var handler = handlers.get(event.getClass());
        if (handler == null) {
            return defaultHandler.handle(event, ctx);
        }

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

