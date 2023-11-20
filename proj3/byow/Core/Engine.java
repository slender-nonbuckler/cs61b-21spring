package byow.Core;
import byow.InputDemo.*;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;
    public static Random RANDOM;
    public static List<Room> roomList = new ArrayList<>();
    private boolean GameState;
    private static long SEED;
    private static int count; //room number range 20-35;
    public static TETile[][] finalWorldFrame;

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
        StringInputDevice inputDevice = new StringInputDevice(input);
        while (inputDevice.possibleNextInput()) {
            char key = inputDevice.getNextKey();
            processkey(key);
        }
        return finalWorldFrame;
    }
    /**
     * process the input of N***S
     * *** is the SEED number
     */
    private void processkey(char key) {
        if (key == 'N') GameState = true;
        else if (key == 'S') {
            GameState = false;
            initializeGame();
        }
        else {
            SEED = SEED * 10 + Character.getNumericValue(key);
        }
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
                tiles[x][y] = Tileset.NOTHING;
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
    }

    /**
     * make hallway to connect rooms together.
     * Pick two rooms from roomslist (sort on their distance to origin）
     * from room origin to a random point to the nearby room.
     */
    public static void connectRoom(TETile[][] tiles) {
        Collections.sort(roomList, (o1, o2) -> (o1.startP.toOrigin() - o1.startP.toOrigin()));
        Room first = roomList.get(0);
        Position start, end;

        for (Room room : roomList) {
            start = first.getCenter();
            Room next = room;
            int X2 = RANDOM.nextInt(next.width - 2) + next.startP.x + 1;
            int Y2 = RANDOM.nextInt(next.height - 2) + next.startP.y + 1;
            end = new Position(X2, Y2);
            makeHallway(tiles, start, end);
            first = next;
        }
    }

    /**
     * make hallway between two points
     * one source and one destination point.
     * if source.x > destination.x, stepx = -1 otherwise +1; same for y;
     * use a random boolean, if true do vertical hallway first
     * if false do horizontal hallway first.
     */
    public static void makeHallway(TETile[][] tiles, Position start, Position end) {
        boolean moveHoriFirst = RANDOM.nextBoolean();//if true, horizontal path first
        if (moveHoriFirst) {
            hallwayXDir(tiles, start, end, start.y);
            hallwayYDir(tiles, start, end, end.x);
            hallwayCorner(tiles, end.x, start.y);
        }
        else {
            hallwayYDir(tiles, start, end, start.x);
            hallwayXDir(tiles, start, end, end.y);
            hallwayCorner(tiles, start.x, end.y);
        }
    }
    /**
     * draw horizontal piece, one floor tile sandwiched by two walls
     */
    public static void hallwayXDirPiece(TETile[][] tiles, int x, int y) {
        // if cur tile is floor, no need for hallwayPiece;
        int[] nearby = new int[]{1, -1};
        int count = 0;
        if (tiles[x][y] != Tileset.FLOOR) {
            tiles[x][y] = Tileset.FLOOR;
            for (int i : nearby) {
                if (tiles[x][y + i] != Tileset.FLOOR) {
                    tiles[x][y + i] = Tileset.WALL;
                }
            }
        }
    }
    /**
     * draw hallway horizontally
     * here if draw horizontal hallway first then vertical one,
     * y shall be same as start.y
     * otherwise, equal to end.y
     */
    public static void hallwayXDir(TETile[][] tiles, Position start, Position end, int y) {
        int stepX = start.x >= end.x ? -1 : 1;
        int curX = start.x;
        while (curX != end.x) {
            curX += stepX;
            hallwayXDirPiece(tiles,curX, y);
        }
    }
    /**
     * draw vertical piece, one floor tile sandwiched by two walls
     */
    public static void hallwayYDirPiece(TETile[][] tiles, int x, int y) {
        // if cur tile is floor, no need for hallwayPiece;
        int[] nearby = new int[] {1, -1};
        int count = 0;
        if (tiles[x][y] != Tileset.FLOOR) {
            tiles[x][y] = Tileset.FLOOR;
            for (int i : nearby) {
                if (tiles[x + i][y] != Tileset.FLOOR) {
                    tiles[x + i][y] = Tileset.WALL;
                }
            }
        }
    }
    /**
     * draw hallway vertically
     * here if draw vertical hallway first then horizontal one,
     * x shall be same as start.x
     * otherwise, equal to end.x
     */
    public static void hallwayYDir(TETile[][] tiles, Position start, Position end, int x) {
        int stepY = start.y >= end.y ? -1 : 1;
        int curY = start.y;
        while (curY != end.y) {
            curY += stepY;
            hallwayYDirPiece(tiles,x, curY);
        }
    }
    /**
     * draw hallway corner
     * if before horizontal first, input x = end.x, input y = start.y
     * else input x = start.x, input y = end.y
     */
    public static void hallwayCorner(TETile[][] tiles, int x, int y ) {
        int[] xc = new int[] {1, 1, 1, -1, -1, -1, 0, 0};
        int[] yc = new int[]{1, -1, 0, 1, -1, 0, 1, -1};
        tiles[x][y] = Tileset.FLOOR;
        for (int i = 0; i < xc.length; i++) {
            if (tiles[x + xc[i]][y + yc[i]] != Tileset.FLOOR) {
                tiles[x + xc[i]][y + yc[i]] = Tileset.WALL;
            }
        }
    }

    /**
     * iterate through the room supposed surrounding walls,
     * if it is wall tile, it will be the door location.
     */
    public void findDoor(TETile[][] tiles) {
        int roomNo = RANDOM.nextInt(roomList.size() - 1);
        Room room = roomList.get(roomNo);
        int minx = room.startP.x;
        int miny = room.startP.y;
        int maxx = room.startP.x + room.width - 1;
        int maxy = room.startP.y + room.height - 1;
        int x = 0;
        int y = 0;
        int dir = RANDOM.nextInt(3);
        switch (dir) {
            case 0:
                x = minx;
                y = miny + 1;
                while (tiles[x][y] != Tileset.WALL) {
                    y += 1; // not consider y outside of rectangle,
                    // because there will be wall element at this line
                }
                break;
            case 1:
                x = minx + 1;
                y = maxy;
                while (tiles[x][y] != Tileset.WALL) {
                    x += 1;
                }
                break;
            case 2:
                x = maxx;
                y = maxy - 1;
                while (tiles[x][y] != Tileset.WALL) {
                    y -= 1;
                }
                break;
            case 3:
                x = maxx - 1;
                y = miny;
                while (tiles[x][y] != Tileset.WALL) {
                    x -= 1;
                }
                break;
        }

        tiles[x][y] = Tileset.LOCKED_DOOR;
    }

    /**
     * add HUD at the top of the board.
     * 1. Show the tile that currently under the mouse pointer.
     * 2. add the relal time and date
     */
    public void addHDU() {
        if (GameState && StdDraw.isMousePressed()) {
            Position mouse = new Position((int)StdDraw.mouseX(), (int)StdDraw.mouseY());
            TETile tile =  finalWorldFrame[mouse.x][mouse.y];
            String text = tile.description();
            StdDraw.textLeft(1, HEIGHT - 1, text);//show tile info on top left
        }
        // Add real-time and date on the right top corner
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateTimeString = now.format(formatter);
        StdDraw.textRight(WIDTH - 1, HEIGHT - 1, dateTimeString);
    }
    public void initializeGame() {
        RANDOM = new Random(SEED);
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        finalWorldFrame = new TETile[WIDTH][HEIGHT];
        fillWithNothing(finalWorldFrame);
        //Position anchor = new Position(0, 0);
        //Room room = new Room(5, 5, anchor);
        //room.drawWall(finalWorldFrame,anchor);
        //room.drawOneRoom(finalWorldFrame,anchor, 10, 10);
        count = RANDOM.nextInt(15) + 20;
        drawAllRoom(count, finalWorldFrame);
        connectRoom(finalWorldFrame);
        findDoor(finalWorldFrame);
        addHDU();
        ter.renderFrame(finalWorldFrame);
    }
}
