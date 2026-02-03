package visualization.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jspecify.annotations.NonNull;

public class DisplayValue<T extends Comparable<T>> implements Comparable<DisplayValue<T>> {
    private final T value;
    private final Entity entity;
    private final Entity text;

    public DisplayValue(Instance instance, T value, Block block) {
        this.value = value;
        entity = new Entity(EntityType.BLOCK_DISPLAY);
        BlockDisplayMeta blockMeta = (BlockDisplayMeta) entity.getEntityMeta();
        blockMeta.setBlockState(block);
        blockMeta.setHasNoGravity(true);
        // This controls how long the animation lasts (in ticks)
        blockMeta.setPosRotInterpolationDuration(5);
        // This tells the client to start the animation NOW (0 ticks from receiving)
        blockMeta.setTransformationInterpolationStartDelta(40);

        // Setup Text
        text = new Entity(EntityType.TEXT_DISPLAY);
        TextDisplayMeta textMeta = (TextDisplayMeta) text.getEntityMeta();
        textMeta.setText(Component.text(value.toString(), NamedTextColor.GOLD));
        textMeta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        textMeta.setBackgroundColor(new Color(255, 255,  255).asRGB());
        textMeta.setScale(new Vec(1.5, 1.5, 1.5));
        textMeta.setHasNoGravity(true);
        textMeta.setPosRotInterpolationDuration(5);
        textMeta.setTransformationInterpolationStartDelta(0);

        entity.setInstance(instance);
        text.setInstance(instance);
    }

    @Override
    public int compareTo(@NonNull DisplayValue<T> o) {
        return this.value.compareTo(o.value);
    }

    public T getValue() {
        return value;
    }

    public void remove() {
        entity.remove();
        text.remove();
    }

    public boolean isSpawned() {
        return entity.getInstance() == null && text.getInstance() == null;
    }

    public void teleport(Pos pos) {
        entity.teleport(pos);
        text.teleport(pos.add(.5, 1.2, .5));
    }

    public void setHighlighted(boolean highlighted) {
        entity.setGlowing(highlighted);
        text.setGlowing(highlighted);
    }
}
