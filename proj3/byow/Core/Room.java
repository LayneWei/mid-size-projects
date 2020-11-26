package byow.Core;

import java.util.Random;

/**
 * Rectangular room interior specified by the lower left
 * Point and the upper right point.
 *
 * @author Layne Wei, Robert Shi
 * @e-mail lengning_wei@berkeley.edu, robertyishi@berkeley.edu
 */
public class Room {
    final Rect floor;
    final Rect wall;

    public Room(Point corner1, Point corner2) {
        this.floor = new Rect(corner1, corner2);
        this.wall = new Rect(floor, -1, -1, 1, 1);
    }

    /**
     * Returns {@code true} if this Room's wall overlaps with the
     * other Room's wall.
     *
     * @param other The other Room.
     * @return {@code true} if the two Room's walls overlap,
     * {@code false} otherwise.
     */
    public boolean wallOverlap(Room other) {
        return this.wall.overlap(other.wall);
    }

    public Point randomPointInside(Random r) {
        return floor.randPoint(r);
    }
}
