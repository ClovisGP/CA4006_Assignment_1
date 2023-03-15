package Entities;

import java.util.concurrent.*;
import java.util.ArrayList;
import Objects.Delivery;
import Objects.Section;
import Tools.StatsManager;


public class TimeScheduler extends Thread {
    private boolean isRunning = true;
    private Integer ticks = 0;
    private Integer tickMilliLength;
    
    private ArrayList<SynchronizedThread> threadList = null;

    private CyclicBarrier barrier = null;
    private boolean setupDone = false;

    public TimeScheduler() {
        this.threadList = new ArrayList<SynchronizedThread>();
        this.tickMilliLength = 250;
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

    public Integer getLenghtMillisecOfTick() {
        return tickMilliLength;
    }

    public void setLenghtOfTick(int tickLength) {
        if (tickLength >= 0) this.tickMilliLength = tickLength;
    }

    public void run() {
        try {
            while (this.isRunning) {
                if (!setupDone) {
                    
                    this.barrier = new CyclicBarrier(this.threadList.size() + 1);
                    for (SynchronizedThread currentThread : this.threadList) {
                        currentThread.setupThread(this, barrier);
                    }
                    for (SynchronizedThread currentThread : this.threadList) {
                        if (currentThread.getState() == State.NEW) {
                            currentThread.start();
                        }
                    }
                    setupDone = true;
                }
                this.barrier.await();
                this.ticks++;
                this.barrier.reset();
                //System.out.println("New Tick => " + this.ticks);

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

    public void addAssistant(ArrayList<Section> sectionList, Delivery deliveryArea, int assistantCarryCapacity, int assistantMoveTime, int assistantMovePenaltyPerBook, int assistantTimeInsertBookIntoSection, int assistantBreakTime, int assistantMinTimeBeforeBreak, int assistantMaxTimeBeforeBreak) {
        this.threadList.add(new Assistant(sectionList, deliveryArea, assistantCarryCapacity, assistantMoveTime, assistantMovePenaltyPerBook, assistantTimeInsertBookIntoSection, assistantBreakTime, assistantMinTimeBeforeBreak, assistantMaxTimeBeforeBreak, this.ticks));
        this.setupDone = false;
    }

    /**
     * Add a bookstore to the thread list
     * @param sectionList it is the list of the section
     * @param deliveryArea it is the delivery section
     * @param clientSpawnRate it is the spawn rate of a customer
     * @param bowSpawnRate it is the spawn rate of a delivery box
     */
    public void addBookstore(ArrayList<Section> sectionList, Delivery deliveryArea, Integer clientSpawnRate, Integer bowSpawnRate) {
        this.threadList.add(new Bookstore(sectionList, deliveryArea, clientSpawnRate, bowSpawnRate));
        this.setupDone = false;
    }

    public void addStatsManager(StatsManager manager) {
        this.threadList.add(manager);
        this.setupDone = false;
    }
}
