package adapters.view_models;

public class SsViewModel extends ViewModel {

    private final Awaitable<Boolean> shutdown = new Awaitable<>();

    /**
     * Get this object for both setting and getting purposes, from different threads.
     * <br><br>
     * <b>Thread Safety: </b> A boolean is immutable, so its fully safe
     * @return The awaitable object wrapping the boolean which indicates if the server was
     * shutdown successfully or not
     */
    public Awaitable<Boolean> getIsShutdownAwaitable() {
        return shutdown;
    }
}
