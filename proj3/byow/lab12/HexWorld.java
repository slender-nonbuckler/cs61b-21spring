package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class   HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * picks a random tile
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.SAND;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }
    // Private helper class to deal with positions
    private static class Position {
        int x;
        int y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public Position shift (int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }


    /**
     * Draw a row of tiles to the board, anchored at a given postion
     */
    public static void drawRow (TETile[][] tiles, Position p, TETile tile, int length) {
        for (int dx = 0; dx < length; dx++) {
            tiles[p.x + dx][p.y] = tile;
        }
    }

    /**
     *
     Helper method for addHexgon
     */
    public static void drawhelper (Position p,  int b, int t, TETile[][] tiles, TETile tile) {
        //Draw the first row.
        drawRow(tiles, p.shift(b, 0), tile, t);
        //Draw the remaining rows recursively
        if (b > 0) {
            drawhelper(p.shift(0, -1), b - 1, t + 2, tiles, tile);
        }
        //Draw this row again to be the reflecting.
        drawRow(tiles, p.shift(b, -2*b - 1), tile, t);
    }
    /**
     * Draw Hexgon
     */
    public static void drawHexgon (Position p, int n, TETile[][] tiles, TETile tile) {
        if (n < 2) return;
        drawhelper(p, n - 1, n, tiles, tile);
    }
    /**
     * Add one column of Hexagon, each of whose pattern chosen randomly
     */
    public static void drawCol (Position p, int n, TETile[][] tiles, int row) {
        int round = 0;
        while (row > 0) {
            drawHexgon(p.shift(0, - 2 * n * round - 1), n, tiles, randomTile());
            round += 1;
            row -= 1;
        }

    }
    /**
     * Draws the hexagonal world
     */
    public static void drawWorld(Position p, TETile[][] tiles, int hexSize, int tessSize) {
        for (int i = 0; i < 2 * tessSize - 1 ; i++) {
            if (i < tessSize){
                drawCol(p.shift(i * (hexSize * 2 - 1), i * hexSize), hexSize, tiles, tessSize + i);
            }
            else {
                drawCol(p.shift(i * (hexSize * 2 - 1), (2 * tessSize -2 - i) * hexSize), hexSize,
                        tiles, 3 * tessSize - 2 - i);
            }
        }
    }
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);
        Position anchor = new Position(0, 0);
        drawRow(world, anchor, randomTile(), 5);
        //drawWorld(anchor, world, 3, 4);
        ter.renderFrame(world);
    }
}
