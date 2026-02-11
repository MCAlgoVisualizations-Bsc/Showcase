package io.github.mcalgovisualizations.visualization.utils;

import net.minestom.server.instance.block.Block;

public class Utils {

    /**
     * Get a colored wool block based on the value.
     * Higher values get "warmer" colors.
     */
    public static Block getBlockForValue(int value) {
        return switch (value % 10) {
            case 0 -> Block.RED_WOOL;
            case 1 -> Block.WHITE_WOOL;
            case 2 -> Block.LIGHT_GRAY_WOOL;
            case 3 -> Block.YELLOW_WOOL;
            case 4 -> Block.ORANGE_WOOL;
            case 5 -> Block.PINK_WOOL;
            case 6 -> Block.MAGENTA_WOOL;
            case 7 -> Block.PURPLE_WOOL;
            case 8 -> Block.BLUE_WOOL;
            case 9 -> Block.CYAN_WOOL;
            default -> Block.BLACK_WOOL;
        };
    }
}
