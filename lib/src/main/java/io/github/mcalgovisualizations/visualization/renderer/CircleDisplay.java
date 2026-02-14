package io.github.mcalgovisualizations.visualization.renderer;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A ring of BlockDisplay "segments" arranged on the XZ plane around a center.
 * Implements DisplayValue so it can be managed like a single visual node.
 */
public final class CircleDisplay implements DisplayValue {

    private final List<BlockDisplay> segments = new ArrayList<>();
    private final Set<Long> occupiedBlockPositions = new HashSet<>(); // avoid duplicates from rounding

    private Pos center;
    private final int radius;
    private final int samples;
    private final double yOffset;

    private Instance instance;
    private boolean highlighted;
    private boolean spawned;

    /**
     * @param center  center position of the circle
     * @param radius  radius in blocks (>= 1)
     * @param samples number of sampled points around the circle (>= 4). Higher = smoother.
     * @param block   block to display for each segment
     */
    public CircleDisplay(Pos center, int radius, int samples, Block block) {
        this(center, radius, samples, block, 0.0);
    }

    /**
     * @param yOffset offset applied to center Y when placing the ring
     */
    public CircleDisplay(Pos center, int radius, int samples, Block block, double yOffset) {
        if (center == null) throw new NullPointerException("center cannot be null");
        if (block == null) throw new NullPointerException("block cannot be null");
        if (radius < 1) throw new IllegalArgumentException("radius must be >= 1");
        if (samples < 4) throw new IllegalArgumentException("samples must be >= 4");

        this.center = center;
        this.radius = radius;
        this.samples = samples;
        this.yOffset = yOffset;

        buildSegments(block);
        //applyPositions(); // positions before spawn is fine; setInstance may require a delayed nudge
    }

    private void buildSegments(Block block) {
        segments.clear();
        occupiedBlockPositions.clear();

        // Build segment list using rounded block positions, deduplicated
        for (int i = 0; i < samples; i++) {
            final double theta = (2.0 * Math.PI * i) / samples;

            final double x = center.x() + (radius * Math.cos(theta));
            final double z = center.z() + (radius * Math.sin(theta));
            final double y = center.y() + yOffset;

            final int bx = (int) Math.round(x);
            final int by = (int) Math.round(y);
            final int bz = (int) Math.round(z);

            final long key = pack(bx, by, bz);
            if (!occupiedBlockPositions.add(key)) continue; // skip duplicates

            // BlockDisplay requires non-blank text; use a bullet.
            segments.add(new BlockDisplay(new Pos(bx, by, bz), block, "•"));
        }
    }

    private void applyPositions() {
        // Recompute positions for each segment, preserving their count order
        // If you want exact same set after moving, rebuild+dedupe based on new center.
        // Here we keep the same angles -> same segment count.
        final int n = segments.size();
        for (int i = 0; i < n; i++) {
            final double theta = (2.0 * Math.PI * i) / n;

            final double x = center.x() + (radius * Math.cos(theta));
            final double z = center.z() + (radius * Math.sin(theta));
            final double y = center.y() + yOffset;

            final Pos p = new Pos(
                    Math.round(x),
                    Math.round(y),
                    Math.round(z)
            );

            segments.get(i).teleport(p);
            segments.get(i).setHighlighted(highlighted);
        }
    }

    @Override
    public void setInstance(Instance instance) {
        this.instance = instance;
        for (var s : segments) s.setInstance(instance);

        this.spawned = (instance != null);


    }

    @Override
    public void remove() {
        for (var s : segments) s.remove();
        segments.clear();
        occupiedBlockPositions.clear();
        spawned = false;
        instance = null;
    }

    /**
     * Teleport the entire circle by setting a new center.
     */
    @Override
    public void teleport(Pos pos) {
        if (pos == null) return;
        this.center = pos;

        // If you want strict dedupe after moves (because rounding changes), rebuild.
        // This keeps stable segment identities but can overlap for some radii.
        applyPositions();
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        for (var s : segments) s.setHighlighted(highlighted);
    }

    @Override
    public boolean isSpawned() {
        return spawned;
    }

    public Pos getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public int getSamples() {
        return samples;
    }

    // Packs a block position into a long for dedupe. (Rough, but fine for small coords)
    private static long pack(int x, int y, int z) {
        // 21 bits per axis (signed-ish range). Adjust if you need huge coords.
        final long xx = (x & 0x1FFFFF);
        final long yy = (y & 0x1FFFFF);
        final long zz = (z & 0x1FFFFF);
        return (xx << 42) | (yy << 21) | zz;
    }
}
