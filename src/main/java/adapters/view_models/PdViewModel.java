package adapters.view_models;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.example.Log;
import org.jetbrains.annotations.NotNull;

public class PdViewModel extends ViewModel {

    private PdCallback callback = null;

    public void setCurrentGameState(@NotNull GameDisplayData d) {
        lock.lock();
        if (callback != null) {
            Log.useCaseMsg("PD View Model", "callback not null; calling it with new game data!");
            callback.onUpdate(d);
        }
        lock.unlock();
    }

    /**
     * Thread safe
     * @param callback Pass a callback you want called each time PD updates
     */
    public void injectCallback(PdCallback callback) {
        lock.lock();
        this.callback = callback;
        lock.unlock();
    }
}
