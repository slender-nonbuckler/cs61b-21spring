package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round = 0;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver ;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        this.rand = new Random(seed);
    }

    /**
     * Generate random string of letters of length n
     * @param n
     * @return
     */
    public String generateRandomString(int n) {
        StringBuilder temp = new StringBuilder();
        char ch;
        for (int i = 0; i < n; i++) {
            ch = CHARACTERS[rand.nextInt(26)];
            temp.append(ch);
        }

        return temp.toString();
    }

    /**Take the string and display it in the center of the screen
     * clear the canvas first
     * If game is not over, display relevant game information at the top of the screen
     * @param s
     */

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        //DRAW GUI
        if (!gameOver) {
            Font smallFont = new Font("Monaco", Font.BOLD, 20);
            StdDraw.textLeft(1, height - 1, "Round: " + round);
            StdDraw.text(width/2, height - 1, playerTurn ? "Type!" : "Watch!");
            StdDraw.textRight(width - 1, height - 1, ENCOURAGEMENT[round % ENCOURAGEMENT.length]);
            StdDraw.line(0, height - 2, width, height - 2);
        }
        //DRAW THE TEXT
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(this.width/2, this.height/2, s);
        StdDraw.show();
    }

    /**
     * make it flash
     * @param letters
     */
    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); i++) {
            if (i == letters.length() - 1) playerTurn = true;
            drawFrame(letters.substring(i, i + 1));
            StdDraw.pause(800);
            drawFrame(" ");
            StdDraw.pause(400);
        }
    }

    /**
     * Read n letters of player input
     * @param n
     * @return
     */
    public String solicitNCharsInput(int n) {
        StringBuilder temp = new StringBuilder();
        while (temp.length() < n) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            temp.append(StdDraw.nextKeyTyped());
            drawFrame(temp.toString());
        }
        return temp.toString();
    }

    public void startGame() {
        gameOver = false;
        playerTurn = false;
        while(!gameOver){
            playerTurn = false;
            round++;
            drawFrame("Round: " + round);
            StdDraw.pause(1000);
            String key = generateRandomString(round);
            flashSequence(key);
            playerTurn = true;
            String ans = solicitNCharsInput(round);
            if (!key.equals(ans)) {
                this.gameOver = true;
                drawFrame("Game Over! You made it to round:" + round);
            }
        }
    }
}

