package usecases;

import usecases.shutdown_server.SsOutputBoundary;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The base abstract class for all the use case threads
 */
public abstract class InterruptibleThread extends Thread {

    protected final AtomicBoolean blockInterrupt = new AtomicBoolean(false);
    protected final ThreadRegister register;
    protected final SsOutputBoundary outputBoundary;

    public InterruptibleThread(ThreadRegister register, SsOutputBoundary outputBoundary) {
        this.register = register;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void run() {
        try {
            threadLogic();
        } catch (InterruptedException exception) {
            outputBoundary.outputShutdownServer();
        } finally {
            register.removeThread(this);
        }
    }

    /**
     * The core logic of the use case thread
     */
    protected abstract void threadLogic() throws InterruptedException;

    /**
     * Sets the <code>blockInterrupt</code> field to the given value
     * @param value The value to be set
     */
    public void setBlockInterrupt(boolean value) {
        blockInterrupt.set(value);
    }

    /**
     * @return The value of <code>blockInterrupt</code>
     */
    public boolean isBlockInterrupt() {
        return blockInterrupt.get();
    }



}
