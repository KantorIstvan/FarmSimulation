package FarmSimulation;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

class Farm {
    private final int width;
    private final int length;
    private final FarmObject[][] grid;
    private final ReentrantLock[][] locks;
    private final List<Thread> threads = new ArrayList<>();
    private final List<MovingObject> movingObjects = new ArrayList<>();
    private volatile boolean gameOver = false;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    public Farm(int width, int length, int numSheep, int numDogs) {
        this.width = width;
        this.length = length;
        this.grid = new FarmObject[width][length];
        this.locks = new ReentrantLock[width][length];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                locks[i][j] = new ReentrantLock();
            }
        }

        initializeGrid();
        placeGates();
        placeSheepAndDogs(numSheep, numDogs);
    }

    private void initializeGrid() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                if (i == 0 || i == width - 1 || j == 0 || j == length - 1) {
                    grid[i][j] = new Wall();
                } else {
                    grid[i][j] = new Empty();
                }
            }
        }
    }

    private void placeGates() {
        Random random = new Random();
        grid[random.nextInt(width - 2) + 1][0] = new Gate();
        grid[random.nextInt(width - 2) + 1][length - 1] = new Gate();
        grid[0][random.nextInt(length - 2) + 1] = new Gate();
        grid[width - 1][random.nextInt(length - 2) + 1] = new Gate();
    }

    private void placeSheepAndDogs(int numSheep, int numDogs) {
        Random random = new Random();
        int zoneWidth = (width - 2) / 3;
        int zoneLength = (length - 2) / 3;

        for (int i = 0; i < numSheep; i++) {
            int x = random.nextInt(zoneWidth) + zoneWidth + 1;
            int y = random.nextInt(zoneLength) + zoneLength + 1;
            Sheep sheep = new Sheep(this, x, y, 200);
            grid[x][y] = sheep;
            Thread sheepThread = new Thread(sheep);
            threads.add(sheepThread);
            movingObjects.add(sheep);
        }

        for (int i = 0; i < numDogs; i++) {
            int zone = random.nextInt(8);
            int x=0, y=0;
            int zoneMinX=0, zoneMaxX=0, zoneMinY=0, zoneMaxY=0;

            do {
                int zoneX = zone % 3;
                int zoneY = (zone / 3) % 3;
                if (zoneX == 1 && zoneY == 1) continue;

                zoneMinX = zoneX * zoneWidth + 1;
                zoneMaxX = (zoneX + 1) * zoneWidth;
                zoneMinY = zoneY * zoneLength + 1;
                zoneMaxY = (zoneY + 1) * zoneLength;

                x = random.nextInt(zoneMaxX - zoneMinX + 1) + zoneMinX;
                y = random.nextInt(zoneMaxY - zoneMinY + 1) + zoneMinY;
            } while (!(grid[x][y] instanceof Empty));

            Dog dog = new Dog(this, x, y, 200, zoneMinX, zoneMaxX, zoneMinY, zoneMaxY);
            grid[x][y] = dog;
            Thread dogThread = new Thread(dog);
            threads.add(dogThread);
            movingObjects.add(dog);
        }
    }

    public void start() {
        for (Thread thread : threads) {
            thread.start();
        }

        Thread displayThread = new Thread(() -> {
            while (isRunning.get()) {
                try {
                    Thread.sleep(200);
                    display();
                } catch (InterruptedException e) {
                    break;
                }
            }
            display();
        });
        displayThread.start();
        threads.add(displayThread);
    }
    
    public boolean isRunning() {
        return isRunning.get();
    }



    public void tryMove(MovingObject object, int newX, int newY) {
        ReentrantLock currentLock = locks[object.x][object.y];
        ReentrantLock newLock = locks[newX][newY];

        if (currentLock.tryLock()) {
            try {
                if (newLock.tryLock()) {
                    try {
                        if (grid[newX][newY] instanceof Gate && object instanceof Sheep) {
                            System.out.println("\nSheep " + object + " has escaped!");
                            gameOver = true;
                            stopAllThreads();
                            System.exit(0);
                            return;
                        }

                        if (grid[newX][newY] instanceof Empty) {
                            grid[object.x][object.y] = new Empty();
                            grid[newX][newY] = object;
                            object.x = newX;
                            object.y = newY;
                        }
                    } finally {
                        newLock.unlock();
                    }
                }
            } finally {
                currentLock.unlock();
            }
        }
    }

    public boolean isDogAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= length) return false;
        ReentrantLock lock = locks[x][y];
        if (lock.tryLock()) {
            try {
                return grid[x][y] instanceof Dog;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    private void stopAllThreads() {
        isRunning.set(false);
        for (MovingObject obj : movingObjects) {
            obj.stopRunning();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void display() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--------------------\n");
        
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(grid[x][y]).append(" ");
            }
            sb.append("\n");
        }
        sb.append("--------------------");
        
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println(sb.toString());
    }

    public int getWidth() { return width; }
    public int getLength() { return length; }
}
