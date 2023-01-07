package adapters.view_models;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract class for all view models to follow
 */
public abstract class ViewModel {

    protected Lock lock;

    public ViewModel() {
        lock = new ReentrantLock();
    }
}
