package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;
    public static final Random RANDOM = new Random();
    public static List<Room> roomList = new ArrayList<>();


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {

    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        return finalWorldFrame;
    }
    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.TREE;
            }
        }
    }
    /**
     * Check if this room overlap with all existing rooms
     * @param room
     * @return true if it overlaps
     */
    public static boolean roomOverlapAll(Room room) {
        if (roomList.isEmpty()) return false;
        for (Room i : roomList) {
            if(room.roomOverlap(i)) return true;
        }
        return false;
    }

    /**
     * draw total number of roomTotal rooms
     * @param roomTotal
     * @return
     */
    public static void drawAllRoom(int roomTotal, TETile[][] tiles) {
        int i = 0;
        int th = 0;
        while (i < roomTotal) {
            int width = RANDOM.nextInt(WIDTH / 8) + 4;
            int height = RANDOM.nextInt(HEIGHT / 8) + 4;
            int x = RANDOM.nextInt(WIDTH - width - 2) + 1;
            int y = RANDOM.nextInt( HEIGHT - height) + 1;
            Position p = new Position(x, y);
            Room room = new Room(width, height, p);
            th++;
            if (!roomOverlapAll(room)) {
                i++;
                room.drawOneRoom(tiles, p);
                roomList.add(room);
            }
        }
        System.out.println(th);

    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        fillWithNothing(finalWorldFrame);
        //Position anchor = new Position(0, 0);
        //Room room = new Room(5, 5, anchor);
        //room.drawWall(finalWorldFrame,anchor);
        //room.drawOneRoom(finalWorldFrame,anchor, 10, 10);
        drawAllRoom(40, finalWorldFrame);
        ter.renderFrame(finalWorldFrame);
    }
}
