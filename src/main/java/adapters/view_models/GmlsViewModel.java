package adapters.view_models;

import usecases.StoryRepoData;
import java.util.List;

public class GmlsViewModel extends ResponseViewModel {

    private final Awaitable<List<StoryRepoData>> stories = new Awaitable<>();

    /**
     * Get this object for both setting and getting purposes, from different threads.
     * <br><br>
     * <b>Thread Safety: </b> The StoryRepoData object is read only, but the list is mutable.
     * It is not thread safe if multiple threads mutate the same returned list.
     * @return The awaitable object wrapping the list of database rows for stories
     */
    public Awaitable<List<StoryRepoData>> getStoriesAwaitable() { return stories; }
}
