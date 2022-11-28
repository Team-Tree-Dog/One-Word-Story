package usecases.get_most_liked_stories;

import usecases.StoryData;

/**
 * The data structure that contains the desired range of stories from the repo sorted in decreasing order
 */
public class GmlsGatewayOutputData {
    private StoryData[] stories;

    /**
     * @param stories all stories outputted to be outputted from the database
     */
    public GmlsGatewayOutputData (StoryData[] stories) {
        this.stories = stories;
    }

    /**
     * @return the desired range of stories by descending likes retrieved from the repo
     */
    public StoryData[] getStories() {
        return stories;
    }
}
