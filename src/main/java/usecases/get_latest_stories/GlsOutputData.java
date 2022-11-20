package usecases.get_latest_stories;

import usecases.StoryData;

/**
 * Output data class of Get Latest Stories use-case
 */
public class GlsOutputData {

    private final StoryData[] stories;

    /**
     * Constructor for GlsOutputData
     * @param stories StoryData
     */
    public GlsOutputData (StoryData[] stories) {
        this.stories = stories;
    }

    /**
     * Getter for GlsOutputData
     * @return  data.numToGet StoryData retrieved from repo
     */
    public StoryData[] getStories(){
        return this.stories;
    }
}