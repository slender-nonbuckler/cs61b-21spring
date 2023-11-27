package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

/**
 * Player is the character the user controls,
 * the initial location shall be a random floor location
 * the symbol will be avatar
 * the move method will move the avatar to the floor nearby(wall not allowed)
 */
public class Player implements Serializable {
    protected Position location;
    protected TETile symbol = Tileset.AVATAR;

    public Player(Position startPoint) {
        this.location = startPoint;
    }

    /**
     * move up for w;move down for s;
     * move left for a; move right for d;
     * The underlying requirements is the location to be moved to
     * shall be floor.
     * @param dir
     */
    public void move(Character dir, TETile[][] tiles) {
        Position prev = this.location;
        Position cur = this.location;
        switch (dir) {
            case ('W'):
                cur = cur.shift(0, 1);
                if (moveValid(cur, tiles)) {
                    this.location = cur;
                }
                break;
            case ('S'):
                cur = cur.shift(0, -1);
                if (moveValid(cur, tiles)) {
                    this.location = cur;
                }
                break;
            case ('A'):
                cur = cur.shift(-1, 0);
                if (moveValid(cur, tiles)) {
                    this.location = cur;
                }
                break;
            case ('D'):
                cur = cur.shift(1, 0);
                if (moveValid(cur, tiles)) {
                    this.location = cur;
                }
                break;
        }
        TETile curShape = tiles[this.location.x][this.location.y];
        if (!prev.equals(this.location) && !curShape.equals(Tileset.UNLOCKED_DOOR)) {
            tiles[prev.x][prev.y] = Tileset.FLOOR;
            tiles[cur.x][cur.y] = this.symbol;
        }
    }
    /**
     * check if the move is valid,
     * only valid for floor and door
     */
    public boolean moveValid(Position p, TETile[][] tiles) {
        return (tiles[p.x][p.y].equals(Tileset.FLOOR) || tiles[p.x][p.y].equals(Tileset.UNLOCKED_DOOR));

    }
}
