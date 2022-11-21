package usecases.get_most_liked_stories;

/**
 * The data structure that contains the desired range of stories from the repo sorted in decreasing order
 */
public class GmlsGatewayOutputData {
    private StoryData[] stories;

    /**
     * @return the desired range of stories by descending likes retrieved from the repo
     */
    public StoryData[] getStories() {
        return stories;
    }
}
