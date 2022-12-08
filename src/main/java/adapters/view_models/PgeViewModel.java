package adapters.view_models;

import adapters.display_data.GameEndPlayerDisplayData;
import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PgeViewModel extends ViewModel {

    private GameEndPlayerDisplayData[] gameEndPlayerStats = null;

    public void setCurrentGameState(@NotNull GameEndPlayerDisplayData[] d) {
        lock.lock();
        gameEndPlayerStats = d;
        condition.signal();
        lock.unlock();
    }

    /**
     * null if a game has never ended previously.
     * Gets set each time a game ends but never resets to null.
     * View must listen for each time it gets updated to know a game has just ended
     */
    @Nullable
    public GameEndPlayerDisplayData[] getCurrentGameState() {
        GameEndPlayerDisplayData[] out;
        lock.lock();
        //  We will only copy the array and not the objects inside. However, the objects inside the
        //  array have a stats array which can be acquired and modified, and recursive map in that
        //  array can also be technically modified. This is indeed a problem but it would be very
        //  annoying to copy the inner array and the recursive data structure. For this reason, we
        //  will explicitly document this concern and trust that whoever calls this getter will
        //  treat the inner array objects as readonly
        out = gameEndPlayerStats.clone();
        lock.unlock();
        return out;
    }
}
