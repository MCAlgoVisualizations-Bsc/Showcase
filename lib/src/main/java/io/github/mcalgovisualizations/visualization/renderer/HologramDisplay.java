package io.github.mcalgovisualizations.visualization.renderer;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;

/**
 * A floating text hologram displayed above the visualization.
 * Uses a TEXT_DISPLAY entity so it's always billboard-facing the viewer.
 */
public final class HologramDisplay {

    private final Entity textEntity;

    public HologramDisplay(Instance instance, Pos pos) {
        this.textEntity = new Entity(EntityType.TEXT_DISPLAY);

        var meta = (TextDisplayMeta) textEntity.getEntityMeta();
        meta.setText(Component.empty());
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        meta.setHasNoGravity(true);
        meta.setScale(new net.minestom.server.coordinate.Vec(3));
        meta.setShadow(true);
        meta.setBackgroundColor(0x40000000); // semi-transparent dark background

        textEntity.setInstance(instance);
        textEntity.teleport(pos);
    }

    /** Update the displayed text. */
    public void setText(Component text) {
        var meta = (TextDisplayMeta) textEntity.getEntityMeta();
        meta.setText(text);
    }

    public void addViewer(Player player) {
        textEntity.addViewer(player);
    }

    public void remove() {
        textEntity.remove();
    }
}




