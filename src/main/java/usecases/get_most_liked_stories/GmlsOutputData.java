package usecases.get_most_liked_stories;
import org.jetbrains.annotations.Nullable;
import usecases.Response;
import usecases.StoryRepoData;

import java.util.List;

/**
 * Contains output data for Get Most Liked Stories use case.
 */
public class GmlsOutputData {
    private final List<StoryRepoData> stories;
    private final Response res;

    /**
     * Constructor for output data for this use case
     * @param stories the desired range of stories sorted in descending order, or null if repo failed
     */
    public GmlsOutputData(@Nullable List<StoryRepoData> stories, Response res) {
        this.stories = stories;
        this.res = res;
    }

    /**
     * @return retrieved stories, or null if repo failed
     */
    @Nullable
    public List<StoryRepoData> getStories() { return this.stories;}

    public Response getRes() { return res; }
}
