package byow.Core;

import java.util.Random;

public class EngineLauncher {
    public static void main(String[] args) {
        Engine e = new Engine();
        e.interactWithKeyboard();
//        e.interactWithInputString("n13s");
    }

    private static void randTest() {
        Engine e = new Engine();
        Random r = new Random(System.currentTimeMillis());
        e.interactWithInputString("n" + r.nextInt() + "s");
        e.renderFrame();
        for (int i = 0; i < 1000; ++i) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            e.interactWithInputString("tn" + r.nextInt() + "s");
            e.renderFrame();
        }
    }
}
