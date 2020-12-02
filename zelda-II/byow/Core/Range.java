package byow.Core;

/**
 * Left-inclusive-right-exclusive integer range [lo, hi).
 * Overlaps if both ranges include a common integer value.
 *
 * @author Robert Shi, Layne Wei
 * @e-mail robertyishi@berkeley.edu, lengning_wei@berkeley.edu
 */
public class Range {
    final int lo;
    final int hi;

    public Range(int lo, int hi) {
        this.lo = lo;
        this.hi = hi;
    }

    public boolean overlap(Range other) {
        return overlap(this, other);
    }

    public static boolean overlap(Range r1, Range r2) {
        return r1.hi > r2.lo && r2.hi > r1.lo;
    }
}
