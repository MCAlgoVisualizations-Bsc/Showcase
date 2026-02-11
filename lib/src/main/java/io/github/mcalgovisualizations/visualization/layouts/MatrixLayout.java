package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.models.DataModel;
import io.github.mcalgovisualizations.visualization.render.DisplayValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A {@link Layout} implementation that places elements randomly within
 * a bounded 2D grid (matrix).
 * <p>
 * The layout defines a fixed number of rows and columns. Each element
 * is assigned to a unique grid cell chosen from a shuffled list of all
 * available cells. The shuffle is deterministic based on the provided seed.
 * <p>
 * The grid is centered around the provided origin.
 */
public final class MatrixLayout implements Layout {

    private final double yOffset;

    // Grid dimensions (cells)
    private final int cols;
    private final int rows;

    // How many blocks between cells (>= 1).
    private final double spacing = 2;

    private final Random random;

    /**
     * Creates a bounded random matrix layout.
     *
     * @param yOffset vertical offset added to {@code origin.y()}
     * @param cols    number of columns in the grid (must be > 0)
     * @param rows    number of rows in the grid (must be > 0)
     * @param seed    seed used for deterministic shuffling of grid cells
     *
     * @throws IllegalArgumentException if {@code cols <= 0} or {@code rows <= 0}
     */
    public MatrixLayout(double yOffset, int cols, int rows, long seed) {
        if (cols <= 0 || rows <= 0)
            throw new IllegalArgumentException("cols/rows must be > 0");

        this.yOffset = yOffset;
        this.cols = cols;
        this.rows = rows;
        this.random = new Random(seed);
    }

    /**
     * Computes a randomized grid layout for the given {@link DataModel}.
     * <p>
     * Elements are placed in randomly selected, non-overlapping grid cells.
     * The grid is centered around {@code origin}, and each cell is spaced
     * evenly using a fixed spacing value.
     *
     * @param model  the data model defining how many elements to render
     * @param origin the center position of the grid layout
     * @return an array of {@link DisplayValue} objects positioned within the grid
     *
     * @throws IllegalArgumentException if the number of elements exceeds grid capacity
     */
    public DisplayValue[] computeRandom(DataModel model, Pos origin) {
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


    // TODO : move computeRandom into compute :)
    @Override
    public DisplayValue[] compute(DataModel model, Pos origin) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public DisplayValue[] random(DataModel model, Pos origin) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Represents a single grid cell defined by column and row indices.
     *
     * @param col zero-based column index
     * @param row zero-based row index
     */
    private record Cell(int col, int row) { }


}
