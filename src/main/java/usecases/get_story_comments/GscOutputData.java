package usecases.get_story_comments;

import usecases.CommentRepoData;
import usecases.Response;

import java.util.List;

/**
 * Output data for Get Story Comments use case
 */
public class GscOutputData {

    private List<CommentRepoData> comments;
    private Response res;

    /**
     * Constructor for GscOutputData
     * @param comments all the comments from a specific story
     * @param res the response, describing what the response was when getStoryComments was called
     */
    public GscOutputData(List<CommentRepoData> comments, Response res) {

        this.comments = comments;
        this.res = res;
    }
}
