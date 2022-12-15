package adapters.view_models;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.jetbrains.annotations.Nullable;
import usecases.Response;

public class SwViewModel extends ViewModel {
    private Response res = null;
    private GameDisplayData gameData = null;

    public void setGameData(GameDisplayData gameData) {
        lock.lock();
        this.gameData = gameData;
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
     * @return the response from this use case, or null if it hasn't been set yet
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
     * Thread safe
     * @return new game state, or null if EITHER sw hasn't finished executing OR response is an error
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
