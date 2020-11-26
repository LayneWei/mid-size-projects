package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

/**
 * Created by hug.
 * Modified by Robert Shi for project 3
 */
public class KeyboardInputSource implements InputSource {
    public Character getNextKey() {
        if (StdDraw.hasNextKeyTyped()) {
            return StdDraw.nextKeyTyped();
        }
        return null;
    }

    public boolean possibleNextInput() {
        return true;
    }
}
