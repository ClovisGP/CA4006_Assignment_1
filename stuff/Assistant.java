package stuff;

import java.util.concurrent.TimeUnit;

public class Assistant extends SynchronizedThread {
    protected void doWork() {
        int timeToWaste = (int)(Math.random() * 900 + 100);
        try {
            TimeUnit.MILLISECONDS.sleep(timeToWaste);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println("Time wasted = " + timeToWaste);
    }
}
