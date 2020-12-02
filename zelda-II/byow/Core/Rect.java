package byow.Core;

import java.util.Random;

/**
 * A rectangular region of TETiles represented by its
 * lower left Point and upper right Point.
 *
 * @author Robert Shi, Layne Wei
 * @e-mail robertyishi@berkeley.edu, lengning_wei@berkeley.edu
 */
public class Rect {
    Point lowerLeft;
    Point upperRight;

    public Rect(Point corner1, Point corner2) {
        this.lowerLeft = new Point(Math.min(corner1.x, corner2.x),
                Math.min(corner1.y, corner2.y));
        this.upperRight = new Point(Math.max(corner1.x, corner2.x),
                Math.max(corner1.y, corner2.y));
    }

    public Rect(Rect ref, int xOffset, int yOffset) {
        this(ref, xOffset, yOffset, xOffset, yOffset);
    }

    public Rect(Rect ref,
                int lowerLeftXOffset,
                int lowerLeftYOffset,
                int upperRightXOffset,
                int upperRightYOffset) {
        lowerLeft = new Point(ref.lowerLeft, lowerLeftXOffset, lowerLeftYOffset);
        upperRight = new Point(ref.upperRight, upperRightXOffset, upperRightYOffset);
    }

    public Rect(Rect other) {
        this(other, 0, 0, 0, 0);
    }

    /**
     * Returns {@code true} if this Rect contains a common Point
     * with the other Rect
     *
     * @param other The other Rect.
     * @return {@code true} if the two Rect overlaps, {@code false}
     * otherwise.
     */
    public boolean overlap(Rect other) {
        return xOverlap(other) && yOverlap(other);
    }

    private boolean xOverlap(Rect other) {
        Range thisXRange = new Range(this.lowerLeft.x, this.upperRight.x + 1);
        Range otherXRange = new Range(other.lowerLeft.x, other.upperRight.x + 1);
        return thisXRange.overlap(otherXRange);
    }

    private boolean yOverlap(Rect other) {
        Range thisYRange = new Range(this.lowerLeft.y, this.upperRight.y + 1);
        Range otherYRange = new Range(other.lowerLeft.y, other.upperRight.y + 1);
        return thisYRange.overlap(otherYRange);
    }

    /**
     * Returns a random Point inside this Rect.
     *
     * @return A random Point inside this Rect.
     */
    public Point randPoint(Random r) {
        int x = RandomUtils.uniform(r, lowerLeft.x, upperRight.x);
        int y = RandomUtils.uniform(r, lowerLeft.y, upperRight.y);
        return new Point(x, y);
    }

    public boolean containsPoint(Point p) {
        if (p.x < lowerLeft.x) {
            return false;
        }
        if (p.y < lowerLeft.y) {
            return false;
        }
        if (p.x > upperRight.x) {
            return false;
        }
        if (p.y > upperRight.y) {
            return false;
        }
        return true;
    }
}
