package usecases.get_most_liked_stories;
import usecases.StoryData;

/**
 * Contains output data for Get Most Liked Stories use case.
 */
public class GmlsOutputData {
    private StoryData[] stories;

    /**
     * Constructor for output data for this use case
     * @param stories the desired range of stories sorted in descending order
     */
    public GmlsOutputData(StoryData[] stories){ this.stories = stories;}

    public StoryData[] getStories() { return this.stories;}

    public void setStories(StoryData[] stories) { this.stories = stories; }
}
