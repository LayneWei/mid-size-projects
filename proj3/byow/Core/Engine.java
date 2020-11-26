package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Game engine.
 *
 * @author Robert Shi, Layne Wei
 * @e-mail robertyishi@berkeley.edu, lengning_wei@berkeley.edu
 */
public class Engine {
    private static final String AUTO_SAVE_FILE_NAME = "autosave.txt";
    private static final int MAX_FRAMES_PER_SECOND = 60;
    private static final int PAUSE_TIME_MILLISECONDS = 1000 / MAX_FRAMES_PER_SECOND;
    private static final int WORLD_WIDTH = 80;
    private static final int WORLD_HEIGHT = 40;
    private static final int HUD_HEIGHT = 3;

    World world;
    private final TERenderer ter;
    GameState state;
    private long seed;
    private StringBuilder seedEntered;
    private StringBuilder commands;
    private StringBuilder playerNameEntered;
    private String HUDPlayerName;
    private String HUDTileDescription;
    private String HUDWarningMessage;
    private WorldEvolver worldEvolver;

    public Engine() {
        ter = new TERenderer();
        state = GameState.MAIN_MENU;
        HUDPlayerName = "Player 1";
        HUDTileDescription = "";
        HUDWarningMessage = "";
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     *
     * Animation loop:
     * 1. clear the canvas
     * 2. draw elements
     * 3. show
     * 4. pause
     */
    public void interactWithKeyboard() {
        StdDraw.enableDoubleBuffering();
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT + HUD_HEIGHT, 0, HUD_HEIGHT);
        InputSource source = new KeyboardInputSource();
        while (state != GameState.TERMINATED) {
            renderFrame();
            StdDraw.pause(PAUSE_TIME_MILLISECONDS);
            Character inputChar = source.getNextKey();
            processInputChar(inputChar);
            processMouse();
            processBackgroundTasks();
        }
        System.exit(0);
    }

    /**
     * Draw a frame of the game. Content to be drawn depend on current
     * game state:
     * 1. MAIN_MENU: Title of the game and options to start, load, or quit.
     * 2. SEED_ENTERING: Prompt for the user to enter the seed, seed typed
     *                   in so far, and options to proceed or quit.
     * 3. GAME: Game world and HUD, including options to save and quit.
     * 4. GAME_COLON: same as GAME, except the quit prompt is highlighted.
     * 5. TERMINATED: throw exception as game should have been stopped.
     *
     */
    void renderFrame() {
        StdDraw.clear(Color.BLACK);
        switch (state) {
            case MAIN_MENU -> drawMainMenu();
            case SEED_ENTERING -> drawSeedEntering();
            case NAME_ENTERING -> drawNameEntering();
            case GAME, GAME_COLON, REPLAY_MODE -> drawGame();
            case TERMINATED -> throw new RuntimeException("renderFrame() "
                    + "when game should have been terminated.");
            default -> throw new RuntimeException("Unknown game state.");
        }
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and save. For example,
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
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        InputSource source = new StringInputDevice(input);
        while (source.possibleNextInput()) {
            processInputChar(source.getNextKey());
        }
        return world.tiles();
    }

    private void processInputChar(Character c) {
        if (c == null) {
            return;
        }
        switch (state) {
            case MAIN_MENU -> processMainMenuInputChar(c);
            case SEED_ENTERING -> processSeedEnteringInputChar(c);
            case NAME_ENTERING -> processNameEnteringInputChar(c);
            case GAME -> processGameInputChar(c);
            case GAME_COLON -> processGameColonInputChar(c);
            case REPLAY_MODE -> processReplayModeInputChar(c);
            case TERMINATED -> throw new RuntimeException("processInputChar() when "
                    + "game should have been terminated.");
            default -> throw new RuntimeException("unknown game state");
        }
    }

    private void processMainMenuInputChar(char c) {
        switch (Character.toUpperCase(c)) {
            case 'N' -> {
                seedEntered = new StringBuilder();
                state = GameState.SEED_ENTERING;
            }
            case 'L' -> loadFromAutoSave();
            case 'Q' -> state = GameState.TERMINATED;
            case 'C' -> {
                playerNameEntered = new StringBuilder(HUDPlayerName);
                state = GameState.NAME_ENTERING;
            }
            case 'V' -> {
                HUDWarningMessage = "--- Replaying Last Saved Game ---";
                startReplayingAutoSave();
            }
        }
    }

