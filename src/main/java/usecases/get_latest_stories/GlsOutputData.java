package usecases.get_latest_stories;

import usecases.Response;
import usecases.StoryRepoData;

import java.util.List;

/**
 * Output data class of Get Latest Stories use-case
 */
public class GlsOutputData {

    private final List<StoryRepoData> stories;
    private final Response res;

    /**
     * Constructor for GlsOutputData
     * @param stories StoryData
     */
    public GlsOutputData (List<StoryRepoData> stories, Response res) {
        this.stories = stories;
        this.res = res;
    }

    /**
     * Getter for GlsOutputData
     * @return  data.numToGet StoryData retrieved from repo
     * sorted from latest date to earliest public date
     */
    public List<StoryRepoData> getStories(){
        return this.stories;
    }

    public Response getRes() { return res; }
}
