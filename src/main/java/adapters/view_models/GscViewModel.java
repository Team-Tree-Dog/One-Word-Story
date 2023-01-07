package adapters.view_models;

import adapters.display_data.comment_data.CommentDisplayData;
import java.util.List;

public class GscViewModel extends ResponseViewModel {
    private final Awaitable<List<CommentDisplayData>> comments = new Awaitable<>();

    /**
     * Get this object for both setting and getting purposes, from different threads.
     * <br><br>
     * <b>Thread Safety: </b> The CommentRepoData object is read only, but the list is mutable.
     * It is not thread safe if multiple threads mutate the same returned list.
     * @return The awaitable object wrapping the list of database rows for comments
     */
    public Awaitable<List<CommentDisplayData>> getCommentsAwaitable() { return comments; }
}