    private void processSeedEnteringInputChar(char c) {
        switch (Character.toUpperCase(c)) {
            case ControlCharacter.BACKSPACE -> removeBack(seedEntered);
            case 'S', ControlCharacter.LINE_FEED,
                    ControlCharacter.FORM_FEED,
                    ControlCharacter.CARRIAGE_RETURN -> {
                seed = parseSeed();
                commands = new StringBuilder("N" + seed + "S");
                initializeWorld();
                state = GameState.GAME;
            }
            case ControlCharacter.ESCAPE -> state = GameState.MAIN_MENU;
            case '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' -> seedEntered.append(c);
        }
    }

    private long parseSeed() {
        if (seedEntered.length() == 0) {
            return 0;
        }
        return Long.parseLong(seedEntered.toString());
    }

    private void processNameEnteringInputChar(char c) {
        switch (c) {
            case ControlCharacter.BACKSPACE -> removeBack(playerNameEntered);
            case ControlCharacter.LINE_FEED,
                    ControlCharacter.FORM_FEED,
                    ControlCharacter.CARRIAGE_RETURN -> {
                HUDPlayerName = playerNameEntered.toString();
                state = GameState.MAIN_MENU;
            }
            case ControlCharacter.ESCAPE -> state = GameState.MAIN_MENU;
            default -> playerNameEntered.append(c);
        }
    }

    private void processGameInputChar(char c) {
        switch (Character.toUpperCase(c)) {
            case 'W' -> {
                commands.append('W');
                moveAvatar(Direction.UP);
            }
            case 'A' -> {
                commands.append('A');
                moveAvatar(Direction.LEFT);
            }
            case 'S' -> {
                commands.append('S');
                moveAvatar(Direction.DOWN);
            }
            case 'D' -> {
                commands.append('D');
                moveAvatar(Direction.RIGHT);
            }
            case ':' -> {
                HUDWarningMessage = "Press (Q) to quit";
                state = GameState.GAME_COLON;
            }
            case ControlCharacter.ESCAPE -> {
                autoSave();
                state = GameState.MAIN_MENU;
            }
        }
    }

    private void processGameColonInputChar(char c) {
        state = GameState.GAME;
        switch (Character.toUpperCase(c)) {
            case 'W' -> {
                commands.append('W');
                moveAvatar(Direction.UP);
            }
            case 'A' -> {
                commands.append('A');
                moveAvatar(Direction.LEFT);
            }
            case 'S' -> {
                commands.append('S');
                moveAvatar(Direction.DOWN);
            }
            case 'D' -> {
                commands.append('D');
                moveAvatar(Direction.RIGHT);
            }
            case 'Q' -> saveAndQuit();
            case ':' -> state = GameState.GAME_COLON;
        }
    }

    private void processReplayModeInputChar(char c) {
        switch (c) {
            case '+' -> worldEvolver.speedUp();
            case '-' -> worldEvolver.slowDown();
            case ControlCharacter.ESCAPE -> System.out.println("TODO: implement replay escape!");
        }
    }

    private void processMouse() {
        if (!gameRunning()) {
            return;
        }
        Point mousePos = getMousePos();
        Point tilePos = new Point(mousePos, 0, -HUD_HEIGHT);
        TETile mouseHoverTile = world.tileAt(tilePos);
        if (mouseHoverTile == null) {
            HUDTileDescription = "";
        } else {
            HUDTileDescription = "You see " + mouseHoverTile.description();
        }
    }

    private boolean gameRunning() {
        return state == GameState.GAME
                || state == GameState.GAME_COLON
                || state == GameState.REPLAY_MODE;
    }

