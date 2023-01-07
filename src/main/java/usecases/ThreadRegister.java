package usecases;

import org.example.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The thread register keeps track of all the running non-daemon threads and is
 * primarily used by the "shutdown-server" use case
 */
public class ThreadRegister {

    private final List<InterruptibleThread> runningThreads;
    private boolean shuttingDown;
    private final Lock shuttingDownLock;

    /**
     * Initiates the thread register
     */
    public ThreadRegister() {
        this.runningThreads = new ArrayList<>();
        this.shuttingDown = false;
        this.shuttingDownLock = new ReentrantLock();
    }


    /**
     * Adds an interruptible thread to the list
     * @param thread The thread to be added
     */
    public boolean registerThread(InterruptibleThread thread) {
        boolean result = false;
        this.shuttingDownLock.lock();
        // I think that this boolean flag might be redundant
        if (!shuttingDown) {
            this.runningThreads.add(thread);
            thread.start();
            result = true;
        }
        this.shuttingDownLock.unlock();
        return result;
    }

    /**
     * Removes the interruptible thread from the list
     * @param thread The thread to be removed
     */
    public boolean removeThread(InterruptibleThread thread) {
        shuttingDownLock.lock();
        boolean result = this.runningThreads.remove(thread);
        shuttingDownLock.unlock();
        return result;
    }

    /**
     * Stops all the thread. If a thread has a flag <code>blockInterrupt</code> set to <code>false</code>,
     * this method spin waits until it is true. Notices that is this flag is never set to false,
     * the server will never be shut down
     */
    public void stopThreads() {
        Log.useCaseMsg("SS", "Wants SHUTDOWN lock");
        shuttingDownLock.lock();
        Log.useCaseMsg("SS", "Got SHUTDOWN lock");
        shuttingDown = true;
        for (InterruptibleThread thread: runningThreads) {
            Log.useCaseMsg("SS", "Waiting on block...");
            while (thread.blockInterrupt.get()) {
                Thread.onSpinWait();
            }
            Log.useCaseMsg("SS", "Finished waiting on block");
            thread.interrupt();
            Log.useCaseMsg("SS", "Thread Interrupted Successfully");
        }
        shuttingDownLock.unlock();
        Log.useCaseMsg("SS", "Released SHUTDOWN lock");
    }
}
