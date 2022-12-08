package adapters.view_models;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract class for all view models to follow
 */
public abstract class ViewModel {

    protected Lock lock;
    protected Condition condition;

    public ViewModel() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void awaitChange() throws InterruptedException {
        lock.lock();
        condition.await();
        lock.unlock();
    }
}
