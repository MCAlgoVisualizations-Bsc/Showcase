package visualization.core;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.block.Block;
import org.jspecify.annotations.NonNull;

public class DisplayValue<T extends Comparable<T>> implements Comparable<DisplayValue<T>> {
    private T value;
    private final Entity entity;

    public DisplayValue(T value, Block block) {
        this.value = value;
        entity = new Entity(EntityType.BLOCK_DISPLAY);
        BlockDisplayMeta blockMeta = (BlockDisplayMeta) entity.getEntityMeta();
        blockMeta.setBlockState(block);
        blockMeta.setHasNoGravity(true);

        // This controls how long the animation lasts (in ticks)
        blockMeta.setPosRotInterpolationDuration(5);

        // This tells the client to start the animation NOW (0 ticks from receiving)
        blockMeta.setTransformationInterpolationStartDelta(0);
    }

    public void remove() {
        entity.remove();
    }

    @Override
    public int compareTo(@NonNull DisplayValue<T> o) {
        return this.value.compareTo(o.value);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Entity getEntity() {
        return entity;
    }
}
