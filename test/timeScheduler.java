package test;

import java.util.concurrent.*;
import java.util.ArrayList;

public class timeScheduler extends Thread {
    private boolean isRunning = true;
    private Integer tickIncre = 0;
    private Integer tickUnit = 1000;
    
    private ArrayList<testThread> threadList = null;
    private Integer countCurrent = 0;
    private Integer countMax = 0;

    private CyclicBarrier latch = new CyclicBarrier(3);

    public timeScheduler(ArrayList<testThread> threadList) {
        this.threadList = threadList;
        this.countMax = this.threadList.size();

        for (testThread currentThread : this.threadList) {
            currentThread.setScheduler(this, latch);
        }
    }

    public void run() {
        try {
            for (testThread currentThread : this.threadList) {
                currentThread.start();
            }
            this.latch.await();
            latch.reset();

            TimeUnit.MILLISECONDS.sleep((int)(20));
            System.out.println("Here");
            
            while (this.isRunning) {
                System.out.println("Here");
                try {
                    TimeUnit.MILLISECONDS.sleep(this.tickUnit);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                System.out.println("Here");
                while (!(this.countMax <= this.countCurrent)) {
                    System.out.println(this.countCurrent);
                }
                this.countCurrent = 0;
                this.tickIncre++;
                this.latch.await();
                if (this.tickIncre >= 10) {
                    exit();
                }
                TimeUnit.MILLISECONDS.sleep((int)(20));
                latch.reset();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void exit() {
        this.isRunning = false;
        for (testThread currentThread : this.threadList) {
            currentThread.exit();
        }
    }

    public void notifyJobFinish() {
        this.countCurrent++;
    }
}
