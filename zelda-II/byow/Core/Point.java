package byow.Core;

/**
 * @author Robert Shi, Layne Wei
 * @e-mail robertyishi@berkeley.edu, lengning_wei@berkeley.edu
 */
public class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        this(p, 0, 0);
    }

    public Point(Point ref, int xOffset, int yOffset) {
        this.x = ref.x + xOffset;
        this.y = ref.y + yOffset;
    }

    public void move(int xOffset, int yOffset) {
        this.x += xOffset;
        this.y += yOffset;
    }
}
