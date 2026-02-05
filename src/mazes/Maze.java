package mazes;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.Direction;

public class Maze {

    private Cell [][] cells;
    private final int width, heigh;

    public Maze(int w, int l, Pos pos) {
        width = w;
        heigh = l;
        this.cells = new Cell[w][l];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < l; y++) {
                cells[x][y] = new Cell();
            }
        }
    }

    public int getWidth() { return width; }

    public int getHeight() { return heigh; }

    public boolean getCellDirection(int x, int y, Direction direction) {
        return switch (direction.name()) {
            case "EAST" -> this.cells[x][y].east;
            case "SOUTH" -> this.cells[x][y].south;
            case "WEST" -> this.cells[x][y].west;
            case "NORTH" -> this.cells[x][y].north;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    public void setCellDirection(int x, int y, Direction direction, boolean value) {
        switch (direction.name()) {
            case "NORTH": this.cells[x][y].north = value; return;
            case "EAST": this.cells[x][y].east = value; return;
            case "SOUTH": this.cells[x][y].south = value; return;
            case "WEST": this.cells[x][y].west = value; return;
            default: throw new IndexOutOfBoundsException();
        }
    }

    public void setCellVisible(int x, int y, boolean value) {
        this.cells[x][y].display = value;
    }

    public boolean getCellVisible(int x, int y) {
        return this.cells[x][y].display;
    }

    static class Cell {
        boolean east, west, north, south, display;
        Cell() {
            east = west = north = south = false;
            display = true;
        }
    }
}
