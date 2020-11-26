package byow.Core;

public class WorldEvolver {
    private final World world;
    private long lastEvolveTime;
    private long updatePeriod;
    private int index;
    private final String commands;

    public WorldEvolver(World world, String directionCommands) {
        this.world = world;
        this.lastEvolveTime = System.currentTimeMillis();
        this.updatePeriod = 400;
        this.index = 0;
        this.commands = directionCommands;
    }

    public void speedUp() {
        if (updatePeriod > 50) {
            updatePeriod /= 2;
        }
    }

    public void slowDown() {
        if (updatePeriod < 1000) {
            updatePeriod *= 2;
        }
    }

    public boolean finished() {
        return this.index == commands.length();
    }

    public void evolve() {
        if (finished() || !evoPending()) {
            return;
        }
        switch (commands.charAt(index)) {
            case 'W' -> world.moveAvatar(Direction.UP);
            case 'A' -> world.moveAvatar(Direction.LEFT);
            case 'S' -> world.moveAvatar(Direction.DOWN);
            case 'D' -> world.moveAvatar(Direction.RIGHT);
            default -> throw new RuntimeException("Unknown command char in evolve()");
        }
        ++index;
        lastEvolveTime = System.currentTimeMillis();
    }

    private boolean evoPending() {
        return System.currentTimeMillis() - lastEvolveTime >= updatePeriod;
    }
}
