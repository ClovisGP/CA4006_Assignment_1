package stuff;

import java.util.concurrent.*;

import stuff.SynchronizedThread;
import java.util.ArrayList;
import Entities.Section;


public class TimeScheduler extends Thread {
    private boolean isRunning = true;
    private Integer ticks = 0;
    private Integer tickMilliLength;
    
    private ArrayList<SynchronizedThread> threadList = null;

    private CyclicBarrier barrier = null;
    private boolean setupDone = false;

    public TimeScheduler() {
        this.threadList = new ArrayList<SynchronizedThread>();
        this.tickMilliLength = 100;
    }

    public TimeScheduler(int minimumTickMilliLength) {
        this.threadList = new ArrayList<SynchronizedThread>();
        this.tickMilliLength = minimumTickMilliLength;
    }

    /**
     * Get the Current Tick number.
     * @Return current tick number as Interger
     */
    public Integer getTickNumber() {
        return ticks;
    }

    public void run() {
        try {
            while (this.isRunning) {
                if (!setupDone) {
                    this.barrier = new CyclicBarrier(this.threadList.size() + 1);
                    for (SynchronizedThread currentThread : this.threadList) {
                        System.out.println("hello");
                        currentThread.setupThread(this, barrier);
                        System.out.println("hello2");
                    }
                    for (SynchronizedThread currentThread : this.threadList) {
                        if (currentThread.getState() == State.NEW) {
                            currentThread.start();
                        }
                    }
                    setupDone = true;
                }
                this.barrier.await();
                this.barrier.reset();
                System.out.println("New tick!");
                this.ticks++;

                TimeUnit.MILLISECONDS.sleep(this.tickMilliLength);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void exitThread() {
        this.isRunning = false;
        for (SynchronizedThread currentThread : this.threadList) {
            currentThread.exitThread();
        }
    }

    public synchronized void addWorker() {
        this.threadList.add(new Assistant());
        this.setupDone = false;
    }

    /**
     * Add a bookstore to the thread list
     * @param sectionList it is the list of the section
     * @param clientSpawnRate it is the spawn rate of a customer
     * @param bowSpawnRate it is the spawn rate of a delivery box
     * @param boxSpawnSize it is the size of a delivery box
     */
    public synchronized void addBookstore(ArrayList<Section> sectionList, Integer clientSpawnRate, Integer bowSpawnRate, Integer boxSpawnSize) {
        this.threadList.add(new Bookstore(sectionList, clientSpawnRate, bowSpawnRate, boxSpawnSize));
        this.setupDone = false;
    }

    public synchronized void removeWorker() {
        if (this.threadList.size() > 0) {
            this.threadList.remove(this.threadList.size() - 1);
            this.setupDone = false;
        }
    }
}
