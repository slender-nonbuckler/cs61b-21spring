package byow.Core;

public class Position {
    int x, y;
    Position (int x, int y) {
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
        if (this.x > p1.x && this.y > p1.x) {
            return true;
        }
        return false;
    }
}
