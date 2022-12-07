package usecases.get_story_comments;

/**
 * Input data for Get Story Comments use case
 */
public class GscInputData {

    private int storyId;

    /**
     * Constructor for GscInputData
     * @param storyId the id of the story to get comments from
     */
    public GscInputData(int storyId) { this.storyId = storyId; }

    /**
     * @return story id
     */
    public int getStoryId() { return storyId; }
}
