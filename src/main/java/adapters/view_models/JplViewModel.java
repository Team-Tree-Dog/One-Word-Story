package adapters.view_models;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.example.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.Response;

public class JplViewModel extends ViewModel {

    private GameDisplayData display = null;
    private boolean cancelled = false;
    private Response res = null;
    private JplCallback callback = null;

    /**
     * THIS METHOD MUST BE CALLED AT MOST ONCE FOR A JplViewModel INSTANCE
     * <br><br>
     * IF THIS METHOD IS CALLED, setCancelled MUST NOT BE CALLED
     * @param data game display data output from JPL
     */
    public void setGameDisplay(@NotNull GameDisplayData data) {
        lock.lock();
        display = data;
        if (callback != null) {
            Log.useCaseMsg("JPL View Model", "setGameDisplay triggered; Calling callback");
            callback.onUpdate(false, data);
        }
        lock.unlock();
    }

    /**
     * THIS METHOD MUST BE CALLED AT MOST ONCE FOR A JplViewModel INSTANCE
     * <br><br>
     * IF THIS METHOD IS CALLED, setGameDisplay MUST NOT BE CALLED
     */
    public void setCancelled() {
        lock.lock();
        cancelled = true;
        if (callback != null) {
            Log.useCaseMsg("JPL View Model", "setCancelled triggered; Calling callback");
            callback.onUpdate(true, null);
        }
        lock.unlock();
    }

    public void setResponse(Response response) {
        lock.lock();
        res = response;
        condition.signal();
        lock.unlock();
    }

    /**
     * Thread safe
     * @return Initial response from JPL indicating if the player made it into the pool
     */
    @Nullable
    public Response getResponse() {
        Response out;
        lock.lock();
        // Response is readonly so we don't care to copy it. It'll be thread safe as is
        out = res;
        lock.unlock();
        return out;
    }

    /**
     * Inject a callback which will be called once second stage data is set; that is, once
     * the corresponding player has either cancelled or been added to a game
     * <br><br>
     * If the second stage data has already been set when callback is injected,
     * callback is called immediately. Otherwise, the callback will trigger
     * once the data arrives
     * <br><br>
     * If this method is called when inPool returned a fail, the callback will NEVER trigger
     * @param callback the callback yuh
     */
    public void injectCallback(JplCallback callback) {
        lock.lock();
        if (cancelled || display != null) {
            Log.useCaseMsg("JPL View Model",
                    "Second stage data already set, calling callback immediately");
            callback.onUpdate(cancelled, display);
        } else {
            this.callback = callback;
        }
        lock.unlock();
    }
}
