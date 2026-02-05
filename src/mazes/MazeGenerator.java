package mazes;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

public class MazeGenerator {
    private final Maze maze;
    Pos startPos = new Pos(0, 42, 0);

    public MazeGenerator() {
        this.maze = new Maze(20, 20, startPos);
    }

    public void generateMaze(InstanceContainer instance, Pos pos) {
        var widthMax = maze.getWidth();
        var heightMax = maze.getHeight();
        var currentY = pos.blockY();
        for (int x = 0; x < widthMax; x++) {
            for (int y = 0; y < heightMax; y++) {
                if(maze.getCellVisible(x, y))
                    instance.setBlock(pos.blockX() + x, currentY, pos.blockZ() + y, Block.GRAVEL);
            }
        }

    }

}
