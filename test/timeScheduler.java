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

    private tonavenir comObject = new tonavenir();

    public timeScheduler(ArrayList<testThread> threadList) {
        this.threadList = threadList;
        this.countMax = this.threadList.size();

        for (testThread currentThread : this.threadList) {
            currentThread.setScheduler(this);
        }
    }

    public void run() {
        synchronized (comObject) {
            for (testThread currentThread : this.threadList) {
                currentThread.start();
            }
            try {
                TimeUnit.MILLISECONDS.sleep((int)(1000));
              } catch (InterruptedException e) {
                System.out.println(e);
              }
            comObject.notifyAll();
            while (this.isRunning) {
                try {
                    TimeUnit.MILLISECONDS.sleep(this.tickUnit);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                while (!(this.countMax <= this.countCurrent)) {
                }
                this.countCurrent = 0;
                this.tickIncre++;
                comObject.notifyAll();
                if (this.tickIncre >= 10) {
                    exit();
                }
            }
        }
    }

    public void exit() {
        this.isRunning = false;
        for (testThread currentThread : this.threadList) {
            currentThread.exit();
        }
    }

    public synchronized void notifyJobFinish() {
        this.countCurrent++;
    }
}
