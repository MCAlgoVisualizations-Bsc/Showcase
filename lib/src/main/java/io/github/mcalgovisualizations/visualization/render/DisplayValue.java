package io.github.mcalgovisualizations.visualization.render;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.time.Duration;

public class DisplayValue {

    private final Entity blockEntity;
    private final Entity textEntity;

    private static final Vec TEXT_OFFSET = new Vec(1, 2.4, 1);

    private Pos pos;
    private Block currentBlock;

    public DisplayValue(Pos pos, Block block, String text) {
        if (block == null) throw new NullPointerException("block cannot be null");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("text cannot be blank");

        this.blockEntity = new Entity(EntityType.BLOCK_DISPLAY);
        this.textEntity = new Entity(EntityType.TEXT_DISPLAY);

        this.pos = pos;
        this.currentBlock = block;

        setupBlock(block);
        setupText(text);

        // IMPORTANT: do NOT teleport here if your spawn positioning is scheduled after setInstance()
        // teleport(pos);
    }

    public Pos getPos() {
        return pos;
    }

    private void setupBlock(Block block) {
        var meta = (BlockDisplayMeta) blockEntity.getEntityMeta();
        meta.setBlockState(block);
        meta.setHasNoGravity(true);
        meta.setPosRotInterpolationDuration(5);
        meta.setTransformationInterpolationStartDelta(0);
        meta.setScale(new Vec(2.0, 2.0, 2.0));
    }

    private void setupText(String text) {
        var meta = (TextDisplayMeta) textEntity.getEntityMeta();
        meta.setText(Component.text(text, NamedTextColor.GOLD));
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        meta.setScale(new Vec(4));
        meta.setHasNoGravity(true);
        meta.setPosRotInterpolationDuration(5);
        meta.setTransformationInterpolationStartDelta(0);
    }

    public void setInstance(Instance instance) {
        blockEntity.setInstance(instance);
        textEntity.setInstance(instance);
    }

    public void remove() {
        blockEntity.remove();
        textEntity.remove();
    }

    public void teleport(Pos pos) {
        this.pos = pos;
        blockEntity.teleport(pos);
        textEntity.teleport(pos.add(TEXT_OFFSET));

        MinecraftServer.getSchedulerManager()
                .buildTask(() -> this.teleport(pos))
                .delay(Duration.ofMillis(100))
                .schedule();
    }

    public void updateValue(int value) {
        var meta = (TextDisplayMeta) textEntity.getEntityMeta();
        meta.setText(Component.text(Integer.toString(value), NamedTextColor.GOLD));
    }

    public void updateBlock(Block block) {
        if (block == null) return;
        if (block.equals(this.currentBlock)) return;

        this.currentBlock = block;
        var meta = (BlockDisplayMeta) blockEntity.getEntityMeta();
        meta.setBlockState(block);
    }

    public void setHighlighted(boolean highlighted) {
        blockEntity.setGlowing(highlighted);
        textEntity.setGlowing(highlighted);
    }

    public boolean isSpawned() {
        return blockEntity.getInstance() != null;
    }
}
