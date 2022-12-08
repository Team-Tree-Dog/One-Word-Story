package adapters.view_models;

import org.jetbrains.annotations.Nullable;
import usecases.Response;

public class LsViewModel extends ViewModel {

    private Response res = null;

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
}
