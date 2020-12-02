package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * TETile[][] representation of the game world.
 *
 * @author Robert Shi, Layne Wei
 * @e-mail robertyishi@berkeley.edu, lengning_wei@berkeley.edu
 */
public class World {
    private static final int NUM_SEGMENT_ROWS = 4;
    private static final int NUM_SEGMENT_COLUMNS = 8;
    private static final double MIN_ROOM_WIDTH_EXPAND_RATIO = 0.1;
    private static final double MAX_ROOM_WIDTH_EXPAND_RATIO = 0.6;
    private static final double MIN_ROOM_HEIGHT_EXPAND_RATIO = 0.1;
    private static final double MAX_ROOM_HEIGHT_EXPAND_RATIO = 0.6;

    private final TETile[][] world;
    private Point avatarPos;

    private List<Room> rooms;
    private List<Room> hallways;
    private final Random r;

    public World(int width, int height, long seed) {
        world = new TETile[width][height];
        init();
        r = new Random(seed);
        drawTerrain();
        placeAvatar();
    }

    public TETile tileAt(Point p) {
        if (!isValidPoint(p)) {
            return null;
        }
        return world[p.x][p.y];
    }

    private boolean isValidPoint(Point p) {
        Point worldLowerLeft = new Point(0, 0);
        Point worldUpperRight = new Point(width() - 1, height() - 1);
        Rect entireWorld = new Rect(worldLowerLeft, worldUpperRight);
        return entireWorld.containsPoint(p);
    }

    public TETile[][] tiles() {
        return world;
    }

    public boolean moveAvatar(Direction d) {
        return switch (d) {
            case UP -> moveAvatarUp();
            case LEFT -> moveAvatarLeft();
            case DOWN -> moveAvatarDown();
            case RIGHT -> moveAvatarRight();
        };
    }

