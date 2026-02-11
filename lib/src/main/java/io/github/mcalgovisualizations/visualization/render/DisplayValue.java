package io.github.mcalgovisualizations.visualization.render;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

/**
 * Represents a visual element in a visualization, consisting of a {@link Block}
 * and an associated floating text label.
 *
 */
public class DisplayValue {
    private final Pos       pos;
    private final Entity    entity;
    private final Entity    textEntity;

    public DisplayValue(Pos pos, Block block) {
        if (block == null) throw new NullPointerException("block cannot be null!");

        this.pos = pos;
        this.entity = new Entity(EntityType.BLOCK_DISPLAY);
        this.textEntity = new Entity(EntityType.TEXT_DISPLAY);

        // meta data
        var em = (BlockDisplayMeta) entity.getEntityMeta();
        var tm = (TextDisplayMeta) textEntity.getEntityMeta();

        // Set up Block Display
        em.setBlockState(block);
        em.setHasNoGravity(true);
        // This controls how long the animation lasts (in ticks)
        em.setPosRotInterpolationDuration(5);
        // This tells the client to start the animation NOW (0 ticks from receiving)
        em.setTransformationInterpolationStartDelta(0);
        em.setScale(new Vec(2.0, 2.0, 2));
    }

    /**
     * Constructs a new DisplayValue and spawns it into the specified instance.
     *
     * @param pos       The position of the entity.
     * @param block     The block type to be used for the visual representation.
     * @param text      The text to be shown above visual representation.
     */
    public DisplayValue(Pos pos, Block block, String text) {
        if (block == null) throw new NullPointerException("block cannot be null!");
        if (text.isBlank()) throw new IllegalArgumentException("text cannot be blank!");

        this.pos = pos;
        this.entity = new Entity(EntityType.BLOCK_DISPLAY);
        this.textEntity = new Entity(EntityType.TEXT_DISPLAY);

        // meta data
        var em = (BlockDisplayMeta) entity.getEntityMeta();
        var tm = (TextDisplayMeta) textEntity.getEntityMeta();

        // Set up Block Display
        em.setBlockState(block);
        em.setHasNoGravity(true);
        // This controls how long the animation lasts (in ticks)
        em.setPosRotInterpolationDuration(5);
        // This tells the client to start the animation NOW (0 ticks from receiving)
        em.setTransformationInterpolationStartDelta(0);
        em.setScale(new Vec(2.0, 2.0, 2));

        // Set up Text Display
        tm.setText(Component.text(text, NamedTextColor.GOLD));

        tm.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        tm.setScale(new Vec(4));
        tm.setHasNoGravity(true);
        tm.setPosRotInterpolationDuration(5);
        tm.setTransformationInterpolationStartDelta(0);
    }

    /**
     * Removes both the block and text entities from their current instance.
     */
    public void remove() {
        this.entity.remove();
        this.textEntity.remove();
    }

    /**
     * Toggles the glowing effect (highlight) on both the block and text entities.
     *
     * @param highlighted true to enable glowing, false to disable.
     */
    public void setHighlighted(boolean highlighted) {
        this.entity.setGlowing(highlighted);
        this.textEntity.setGlowing(highlighted);
    }

    /**
     * Assigns both the block display entity and the text display entity to an instance.
     * <p>
     * After calling this, the entities will be spawned/visible in that instance (subject to
     * client view distance and other normal Minestom rules).
     *
     * @param instance The instance to spawn/attach both entities to.
     */
    public void setInstance(Instance instance) {
        this.entity.setInstance(instance);
        this.textEntity.setInstance(instance);
    }

    /**
     * Returns the base position associated with this display value.
     * <p>
     * Note: this is the stored position passed in the constructor; it is not updated by {@link #teleport(Pos)}.
     *
     * @return The original/base {@link Pos} for this display value.
     */
    public Pos pos() {
        return this.pos;
    }

    /**
     * Returns the current block state id used by the underlying {@link EntityType#BLOCK_DISPLAY}.
     *
     * @return The block state id currently set on the block display.
     */
    public Block block() {
        var em  = (BlockDisplayMeta) entity.getEntityMeta();
        return em.getBlockStateId();
    }

    /**
     * Checks whether both entities are currently <em>unassigned</em> to any {@link Instance}.
     * <p>
     * This method returns {@code true} when both entities have no instance (i.e., they are not spawned).
     *
     * @return {@code true} if both entities have {@code null} instances; otherwise {@code false}.
     */
    public boolean isSpawned() { return entity.getInstance() == null && textEntity.getInstance() == null; }

    /**
     * Teleports the block display entity to the specified position and teleports the
     * text display entity to a fixed offset above it.
     * <p>
     * The text is offset by {@code (1, 2.4, 1)} to keep it centered above a 2x-scaled block display.
     *
     * @param pos The new base position for the block display entity.
     */
    public void teleport(Pos pos) {
        this.entity.teleport(pos);
        this.textEntity.teleport(pos.add(1, 2.4, 1));
    }
}
