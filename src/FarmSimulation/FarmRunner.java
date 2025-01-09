package FarmSimulation;

public class FarmRunner {
    public static void main(String[] args) {
        System.out.println("=== Starting Farm Simulation ===");

        Farm farm = new Farm(14, 14, 10, 5);
        farm.start();

        while (farm.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}