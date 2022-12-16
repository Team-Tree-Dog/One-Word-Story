package adapters.view_models;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.jetbrains.annotations.Nullable;
import usecases.Response;

public class DcViewModel extends ViewModel {
    private Response res = null;
    private GameDisplayData gameData = null;

    public void setResponse(Response response) {
        lock.lock();
        res = response;
        condition.signal();
        lock.unlock();
    }

    /**
     * @param gameData new game state after the player disconnected. ONLY NOT NULL when
     *                 the player was successfully disconnected from a GAME
     */
    public void setGameData(@Nullable GameDisplayData gameData) {
        lock.lock();
        this.gameData = gameData;
        lock.unlock();
    }

    /**
     * @return DC response or null if it hasn't been set yet
     */
    @Nullable
    public Response getResponse() {
        Response out;
        lock.lock();
        out = res;
        lock.unlock();
        return out;
    }

    /**
     * @return new game state after the player disconnected. ONLY NOT NULL when
     *        the player was successfully disconnected from a GAME
     */
    @Nullable
    public GameDisplayData getGameData() {
        GameDisplayData out;
        lock.lock();
        out = gameData;
        lock.unlock();
        return out;
    }
}
