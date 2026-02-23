package io.github.mcalgovisualizations.visualization.renderer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class BlockDisplay implements DisplayValue {
    public final Instance instance;
    public final Entity blockEntity;
    public final Entity textEntity;

    private static final Vec TEXT_OFFSET = new Vec(1, 2.4, 1);

    private Pos pos;
    private Block currentBlock;

    public BlockDisplay(Instance instance, Pos pos, Block block, String text) {
        if (block == null) throw new NullPointerException("block cannot be null");
        if (instance == null) throw new NullPointerException("instance cannot be null");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("text cannot be blank");

        this.instance = instance;

        this.blockEntity = new Entity(EntityType.BLOCK_DISPLAY);
        this.textEntity = new Entity(EntityType.TEXT_DISPLAY);

        this.pos = pos;
        this.currentBlock = block;


        setupBlock(block);
        setupText(text);
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

    public void setInstance() {
        blockEntity.setInstance(instance);
        textEntity.setInstance(instance);
    }

    @Override
    public void addViewer(Player player) {
        final var _ = this.blockEntity.addViewer(player);
        final var _ = this.textEntity.addViewer(player);
    }

    public void remove() {
        this.blockEntity.remove();
        this.textEntity.remove();
    }

    public void teleport(Pos pos) {
        this.pos = pos;
        final var _ = this.blockEntity.teleport(pos);
        final var offset = pos.add(TEXT_OFFSET);
        final var _ = textEntity.teleport(offset);
    }

    public void setValue(int value) {
        final var meta = (TextDisplayMeta) textEntity.getEntityMeta();
        meta.setText(Component.text(Integer.toString(value), NamedTextColor.GOLD));
    }

    public void setGlowing(boolean highlighted) {
        blockEntity.setGlowing(highlighted);
        textEntity.setGlowing(highlighted);
    }

    public boolean isSpawned() {
        return blockEntity.isActive() || textEntity.isActive();
    }
}
