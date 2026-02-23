package io.github.mcalgovisualizations.visualization.renderer.handlers;

import io.github.mcalgovisualizations.visualization.algorithms.events.Message;
import io.github.mcalgovisualizations.visualization.renderer.RenderContext;
import io.github.mcalgovisualizations.visualization.renderer.dispatch.AnimationPlan;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

/**
 * Per-instance message handler. Created per player/algorithm pair by VisualizationManager.
 *
 * Public interface:
 *   send(MessageType, text)  — send a typed message to the player
 *   start()                  — called when visualization begins
 *   cleanup()                — called when visualization ends
 */
public class MessageHandler implements AnimationHandler<Message> {

    private final Player player;
    private final AlgorithmMessages messages;

    public MessageHandler(Player player, AlgorithmMessages messages) {
        this.player = player;
        this.messages = messages;
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    /** Called when the visualization is assigned and started. */
    public void start() {
        send(Message.MessageType.INFO, messages.onStart());
    }

    /** Called when the visualization is cleaned up (player leaves, new algo selected). */
    public void cleanup() {
        // No hologram to tear down here — VisualizationScene owns the hologram entity.
        // Extend here if MessageHandler ever owns its own resources.
    }

    // ── Core send ─────────────────────────────────────────────────────────────

    /**
     * Send a typed message to the player in chat.
     *
     * @param type The message type (controls color via AlgorithmMessages)
     * @param text The message text
     */
    public void send(Message.MessageType type, String text) {
        player.sendMessage(Component.text(text, messages.colorFor(type)));
    }

    // ── AnimationHandler (hologram updates via SceneOps) ─────────────────────

    /**
     * Called by the Dispatcher during animation to update the scene hologram.
     * Uses AlgorithmMessages for the color, keeping algorithm config in one place.
     */
    @Override
    public AnimationPlan handle(Message event, RenderContext ctx) {
        return AnimationPlan.instant(sceneOps ->
                sceneOps.setStatusText(event.message(), messages.colorFor(event.type())));
    }
}
