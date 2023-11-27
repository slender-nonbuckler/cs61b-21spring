package byow.Core;

import java.io.Serializable;

public class Position implements Serializable {
    int x, y;
    public Position (int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position shift(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    /**
     * compare if self position is at top right of p1
     * return true if self larger than p1
     */
    public boolean larger (Position p1) {
        if (this.x > p1.x && this.y > p1.y) {
            return true;
        }
        return false;
    }

    /**
     * return the distance to origin
     * x2 + y2
     */

    public int toOrigin() {
        return this.x * this.x + this.y * this.y;
    }
}
