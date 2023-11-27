package byow.Core;
import byow.Input.*;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;


import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import static byow.Core.GameState.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;
    public static Random RANDOM;
    public static List<Room> roomList = new ArrayList<>();
    private GameState gameState = Menu;
    private Player player;
    private boolean Gameactive = true;
    private static long SEED;
    private final long START_TIME = 1 * 10 * 1000; // 1 minutes(limit) in milliseconds
    private long startTime;
    private static int count; //room number range 20-35;
    public static TETile[][] finalWorldFrame;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException {
        DisplayMenu();
        KeyBoardInputSource keyboardInput = new KeyBoardInputSource();
        while (Gameactive) {
            if (keyboardInput.possibleNextInput()) {
                char key = keyboardInput.getNextKey();
                processkey(key);
            }
            addHDU();
        }
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
    public TETile[][] interactWithInputString(String input) throws IOException {
        // it runs the engine using the input
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
     * You have to option to use cheat code G to turn on godview
     * otherwise you will have limited view
     * The way to win is walk to the door
     * The way to lose is not find the door within 2mins
     */
    private void processkey(char key) throws IOException {
        switch (gameState) {
            case Menu:
                switch (key) {
                    case 'N':
                        gameState = Select_Seed;
                        displaySeedMenu();
                        break;
                    case 'L':
                        load_Game();
                        gameState = Play;
                        break;
                    case 'Q':
                        Gameactive = false;
                        gameState = Saved;
                        System.exit(0);
                        break;
                }
                break;
            case Select_Seed:
                switch(key) {
                    case 'S':
                        gameState = Play;
                        initializeGame();
                        break;
                    default:
                        SEED = SEED * 10 + Character.getNumericValue(key);
                        displaySeedMenu();
                }
                break;
            case Play:
                switch (key) {
                    case(':') :
                        gameState = to_Quit;
                        break;
                    case('G') :
                        gameState = Godview;
                        break;
                    default:
                        moveRoundLimited(key);
                }
                break;
            case Godview:
                switch (key) {
                    case(':') :
                        gameState = to_Quit;
                        break;
                    case('G') :
                        gameState = Play;
                        break;
                    default:
                        moveRound(key);
                }
                break;
            case to_Quit:
                if (key == 'Q') {
                    saveGame();
                    Gameactive = false;
                    gameState = Saved;
                    System.exit(0);
                }
                else {
                    gameState = Play;
                }
                break;
            case Win, Lose:
                //endingdisplay & Gameactive = false & exit(0) in moveRound method for win
                //endingdisplay & Gameactive = false & exit(0) in timeRemaing method for lose
                //Actually, this case is not needed. 
                break;
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
            int y = RANDOM.nextInt( HEIGHT - height - 2) + 1;
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

        tiles[x][y] = Tileset.UNLOCKED_DOOR;
    }
    /**
     * find a random floor location
     * (to place the avatar in the beginning)
     * this uses a different method than locating the door.
     * keep picking a random cell in the board until find a floor cell.
     * cons: might take a long time especially, floor area is small comparing with
     * the whole board
     */
    public Position findFloor() {
        //see drawAllRoom method for the reason of those integers below
        int x = RandomUtils.uniform(RANDOM, 1, WIDTH - 6);
        int y = RandomUtils.uniform(RANDOM, 1, HEIGHT - 4);
        while(!finalWorldFrame[x][y].equals(Tileset.FLOOR)) {
            x = RandomUtils.uniform(RANDOM, 1, WIDTH - 6);
            y = RandomUtils.uniform(RANDOM, 1, HEIGHT - 4);
        }
        return new Position(x, y);
    }

    /**
     * add Avatar to the board;
     */
    public void startAvatar() {
        player = new Player(findFloor());
        finalWorldFrame[player.location.x][player.location.y] = player.symbol;
    }
    /**
     * move Avatar in Godview per the input direction
     * in the future, if there are enemy or coins,
     * this method will deal all items turn here.
     */
    public void moveRound(Character dir) {
        player.move(dir, finalWorldFrame);
        if (finalWorldFrame[player.location.x][player.location.y].equals(Tileset.UNLOCKED_DOOR)) {
            gameState = Win;
            displayEnding("You Win!");
            Gameactive = false;
            System.exit(0);
        }
        ter.renderFrame(finalWorldFrame);

    }
    /**
     * move Avatar in non-Godview
     */
    public void moveRoundLimited(Character dir) {
        player.move(dir, finalWorldFrame);
        if (finalWorldFrame[player.location.x][player.location.y].equals(Tileset.UNLOCKED_DOOR)) {
            gameState = Win;
        }
        ter.renderFramelimited(finalWorldFrame, viewRange());
    }
    /**
     * helper method for determine the 7x7 view square around the avatar
     */
    public HashSet<int[]> viewRange() {
        int range_x = 3;
        int range_y = 3;
        Position temp = player.location;

        HashSet<int[]> range = new HashSet<>();
        for (int x = -range_x; x <= range_x; x++) {
            for (int y = -range_y; y <= range_y; y++) {
                if (isValid(temp.shift(x, y))){
                    int[] locations = new int[2];
                    locations[0] = temp.x + x;
                    locations[1] = temp.y + y;
                    range.add(locations);
                }
            }
        }
        return range;
    }
    /**
     * add HUD at the top of the board.
     * 1. Show the tile that currently under the mouse pointer.
     * 2. add the real time and date on the right top corner
     */
    public void addHDU() {
        if (gameState.equals(Play)) {
            Position mouse = new Position((int)StdDraw.mouseX(), (int)StdDraw.mouseY());
            if (!isValid(mouse)) {
                return;
            }
            TETile tile =  finalWorldFrame[mouse.x][mouse.y];
            String text = tile.description();
            ter.renderFramelimited(finalWorldFrame, viewRange(), text, remainingTime());
        }
        if (gameState.equals(Godview)) {
            Position mouse = new Position((int)StdDraw.mouseX(), (int)StdDraw.mouseY());
            if (!isValid(mouse)) {
                return;
            }
            TETile tile =  finalWorldFrame[mouse.x][mouse.y];
            String text = tile.description();
            ter.renderFrame(finalWorldFrame, text, remainingTime());
        }
    }
    /**
     * this calculates the remaining Time to play,
     * it there is remaining time, return remaining minutes and seconds
     * if no remaining time, gameState change to Lose(i.e. Game over)
     */
    public long[] remainingTime() {
        long[] time = new long[]{0, 0};
        if (hasTime()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = START_TIME - elapsedTime;

            // Convert remaining time to minutes and seconds
            long remainingMinutes = (remainingTime / 1000) / 60;
            long remainingSeconds = (remainingTime / 1000) % 60;
            time[0] = remainingMinutes;
            time[1] = remainingSeconds;
        }
        else {
            gameState = Lose;
            displayEnding("Gameover! Try again");
            Gameactive = false;
            System.exit(0);
        }
        return time;
    }
    public boolean hasTime() {
        return (System.currentTimeMillis() - startTime < START_TIME);
    }


    /**
     * a helper method for addHDU
     * to determine if mouse position is valid
     * i.e. if it is within board limit
     */
    public boolean isValid(Position p) {
        return p.x < WIDTH && p.x >= 0 && p.y < HEIGHT && p.y >= 0;
    }
    /**
     * Display Menu for keyboard input
     */
    public void DisplayMenu() {
        //Set up the canvas
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        //draw the title
        StdDraw.text(WIDTH / 2, HEIGHT * 4 / 5, "CS61B: THE GAME");
        // draw options
        Font smallFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(smallFont);
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.40, "New Game (N)");
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.35, "Load Game (L)");
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.30, "Quit (Q)");
        StdDraw.show();
        //StdDraw.pause(10000);
    }

    /**
     * Display seed Menu, it will display the seed number inserted
     */
    public void displaySeedMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Enter A Random Seed Number");
        StdDraw.text(WIDTH / 2, HEIGHT * 2 / 5, Long.toString(SEED));
        StdDraw.text(WIDTH / 2, HEIGHT / 3, "When you ready to start , press S");
        StdDraw.show();
        StdDraw.pause(100);
    }
    /**
     * Display endGame message, either win or lose the game
     */
    public void displayEnding(String message) {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, message);
        StdDraw.show();
        StdDraw.pause(2000);
    }
    /**
     * Save Game
     */
    public void saveGame() throws IOException {
        File f = new File("./save");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(finalWorldFrame);
            os.writeObject(ter);
            os.writeObject(SEED);
            os.writeObject(player);
            os.writeObject(roomList);

        } catch(FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(0);
        }
        catch(IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    /**
     * Load game
     */
    public void load_Game() throws IOException {
        File f = new File("./save");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                finalWorldFrame = (TETile[][]) os.readObject();
                ter = (TERenderer) os.readObject();
                player = (Player)  os.readObject();
                SEED = (long) os.readObject();
                //roomList = (ArrayList<Room>) os.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
    public void initializeGame() {
        RANDOM = new Random(SEED);
        ter.initialize(WIDTH, HEIGHT);
        finalWorldFrame = new TETile[WIDTH][HEIGHT];
        fillWithNothing(finalWorldFrame);
        count = RANDOM.nextInt(15) + 20;
        drawAllRoom(count, finalWorldFrame);
        connectRoom(finalWorldFrame);
        findDoor(finalWorldFrame);
        startAvatar();
        startTime = System.currentTimeMillis();
        ter.renderFrame(finalWorldFrame);
    }

}
