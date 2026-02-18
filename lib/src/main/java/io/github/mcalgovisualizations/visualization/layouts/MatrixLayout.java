package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.renderer.LayoutEntry;
import io.github.mcalgovisualizations.visualization.renderer.update.LayoutResult;
import net.minestom.server.coordinate.Pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Places elements randomly within a bounded 2D grid (matrix) centered on origin.
 * Produces positions only (no entities).
 */
public record MatrixLayout (
    double  yOffset,
    int     cols,
    int     rows,
    double  spacing,
    long    seed
) implements Layout {

    // setting the default configs here
    public MatrixLayout() {
        this(2.0,2,2, 24);
    }
    public MatrixLayout(double yOffset, int cols, int rows) {
        this(yOffset, cols, rows, 24);
    }
    public MatrixLayout(double yOffset, int cols, int rows, int seed) {
        this(yOffset, cols, rows, 2.0, seed);
    }

    /**
     * Deterministic layout for {@code size} nodes using this instance's RNG.
     * Throws if size exceeds capacity.
     */
    @Override
    public LayoutResult[] compute(int[] model, Pos origin) {
        return random(model, origin);
    }

    /**
     * Randomized (but deterministic per seed) grid positions.
     */
    @Override
    public LayoutResult[] random(int[] model, Pos origin) {
        final var size = model.length;
        final var random = new Random(seed);
        int capacity = cols * rows;
        if (size > capacity) {
            throw new IllegalArgumentException(
                    "Grid too small: size=" + size + " capacity=" + capacity +
                            " (cols=" + cols + ", rows=" + rows + ")"
            );
        }

        Pos[] positions = new Pos[size];

        double y = origin.y() + yOffset;

        double gridWidth = (cols - 1) * spacing;
        double gridHeight = (rows - 1) * spacing;

        double startX = origin.x() - gridWidth / 2.0;
        double startZ = origin.z() - gridHeight / 2.0;

        // All cells
        List<Cell> cells = new ArrayList<>(capacity);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells.add(new Cell(c, r));
            }
        }

        // Shuffle deterministically
        Collections.shuffle(cells, random);

        // First 'size' cells become node positions (node id == index)
        for (int id = 0; id < size; id++) {
            Cell cell = cells.get(id);
            double x = startX + cell.col * spacing;
            double z = startZ + cell.row * spacing;
            positions[id] = new Pos(x, y, z);
        }

        return new LayoutResult[0];
    }

    private record Cell(int col, int row) { }
}
