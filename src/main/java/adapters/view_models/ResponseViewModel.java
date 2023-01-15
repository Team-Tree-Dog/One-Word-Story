package adapters.view_models;

import usecases.Response;

public abstract class ResponseViewModel extends ViewModel {
    private final Awaitable<Response> res = new Awaitable<>();

    /**
     * Get this object for both setting and getting purposes, from different threads.
     * <br><br>
     * <b>Thread Safety: </b> The Response object is immutable and thus is completely safe
     * to get from multiple threads, not counting the setter thread
     * @return The awaitable object wrapping the response
     */
    public Awaitable<Response> getResponseAwaitable() {
        return res;
    }
}
