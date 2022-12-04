package usecases.get_latest_stories;

import usecases.Response;
import usecases.StoryData;

/**
 * Output data class of Get Latest Stories use-case
 */
public class GlsOutputData {

    private final StoryData[] stories;
    private final Response res;

    /**
     * Constructor for GlsOutputData
     * @param stories StoryData
     */
    public GlsOutputData (StoryData[] stories, Response res) {
        this.stories = stories;
        this.res = res;
    }

    /**
     * Getter for GlsOutputData
     * @return  data.numToGet StoryData retrieved from repo
     * sorted from latest date to earliest public date
     */
    public StoryData[] getStories(){
        return this.stories;
    }

    public Response getRes() { return res; }
}
