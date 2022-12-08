package adapters.view_models;

public class SsViewModel extends ViewModel {

    private boolean shutdown = false;

    public void setShutdown() {
        lock.lock();
        shutdown = true;
        condition.signal();
        lock.unlock();
    }

    public boolean getShutdown() {
        boolean out;
        lock.lock();
        out = shutdown;
        lock.unlock();
        return out;
    }
}
