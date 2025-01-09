package FarmSimulation;

class Sheep extends MovingObject {
    private static char nextName = 'A';
    private final char name;

    public Sheep(Farm farm, int x, int y, int sleepTime) {
        super(farm, x, y, sleepTime);
        this.name = nextName++;
        Thread.currentThread().setName(String.valueOf(name));
    }

    @Override
    public void run() {
        while (isStillRunning()) {
            try {
                Thread.sleep(sleepTime);
                moveAwayFromDogs();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void moveAwayFromDogs() {
        boolean[] dogPresent = new boolean[8];
        
        dogPresent[0] = farm.isDogAt(x, y - 1);
        dogPresent[1] = farm.isDogAt(x + 1, y - 1);
        dogPresent[2] = farm.isDogAt(x + 1, y);
        dogPresent[3] = farm.isDogAt(x + 1, y + 1);
        dogPresent[4] = farm.isDogAt(x, y + 1);
        dogPresent[5] = farm.isDogAt(x - 1, y + 1);
        dogPresent[6] = farm.isDogAt(x - 1, y);
        dogPresent[7] = farm.isDogAt(x - 1, y - 1);

        int dx = 0, dy = 0;

        if (dogPresent[7] || dogPresent[0] || dogPresent[1]) {
            dy = 1;
        }
        else if (dogPresent[5] || dogPresent[4] || dogPresent[3]) {
            dy = -1;
        }

        if (dogPresent[7] || dogPresent[6] || dogPresent[5]) {
            dx = 1;
        }
        else if (dogPresent[1] || dogPresent[2] || dogPresent[3]) {
            dx = -1;
        }

        if (dx == 0 && dy == 0) {
            do {
                dx = random.nextInt(3) - 1;
                dy = random.nextInt(3) - 1;
            } while (dx == 0 && dy == 0);
        }

        tryMove(dx, dy);
    }


    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
