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

    public void setHighlighted(boolean highlighted) {
        this.entity.setGlowing(highlighted);
        this.textEntity.setGlowing(highlighted);
    }

    public void setInstance(Instance instance) {
        this.entity.setInstance(instance);
        this.textEntity.setInstance(instance);
    }

    public Pos pos() {
        return this.pos;
    }

    public Block block() {
        var em  = (BlockDisplayMeta) entity.getEntityMeta();
        return em.getBlockStateId();
    }

    /**
     * Checks if the entities are currently associated with an instance.
     * * @return true if both entities are currently despawned/unassigned.
     */
    public boolean isSpawned() { return entity.getInstance() == null && textEntity.getInstance() == null; }

    /**
     * Teleports the block to the specified position and offsets the text label
     * to remain centered above the block.
     *
     * @param pos The new base position for the block display.
     */
    public void teleport(Pos pos) {
        this.entity.teleport(pos);
        this.textEntity.teleport(pos.add(1, 2.4, 1));
    }
//    private final T value;
//    private final Entity entity;
//    private final Entity text;
//
//    /**
//     * Constructs a new DisplayValue and spawns it into the specified instance.
//     *
//     * @param instance The Minestom instance where the entities will be spawned.
//     * @param value    The data value associated with this display.
//     * @param block    The block type to be used for the visual representation.
//     */
//    public DisplayValue(Instance instance, T value, Block block) {
//        this.value = value;
//
//        // Setup Block Display
//        entity = new Entity(EntityType.BLOCK_DISPLAY);
//        BlockDisplayMeta blockMeta = (BlockDisplayMeta) entity.getEntityMeta();
//        blockMeta.setBlockState(block);
//        blockMeta.setHasNoGravity(true);
//        // This controls how long the animation lasts (in ticks)
//        blockMeta.setPosRotInterpolationDuration(5);
//        // This tells the client to start the animation NOW (0 ticks from receiving)
//        blockMeta.setTransformationInterpolationStartDelta(0);
//        blockMeta.setScale(new Vec(2.0, 2.0, 2));
//
//        // Setup Text Display
//        text = new Entity(EntityType.TEXT_DISPLAY);
//        TextDisplayMeta textMeta = (TextDisplayMeta) text.getEntityMeta();
//        textMeta.setText(Component.text(value.toString(), NamedTextColor.GOLD));
//        textMeta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
//        textMeta.setScale(new Vec(4));
//        textMeta.setHasNoGravity(true);
//        textMeta.setPosRotInterpolationDuration(5);
//        textMeta.setTransformationInterpolationStartDelta(0);
//
//        entity.setInstance(instance);
//        text.setInstance(instance);
//    }
//
//    /**
//     * Compares this DisplayValue with another based on their underlying data values.
//     *
//     * @param o The other DisplayValue to compare against.
//     * @return A negative integer, zero, or a positive integer as this value
//     * is less than, equal to, or greater than the specified object.
//     */
//    @Override
//    public int compareTo(@NonNull DisplayValue<T> o) {
//        return this.value.compareTo(o.value);
//    }
//
//    /**
//     * @return The underlying data value.
//     */
//    public T getValue() {
//        return value;
//    }
//
//    /**
//     * Removes both the block and text entities from their current instance.
//     */
//    public void remove() {
//        entity.remove();
//        text.remove();
//    }
//
//    /**
//     * Checks if the entities are currently associated with an instance.
//     * * @return true if both entities are currently despawned/unassigned.
//     */
//    public boolean isSpawned() {
//        return entity.getInstance() == null && text.getInstance() == null;
//    }
//
//    /**
//     * Teleports the block to the specified position and offsets the text label
//     * to remain centered above the block.
//     *
//     * @param pos The new base position for the block display.
//     */
//    public void teleport(Pos pos) {
//        entity.teleport(pos);
//        text.teleport(pos.add(1, 2.4, 1));
//    }
//
//    /**
//     * Toggles the glowing effect (highlight) on both the block and text entities.
//     *
//     * @param highlighted true to enable glowing, false to disable.
//     */
//    public void setHighlighted(boolean highlighted) {
//        entity.setGlowing(highlighted);
//        text.setGlowing(highlighted);
//    }
}