package stuff;
import java.util.concurrent.*;

public abstract class SynchronizedThread extends Thread {
    private boolean isRunning = true;
    private CyclicBarrier barrier;
    protected TimeScheduler scheduler;

    @Override
    public void run() {
        try {
            while (this.isRunning) {
                this.barrier.await();
                this.doWork();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    protected abstract void doWork();

    public void exitThread() {
        this.isRunning = false;
    }

    public void setupThread(TimeScheduler scheduler, CyclicBarrier barrier) {
        this.scheduler = scheduler;
        this.barrier = barrier;
    }
}