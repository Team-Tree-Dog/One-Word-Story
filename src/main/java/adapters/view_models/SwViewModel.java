package adapters.view_models;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.jetbrains.annotations.Nullable;
import usecases.Response;

public class SwViewModel extends ResponseViewModel {
    private final Awaitable<GameDisplayData> gameData = new Awaitable<>();

    /**
     * Get this object for both setting and getting purposes, from different threads.
     * <br><br>
     * <b>Thread Safety: </b> The GameDisplayData object is immutable and thus is completely safe
     * to get from multiple threads, not counting the setter thread
     * @return The awaitable object wrapping the game display data
     */
    public Awaitable<GameDisplayData> getGameDataAwaitable() {
        return gameData;
    }
}