    private Point getMousePos() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        return new Point(x,y);
    }

    private void processBackgroundTasks() {
        if (state == GameState.REPLAY_MODE) {
            if (worldEvolver.finished()) {
                HUDWarningMessage = "";
                state = GameState.GAME;
            }
            worldEvolver.evolve();
        }
    }

    private void saveAndQuit() {
        autoSave();
        state = GameState.TERMINATED;
    }

    private void autoSave() {
        try {
            FileWriter fw = new FileWriter(AUTO_SAVE_FILE_NAME);
            fw.write(HUDPlayerName);
            fw.write('\n');
            fw.write(commands.toString());
            fw.close();
        } catch (IOException e) {
            System.out.println("Cannot create auto save file.");
            e.printStackTrace();
        }
    }

    private void loadFromAutoSave() {
        File autoSaveFile = new File(AUTO_SAVE_FILE_NAME);
        Scanner scanner = scannerFor(autoSaveFile);
        if (scanner == null) {
            return;
        }
        HUDPlayerName = scanner.nextLine();
        String commands = scanner.nextLine();
        interactWithInputString(commands);
        scanner.close();
    }

    private void startReplayingAutoSave() {
        File autoSaveFile = new File(AUTO_SAVE_FILE_NAME);
        Scanner scanner = scannerFor(autoSaveFile);
        if (scanner == null) {
            return;
        }
        HUDPlayerName = scanner.nextLine();
        String commands = scanner.nextLine();
        int sIndex = sIndex(commands);
        interactWithInputString(commands.substring(0, sIndex + 1));
        String directionCommands = commands.substring(sIndex + 1);
        worldEvolver = new WorldEvolver(world, directionCommands);
        scanner.close();
        state = GameState.REPLAY_MODE;
    }

    private int sIndex(String commands) {
        for (int i = 0; i < commands.length(); ++i) {
            if (commands.charAt(i) == 'S') {
                return i;
            }
        }
        return -1;
    }

    private Scanner scannerFor(File file) {
        try {
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File " + file.getName() + " not found in getScanner()");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reset the game world with seed.
     */
    private void initializeWorld() {
        world = new World(WORLD_WIDTH, WORLD_HEIGHT, seed);
    }

    private void drawMainMenu() {
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 60));
        StdDraw.text(0.5 * WORLD_WIDTH, 0.7 * WORLD_HEIGHT, "CS 61B: THE GAME");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 24));
        StdDraw.text(0.5 * WORLD_WIDTH, 0.45 * WORLD_HEIGHT, "(N)ew Game");
        StdDraw.text(0.5 * WORLD_WIDTH, 0.38 * WORLD_HEIGHT, "(L)oad");
        StdDraw.text(0.5 * WORLD_WIDTH, 0.31 * WORLD_HEIGHT, "(C)hange Player Name");
        StdDraw.text(0.5 * WORLD_WIDTH, 0.24 * WORLD_HEIGHT, "(V)isualize Last Save");
        StdDraw.text(0.5 * WORLD_WIDTH, 0.17 * WORLD_HEIGHT, "(Q)uit");
    }

    private void drawSeedEntering() {
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 48));
        StdDraw.text(0.5 * WORLD_WIDTH, 0.6 * WORLD_HEIGHT, "ENTER SEED:");
        StdDraw.setFont(new Font("Monaco", Font.ITALIC, 24));
        StdDraw.text(0.5 * WORLD_WIDTH, 0.5 * WORLD_HEIGHT, seedEntered.toString());
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 24));
        StdDraw.text(0.5 * WORLD_WIDTH, 0.3 * WORLD_HEIGHT, "(S)tart");
    }

    private void drawNameEntering() {
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 48));
        StdDraw.text(0.5 * WORLD_WIDTH, 0.6 * WORLD_HEIGHT, "WHO AM I?");
        StdDraw.setFont(new Font("Monaco", Font.ITALIC, 24));
        StdDraw.text(0.5 * WORLD_WIDTH, 0.5 * WORLD_HEIGHT, playerNameEntered.toString());
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 16));
        StdDraw.text(0.5 * WORLD_WIDTH, 0.3 * WORLD_HEIGHT, "Press (Enter/Return) to confirm");
    }

    private void drawGame() {
        ter.renderFrame(world.tiles());
        drawHUD();
    }

    private void drawHUD() {
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.text(0.1 * WORLD_WIDTH, 0.5 * HUD_HEIGHT, "Player: " + HUDPlayerName);
        StdDraw.text(0.5 * WORLD_WIDTH, 0.5 * HUD_HEIGHT, HUDWarningMessage);
        StdDraw.text(0.9 * WORLD_WIDTH, 0.5 * HUD_HEIGHT, HUDTileDescription);
    }

    private void moveAvatar(Direction d) {
        world.moveAvatar(d);
    }

    private void removeBack(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    /**
     * Return a String representation of the game world.
     *
     * @return String representation of the game world.
     */
    @Override
    public String toString() {
        return world.toString();
    }
}
