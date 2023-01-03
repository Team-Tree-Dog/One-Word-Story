package adapters.view_models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Defines the interface for a single set object. That is, an object whose
 * initial attributes are null, and are set ONCE from another thread after
 * some time. For an awaitable, there are two threads involved: the setter
 * thread and the getter thread. The setter thread sets the object once when
 * it is prepared, and the getter thread can await for it to be set and then
 * get it.
 */
public class Awaitable<T> {

    private final Lock lock;
    private final Condition condition;
    private boolean hasSignalled;
    private T content;

    public Awaitable() {
        lock = new ReentrantLock();
        hasSignalled = false;
        condition = lock.newCondition();
        content = null;
    }

    /**
     * @return if the items were set (true) or are still null (false)
     */
    public boolean isSet() {
        return hasSignalled;
    }

    /**
     * Wait for the content to be set. Once the content is set, it is returned
     * @return the content once it is set, or null if the await call was interrupted
     */
    @NotNull
    public T await() throws InterruptedException {
        T out;

        lock.lock();
        try {
            while (!isSet()) {
                condition.await();
            }
            out = content;
        } catch (InterruptedException e) {
            throw new InterruptedException(e.getMessage());
        }
        finally {
            lock.unlock();
        }
        return out;
    }

    /**
     * Signals and sets a boolean to awake any await calls. After the boolean is set,
     * all future await calls return immediately
     */
    private void signal() {
        lock.lock();
        if (!hasSignalled) {
            hasSignalled = true;
            condition.signal();
        }
        lock.unlock();
    }

    /**
     * Set the content of this awaitable and wake anything awaiting it.
     * Do not mutate the object after setting it from the thread which sets it! That is,
     * the content that you pass in to this method to be set should not be mutated.
     * @param content the content to set
     */
    public void set(@NotNull T content) {
        lock.lock();
        this.content = content;
        signal();
        lock.unlock();
    }

    /**
     * Thread safely (with respect to the reference) get the item currently being stored.
     * Note that if the item returned is mutable, any mutation operations are
     * no longer thread safe if this item is retrieved by multiple
     * threads.
     * <br><br>
     * As a rule, the thread which sets the object should not mutate the object
     * so it is safe in that regard
     * @return the item of this Awaitable, or null if not yet set.
     */
    @Nullable
    public T get() {
        lock.lock();
        T out = content;
        lock.unlock();
        return out;
    }
}

