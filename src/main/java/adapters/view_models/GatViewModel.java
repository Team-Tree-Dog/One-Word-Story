package adapters.view_models;

import adapters.display_data.title_data.SuggestedTitleDisplayData;
import java.util.List;

public class GatViewModel extends ResponseViewModel {
    private final Awaitable<List<SuggestedTitleDisplayData>> suggestedTitles = new Awaitable<>();

    /**
     * Get this object for both setting and getting purposes, from different threads.
     * <br><br>
     * <b>Thread Safety: </b> The TitleRepoData object is read only, but the list is mutable.
     * It is not thread safe if multiple threads mutate the same returned list.
     * @return The awaitable object wrapping the list of database rows for suggested titles
     */
    public Awaitable<List<SuggestedTitleDisplayData>> getSuggestedTitlesAwaitable() { return suggestedTitles; }
}
