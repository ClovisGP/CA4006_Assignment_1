package test;

import java.util.concurrent.TimeUnit;

public class testThread extends Thread {
    private boolean isRunning = true;
    private EmptyVessel comObject = null;
    private timeScheduler scheduler = null;

    public void run() {
      try {
        synchronized (this.comObject) {
          while (this.isRunning) {
            System.out.println("before");
            this.comObject.wait();
            System.out.println("after");
            try {
              TimeUnit.MILLISECONDS.sleep((int)(Math.random() * 1900 + 100));
            } catch (InterruptedException e) {
              System.out.println(e);
            }
            System.out.println("This code is running in a thread");
            this.scheduler.notifyJobFinish();
          }
        }
      } catch (Exception e) {
        System.out.println(e);
      }
    }

    public void exit() {
      this.isRunning = false;
    }

    public void setScheduler(timeScheduler scheduler, EmptyVessel comObject) {
      this.scheduler = scheduler;
      this.comObject = comObject;
    }
  }