package io.github.mcalgovisualizations.visualization.renderer.dispatch;

import io.github.mcalgovisualizations.visualization.algorithms.events.IAlgorithmEvent;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.handlers.IAnimationHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Dispatcher {

    private final Map<Class<? extends IAlgorithmEvent>, IAnimationHandler<?>> handlers = new HashMap<>();

    public <E extends IAlgorithmEvent> void register(Class<E> eventType, IAnimationHandler<E> handler) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(handler, "handler");
        handlers.put(eventType, handler);
    }

    public AnimationPlan dispatch(IAlgorithmEvent event, RenderContext ctx) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(ctx, "ctx");

        var handler = handlers.get(event.getClass());

        return invokeUnchecked(handler, event, ctx);
    }

    @SuppressWarnings("unchecked")
    private static <E extends IAlgorithmEvent> AnimationPlan invokeUnchecked(
            IAnimationHandler<?> raw,
            IAlgorithmEvent event,
            RenderContext ctx
    ) {
        return ((IAnimationHandler<E>) raw).handle((E) event, ctx);
    }
}

