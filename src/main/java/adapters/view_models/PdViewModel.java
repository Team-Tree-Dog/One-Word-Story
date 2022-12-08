package adapters.view_models;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PdViewModel extends ViewModel {

    private GameDisplayData currentGameState = null;

    public void setCurrentGameState(@NotNull GameDisplayData d) {
        lock.lock();
        currentGameState = d;
        condition.signal();
        lock.unlock();
    }

    /**
     * null only if there has not ever been a game started yet. Once a game starts, this object
     * will be updated continuously, but even once a game ends, the object will not be set to null
     * and will linger. View must listen for PGE to know that a game ended
     */
    @Nullable
    public GameDisplayData getCurrentGameState() {
        GameDisplayData out;
        lock.lock();
        // Note that normally, we don’t want to return the object reference directly since we cannot
        // guarantee that the view will engage locks as needed. However, in this case,
        // currentGameState is either null or GameDisplayData, which is an immutable / readonly
        // datatype. As a result, there is no danger since currentGameState will be set to a
        // completely different object each time
        out = currentGameState;
        lock.unlock();
        return out;
    }
}
