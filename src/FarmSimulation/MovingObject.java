package FarmSimulation;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class MovingObject extends FarmObject implements Runnable {
    protected int x, y;
    protected Farm farm;
    protected Random random = new Random();
    protected int sleepTime;
    protected volatile boolean running = true;
    protected final AtomicBoolean isRunning = new AtomicBoolean(true);

    public MovingObject(Farm farm, int x, int y, int sleepTime) {
        this.farm = farm;
        this.x = x;
        this.y = y;
        this.sleepTime = sleepTime;
    }

    @Override
    public abstract void run();

    protected void move() {
        int dx, dy;
        do {
            dx = random.nextInt(3) - 1;
            dy = random.nextInt(3) - 1;
        } while (dx == 0 && dy == 0);

        tryMove(dx, dy);
    }

    protected void tryMove(int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;

        if (newX >= 0 && newX < farm.getWidth() && 
            newY >= 0 && newY < farm.getLength()) {
            farm.tryMove(this, newX, newY);
        }
    }

    public void stopRunning() {
        isRunning.set(false);
    }
    
    protected boolean isStillRunning() {
        return isRunning.get() && farm.isRunning();
    }
}
