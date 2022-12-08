package adapters.view_models;

import org.jetbrains.annotations.Nullable;
import usecases.Response;
import usecases.TitleRepoData;

import java.util.List;

public class GatViewModel extends ViewModel {
    private List<TitleRepoData> stories = null;
    private Response res = null;

    public void setSuggestedTitles(List<TitleRepoData> data) {
        lock.lock();
        stories = data;
        lock.unlock();
    }

    public void setResponse(Response response) {
        lock.lock();
        res = response;
        condition.signal();
        lock.unlock();
    }

    @Nullable
    public List<TitleRepoData> getSuggestedTitles() {
        List<TitleRepoData> out;
        lock.lock();
        out = stories;
        lock.unlock();
        return out;
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
