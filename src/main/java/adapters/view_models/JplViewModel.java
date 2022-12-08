package adapters.view_models;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.Response;

public class JplViewModel extends ViewModel {

    private GameDisplayData display = null;
    private boolean cancelled = false;
    private Response res = null;

    public void setGameDisplay(@NotNull GameDisplayData data) {
        lock.lock();
        display = data;
        condition.signal();
        lock.unlock();
    }

    public void setCancelled() {
        lock.lock();
        cancelled = true;
        condition.signal();
        lock.unlock();
    }

    public void setResponse(Response response) {
        lock.lock();
        res = response;
        condition.signal();
        lock.unlock();
    }

    @Nullable
    public Response.ResCode getResponseCode() {
        Response.ResCode out;
        lock.lock();
        if (res == null) { out = null;}
        else { out = res.getCode(); }
        lock.unlock();
        return out;
    }

    @Nullable
    public String getResponseMessage() {
        String out;
        lock.lock();
        if (res == null) { out = null; }
        else { out = res.getMessage(); }
        lock.unlock();
        return out;
    }

    public boolean getCancelled() {
        boolean out;
        lock.lock();
        out = cancelled;
        lock.unlock();
        return out;
    }

    @Nullable
    public GameDisplayData getGameState() {
        GameDisplayData out;
        lock.lock();
        out = display;
        lock.unlock();
        return out;
    }
}
