package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private static final Font DEFAULT_FONT = new Font("Monaco", Font.BOLD, 30);
    private static final Font INFO_BAR_FONT = new Font("Monaco", Font.BOLD, 16);
    private static final long GAME_START_TIME = System.currentTimeMillis();
    private final int width;
    private final int height;
    private int round;
    private final Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
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
        StdDraw.setFont(DEFAULT_FONT);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        // Initialize random number generator
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        // Generate random string of letters of length n
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            int index = RandomUtils.uniform(rand, 0, CHARACTERS.length);
            sb.append(CHARACTERS[index]);
        }
        return sb.toString();
    }

    public void drawFrame(String s) {
        // Take the string and display it in the center of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.text(width >> 1, height >> 1, s);

        // If game is not over, display relevant game information at the top of the screen
        if (!gameOver) {
            drawInfoBar();
        }
        StdDraw.show();
    }

    private void drawInfoBar() {
        // display relevant game information at the top of the screen
        double lineHeight = 0.95 * height;
        double textCenterHeight = 0.975 * height;
        long timeElapsed = System.currentTimeMillis() - GAME_START_TIME;
        int index = ((int) (timeElapsed / 2000)) % ENCOURAGEMENT.length;
        String encouragement = ENCOURAGEMENT[index];
        StdDraw.line(0, lineHeight, width, lineHeight);
        StdDraw.setFont(INFO_BAR_FONT);
        StdDraw.text(3, textCenterHeight, "Round: " + round);
        StdDraw.text(width >> 1, textCenterHeight, gameState());
        StdDraw.text(width - 5, textCenterHeight, encouragement);
        StdDraw.setFont(DEFAULT_FONT);
    }

    private String gameState() {
        if (playerTurn) {
            return "Type!";
        } else {
            return "Watch!";
        }
    }

    public void flashSequence(String letters) {
        // Display each character in letters, making sure
        // to blank the screen between letters
        for (int i = 0; i < letters.length(); ++i) {
            drawFrame(letters.substring(i, i + 1));
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        // Read n letters of player input
        StringBuilder sb = new StringBuilder();
        while (sb.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char entered = StdDraw.nextKeyTyped();
                if (entered == '0') {
                    sb.deleteCharAt(sb.length() - 1);
                } else if (Character.isLetter(entered)){
                    sb.append(entered);
                }
            }
            drawFrame(sb.toString());
            StdDraw.pause(16);
        }
        return sb.toString();
    }

    public void startGame() {
        // Set any relevant variables before the game starts
        gameOver = false;
        round = 0;
        playerTurn = false;
        String generated = "";
        // Establish Engine loop
        while (!gameOver) {
            if (playerTurn) {
                String userInput = solicitNCharsInput(round);
                if (!userInput.equals(generated)) {
                    gameOver = true;
                }
                playerTurn = false;
            } else {
                ++round;
                generated = generateRandomString(round);
                flashSequence(generated);
                playerTurn = true;
            }
        }
        drawGameOver(round);
    }

    private void drawGameOver(int round) {
        drawFrame("GAME OVER. YOU MADE IT TO ROUND " + round);
    }
}
