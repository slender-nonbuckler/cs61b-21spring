package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {
    int width, height;

    //startP is the leftbottom corner point
    Position startP;

    public Room(int width, int height, Position p) {
        this.width = width;
        this.height = height;
        this.startP = p;
    }

    /**
     * Draw a row of tiles to the board, anchored at a given postion
     * with the two ends as wall tiles
     */
    public void drawRow(TETile[][] tiles, Position p) {
        for (int dx = 0; dx < this.width; dx++) {
            // draw wall on each end;
            if (dx == 0 || dx == this.width - 1) {
                tiles[p.x + dx][p.y] = Tileset.WALL;
            } else {
                tiles[p.x + dx][p.y] = Tileset.FLOOR;
            }
        }
    }

    /**
     * Draw a row of wall tiles to the board, anchored at a given postion
     */
    public void drawWall(TETile[][] tiles, Position p) {
        for (int dx = 0; dx < this.width; dx++) {
            // draw wall on each end;
            tiles[p.x + dx][p.y] = Tileset.WALL;
        }
    }

    /**
     * Draw one room anchored at a given postion
     * with wall elements surronding it.
     * at position p is a wall
     */
    public void drawOneRoom(TETile[][] tiles, Position p) {
        drawWall(tiles, p);
        for (int dy = 1; dy < height - 1; dy++) {
            // draw wall on each end;
            drawRow(tiles, p.shift(0, dy));
        }
        drawWall(tiles, p.shift(0, height - 1));
    }

    /**
     * Determine if a rectangle room overlap with another.
     * only side or corner does not count.
     * return true if overlap
     */
    public boolean roomOverlap(Room other) {
        Position p1 = this.startP;
        Position p2 = p1.shift(this.width, this.height);
        Position p3 = other.startP;
        Position p4 = p3.shift(other.width, other.height);
        if (p2.larger(p3) && p4.larger(p1)) return true;
            //p2.x >  p3.x && p2.y > p3.y && p4.x > p1.x && p4.y > p1.y
        else {
            return false;
        }
    }

    /**
     * get the center position of a rectangle room
     */

    public Position getCenter() {
        return this.startP.shift(this.width / 2, this.height / 2);
    }
}

    /**public static void main(String[] args) {
        Position p1 = new Position(13, 4);
        Position p2 = new Position(17, 3);
        Room room1 = new Room(9, 9, p1);
        Room room2 = new Room(4, 6, p2);
        System.out.println(room1.roomOverlap(room2));
    }
*/

