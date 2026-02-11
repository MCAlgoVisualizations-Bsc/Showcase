package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.models.Graph;
import io.github.mcalgovisualizations.visualization.render.DisplayValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class MatrixLayout implements Layout {

    private final double yOffset;

    // Grid dimensions (cells)
    private final int cols;
    private final int rows;

    // How many blocks between cells (>= 1).
    private final double spacing = 2;

    private final Random random;

    /**
     * Creates a bounded random "matrix" layout.
     *
     * @param yOffset  added to origin.y()
     * @param cols     number of columns in the grid
     * @param rows     number of rows in the grid
     * @param seed     seed for deterministic layouts (same graph size -> same placement)
     */
    public MatrixLayout(double yOffset, int cols, int rows, long seed) {
        if (cols <= 0 || rows <= 0) throw new IllegalArgumentException("cols/rows must be > 0");
        this.yOffset = yOffset;
        this.cols = cols;
        this.rows = rows;
        this.random = new Random(seed);
    }

    @Override
    public DisplayValue[] compute(DataModel model, Pos origin) {
        int n = model.size();
        int capacity = cols * rows;
        if (n > capacity) {
            throw new IllegalArgumentException(
                    "Grid too small for graph: n=" + n + " capacity=" + capacity +
                            " (cols=" + cols + ", rows=" + rows + ")"
            );
        }

        double y = origin.y() + yOffset;

        double gridWidth = (cols - 1) * spacing;
        double gridHeight = (rows - 1) * spacing;

        double startX = origin.x() - gridWidth / 2.0;
        double startZ = origin.z() - gridHeight / 2.0;

        // Create all candidate cell positions
        List<Cell> cells = new ArrayList<>(capacity);
        for (int rz = 0; rz < rows; rz++) {
            for (int cx = 0; cx < cols; cx++) {
                cells.add(new Cell(cx, rz));
            }
        }

        // Shuffle and assign first n cells to vertex ids
        Collections.shuffle(cells, random);

        var values = new DisplayValue[n];

        for (int id = 0; id < n; id++) {
            Cell c = cells.get(id);
            double x = startX + c.col * spacing;
            double z = startZ + c.row * spacing;
            final var pos = new Pos(x, y, z);
            values[id] = new DisplayValue(pos, Block.GRANITE, Integer.toString(id));
        }

        return values;
    }

    /**
     * Used to store col and row ids
     * @param col Column id
     * @param row Row id
     */
    private record Cell(int col, int row) { }
}
