package adapters.view_models;

import adapters.display_data.story_data.StoryDisplayData;
import java.util.List;

/**
 * View model which outputs a list of StoryDisplayData. Used by Gls, Gmls, and
 * any future story retrieval use case
 */
public class StoryListViewModel extends ResponseViewModel {

    private final Awaitable<List<StoryDisplayData>> stories = new Awaitable<>();

    /**
     * Get this object for both setting and getting purposes, from different threads.
     * <br><br>
     * <b>Thread Safety: </b> The StoryRepoData object is read only, but the list is mutable.
     * It is not thread safe if multiple threads mutate the same returned list.
     * @return The awaitable object wrapping the list of database rows for stories. This awaitable
     * is never set if the response code was a fail, so check for null to check that case.
     */
    public Awaitable<List<StoryDisplayData>> getStoriesAwaitable() { return stories; }
}
