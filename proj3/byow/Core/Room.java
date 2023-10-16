package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;

public class Room {
    int width, height;

    //startP is the leftbottom corner point
    Position startP;
    public Room (int width, int height, Position p) {
        this.width = width;
        this.height = height;
        this.startP = p;
    }
    /**
     * Draw a row of tiles to the board, anchored at a given postion
     * with the two ends as wall tiles
     */
    public void drawRow (TETile[][] tiles, Position p, int width){
        for (int dx = 0; dx < width; dx++) {
            // draw wall on each end;
            if (dx == 0 || dx == width - 1) {
                tiles[p.x + dx][p.y] = Tileset.WALL;
            }
            else {
                tiles[p.x + dx][p.y] = Tileset.FLOOR;
            }
        }
    }
    /**
     * Draw a row of wall tiles to the board, anchored at a given postion
     */
    public void drawWall (TETile[][] tiles, Position p, int width) {
        for (int dx = 0; dx < width; dx++) {
            // draw wall on each end;
            tiles[p.x + dx][p.y] = Tileset.WALL;
        }
    }
    /**
     * Draw one room anchored at a given postion
     * with wall elements surronding it.
     */
    public void drawOneRoom (TETile[][] tiles, Position p, int width, int height) {
        drawWall(tiles, p, width);
        for (int dy = 1; dy < height - 1; dy++) {
            // draw wall on each end;
            drawRow(tiles, p.shift(0, dy), width);
        }
        drawWall(tiles, p.shift(0, height - 1), width);
    }
    /**
     * Determine if a rectangle room overlap with another.
     * only side or corner does not count.
     * return true if overlap
     */
    public boolean roomOverlap (Room other) {
        Position p1 = this.startP;
        Position p2 = p1.shift(this.width, this.height);
        Position p3 = other.startP;
        Position p4 = p3.shift(other.width, other.height);
        if (p2.larger(p3) && p4.larger(p1)) return true;
        return false;
    }
}
