package FarmSimulation;

class Dog extends MovingObject {
    private static int nextId = 1;
    private final int id;
    private final int zoneMinX, zoneMaxX, zoneMinY, zoneMaxY;

    public Dog(Farm farm, int x, int y, int sleepTime, int zoneMinX, int zoneMaxX, 
               int zoneMinY, int zoneMaxY) {
        super(farm, x, y, sleepTime);
        this.id = nextId++;
        this.zoneMinX = zoneMinX;
        this.zoneMaxX = zoneMaxX;
        this.zoneMinY = zoneMinY;
        this.zoneMaxY = zoneMaxY;
        Thread.currentThread().setName(String.valueOf(id));
    }

    @Override
    public void run() {
        while (isStillRunning()) {
            try {
                Thread.sleep(sleepTime);
                move();
            } catch (InterruptedException e) {
                break;
            }
        }
    }


    @Override
    protected void tryMove(int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;

        if (newX >= zoneMinX && newX <= zoneMaxX && 
            newY >= zoneMinY && newY <= zoneMaxY) {
            super.tryMove(dx, dy);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
