package adapters.view_models;

import adapters.display_data.GameEndPlayerDisplayData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PgeViewModel extends ViewModel {

    private PgeCallback callback = null;

    public void setEndGameData(@NotNull Map<String, GameEndPlayerDisplayData> d) {
        lock.lock();
        if (callback != null) callback.onGameEnd(d);
        lock.unlock();
    }

    /**
     * Inject a callback which will be called from now on each time a game ends.
     * Injecting a new callback will override the old one. Passing null will
     * erase the old callback. The callback will be called for all future
     * game end events after injection, but for none of the previous ones.
     * <br><br>
     * Thread Safe. This method engages the view model locks
     * @param callback The method you'd like executed each time a game ends
     */
    public void injectCallback(@Nullable PgeCallback callback) {
        lock.lock();
        this.callback = callback;
        lock.unlock();
    }
}
