package usecases.get_most_liked_stories;

import usecases.Response;
import usecases.StoryRepoData;

/**
 * Contains output data for Get Most Liked Stories use case.
 */
public class GmlsOutputData {
    private StoryRepoData[] stories;
    private Response res;

    /**
     * Constructor for output data for this use case
     * @param stories the desired range of stories sorted in descending order
     */
    public GmlsOutputData(StoryRepoData[] stories, Response res) {
        this.stories = stories;
        this.res = res;
    }

    public StoryRepoData[] getStories() { return this.stories;}

    public Response getRes() { return res; }
}