    private void init() {
        for (int x = 0; x < world.length; ++x) {
            for (int y = 0; y < world[0].length; ++y) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private boolean moveAvatarUp() {
        Point dest = new Point(avatarPos, 0, 1);
        return moveAvatar(dest);
    }

    private boolean moveAvatarLeft() {
        Point dest = new Point(avatarPos, -1, 0);
        return moveAvatar(dest);
    }

    private boolean moveAvatarDown() {
        Point dest = new Point(avatarPos, 0, -1);
        return moveAvatar(dest);
    }

    private boolean moveAvatarRight() {
        Point dest = new Point(avatarPos, 1, 0);
        return moveAvatar(dest);
    }

    private boolean moveAvatar(Point dest) {
        if (avatarBlocked(dest)) {
            return false;
        }
        world[avatarPos.x][avatarPos.y] = world[dest.x][dest.y];
        world[dest.x][dest.y] = Tileset.AVATAR;
        avatarPos = dest;
        return true;
    }

    private boolean avatarBlocked(Point dest) {
        return !world[dest.x][dest.y].equals(Tileset.FLOOR);
    }

    private int width() {
        return world.length;
    }

    private int height() {
        return world[0].length;
    }

    /**
     * Draw rooms and hallways along with their surrounding walls.
     *
     * Room generation algorithm: <br>
     * 1. Divide the world into segments. Each segment is a rectangular
     * area.<br>
     * 2. Generate a core in each segment.<br>
     * 3. Generate a Room around each core. If the Room generated overlaps
     * with any of the previously generated Rooms, discard the new Room.<br>
     * 4. Connect adjacent Rooms by connecting their cores with hallways.<br>
     * 5. Draw walls of each Room and Hallway.<br>
     * 6. Draw floor of each Room and Hallway, overwriting all existing
     * Tiles.<br>
     */
    private void drawTerrain() {
        List<Point> cores = generateCores();
        this.rooms = generateRooms(cores);
        this.hallways = generateHallways(cores);
        drawWalls();
        drawRooms();
    }

    /** Place the avatar at a random Point inside of a random Room. */
    private void placeAvatar() {
        Room randRoom = rooms.get(RandomUtils.uniform(r, 0, rooms.size()));
        avatarPos = randRoom.randomPointInside(r);
        world[avatarPos.x][avatarPos.y] = Tileset.AVATAR;
    }

    /**
     * Returns width of each segment.
     *
     * @return Width of each segment.
     */
    private int segmentWidth() {
        return width() / NUM_SEGMENT_COLUMNS;
    }

    /**
     * Returns height of each segment.
     *
     * @return Height of each segment.
     */
    private int segmentHeight() {
        return height() / NUM_SEGMENT_ROWS;
    }

    /**
     * Return a List of cores.
     *
     * @return A list of points. Each of which is
     * a core.
     */
    private List<Point> generateCores() {
        List<Point> cores = new ArrayList<>();
        int segmentWidth = segmentWidth();
        int segmentHeight = segmentHeight();
        // TODO: ugly code, rename variables
        Rect rowStart = new Rect(new Point(0, 0), new Point(segmentWidth, segmentHeight));
        Rect currentRegion;
        for (int i = 0; i < NUM_SEGMENT_ROWS; ++i) {
            currentRegion = new Rect(rowStart);
            for (int j = 0; j < NUM_SEGMENT_COLUMNS; ++j) {
                cores.add(generateCore(currentRegion));
                currentRegion = new Rect(currentRegion, segmentWidth, 0);
            }
            rowStart = new Rect(rowStart, 0, segmentHeight);
        }
        Collections.shuffle(cores, r);
        return cores;
    }

     /** Generate a core inside the given region. */
    private Point generateCore(Rect region) {
        return region.randPoint(r);
    }

    /**
     * Generate a List of Rooms. Each room will be generated
     * around a core from the List of cores.
     *
     * @param cores List of cores that will be used to generate
     *              each Room.
     * @return List of Rooms generated.
     */
    private List<Room> generateRooms(List<Point> cores) {
        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < cores.size(); ++i) {
            Room newRoom = generateRoom(cores.get(i));
            if (outOfBounds(newRoom) || overlap(newRoom, rooms)) {
                cores.remove(i);
                --i;
            } else {
                rooms.add(newRoom);
            }
        }
        return rooms;
    }

    /**
     * Generate a Room around the given core, which
     * is a Point.
     *
     * @param core Core Point around which the Room will be
     *             generated.
     * @return A new room generated.
     */
    private Room generateRoom(Point core) {
        Point lowerLeft = generateLL(core);
        Point upperRight = generateUR(core);
        return new Room(lowerLeft, upperRight);
    }

    /** generate Lower Left point of a Room. */
    private Point generateLL(Point core) {
        int xOffset = -randWidthExpansion();
        int yOffSet = -randHeightExpansion();
        return new Point(core, xOffset, yOffSet);
    }

    /** generate Upper Right point of a Room. */
    private Point generateUR(Point core) {
        int xOffset = randWidthExpansion();
        int yOffSet = randHeightExpansion();
        return new Point(core, xOffset, yOffSet);
    }

    /** Returns a random expansion width. */
    private int randWidthExpansion() {
        return RandomUtils.uniform(r, minRoomWidthExpansion(), maxRoomWidthExpansion());
    }

    /** Returns a random expansion height. */
    private int randHeightExpansion() {
        return RandomUtils.uniform(r, minRoomHeightExpansion(), maxRoomHeightExpansion());
    }

    /** Returns the minimum expansion width. */
    private int minRoomWidthExpansion() {
        return (int) (MIN_ROOM_WIDTH_EXPAND_RATIO * segmentWidth());
    }

    /** Returns the maximum expansion width. */
    private int maxRoomWidthExpansion() {
        return (int) (MAX_ROOM_WIDTH_EXPAND_RATIO * segmentHeight());
    }

    /** Returns the minimum expansion height. */
    private int minRoomHeightExpansion() {
        return (int) (MIN_ROOM_HEIGHT_EXPAND_RATIO * segmentHeight());
    }

    /** Returns the maximum expansion height. */
    private int maxRoomHeightExpansion() {
        return (int) (MAX_ROOM_HEIGHT_EXPAND_RATIO * segmentHeight());
    }

    /**
     * Returns true if the Room is out side of the game world
     * and thus cannot be drawn.
     *
     * @param room Room object to check.
     * @return {@code true} if {@code room} is out of bounds,
     * {@code false} otherwise.
     */
    private boolean outOfBounds(Room room) {
        if (room.wall.lowerLeft.x < 0) {
            return true;
        }
        if (room.wall.lowerLeft.y < 0) {
            return true;
        }
        if (room.wall.upperRight.x >= width()) {
            return true;
        }
        if (room.wall.upperRight.y >= height()) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if newRoom overlaps with one of the Rooms
     * in the given List of Rooms.
     *
     * @param newRoom New Room to be checked.
     * @param rooms List of existing Rooms.
     * @return {@code true} if the newRoom overlaps with any of the Rooms
     * in the given List of Rooms, {@code false} otherwise.
     */
    private boolean overlap(Room newRoom, List<Room> rooms) {
        for (Room r : rooms) {
            if (newRoom.wallOverlap(r)) {
                return true;
            }
        }
        return false;
    }

    /** generate hallways based on cores. */
    private List<Room> generateHallways(List<Point> cores) {
        List<Room> hallways = new ArrayList<>();
        for (int i = 1; i < cores.size(); ++i) {
            Point core1 = cores.get(i - 1);
            Point core2 = cores.get(i);
            Point mid = generateMidPoint(core1, core2);
            hallways.add(new Room(core1, mid));
            hallways.add(new Room(mid, core2));
        }
        return hallways;
    }

    /**
     * Returns a helper Point that is located at the corner
     * of the hallway to be generated from core1 to core2.
     */
    private Point generateMidPoint(Point core1, Point core2) {
        if (r.nextBoolean()) {
            return new Point(core1.x, core2.y);
        } else {
            return new Point(core2.x, core1.y);
        }
    }

    /** Draws wall around all rooms. */
    private void drawWalls() {
        for (Room room : rooms) {
            drawWall(room);
        }
        for (Room hallway : hallways) {
            drawWall(hallway);
        }
    }

    /** Draws wall around one room. */
    private void drawWall(Room room) {
        Point drawPoint = new Point(room.wall.lowerLeft);
        int w = room.wall.upperRight.x - room.wall.lowerLeft.x;
        int h = room.wall.upperRight.y - room.wall.lowerLeft.y;
        // TODO: repetitive code
        for (int i = 0; i < w; ++i) {
            world[drawPoint.x][drawPoint.y] = Tileset.WALL;
            ++drawPoint.x;
        }
        for (int i = 0; i < h; ++i) {
            world[drawPoint.x][drawPoint.y] = Tileset.WALL;
            ++drawPoint.y;
        }
        for (int i = 0; i < w; ++i) {
            world[drawPoint.x][drawPoint.y] = Tileset.WALL;
            --drawPoint.x;
        }
        for (int i = 0; i < h; ++i) {
            world[drawPoint.x][drawPoint.y] = Tileset.WALL;
            --drawPoint.y;
        }
    }

    /** Draws all Rooms. */
    private void drawRooms() {
        for (Room room : rooms) {
            drawRoom(room);
        }
        for (Room hallway : hallways) {
            drawRoom(hallway);
        }
    }

    /** Draws one Room. */
    private void drawRoom(Room room) {
        Point drawPoint = new Point(room.floor.lowerLeft);
        while (drawPoint.y <= room.floor.upperRight.y) {
            while (drawPoint.x <= room.floor.upperRight.x) {
                world[drawPoint.x][drawPoint.y] = Tileset.FLOOR;
                ++drawPoint.x;
            }
            drawPoint.x = room.floor.lowerLeft.x;
            ++drawPoint.y;
        }
    }
}
