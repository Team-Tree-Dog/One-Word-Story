package usecases.get_latest_stories;

import usecases.StoryData;
import java.util.Arrays;

/**
 * Output data class of Get Latest Stories use-case
 */

public class GlsGatewayOutputData {
    private final StoryData[] stories;

    /**
     * Constructor for GlsGatewayOutputData
     * @param stories StoryData
     */
    public GlsGatewayOutputData (StoryData[] stories) {
        this.stories = stories;
    }

    /**
     * Getter for GlsGatewayOutputData
     * @return all StoryData from the repo
     */
    public StoryData[] getStories(){
        return this.stories;
    }
}
