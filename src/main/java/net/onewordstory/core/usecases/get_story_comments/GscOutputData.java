package net.onewordstory.core.usecases.get_story_comments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.onewordstory.core.usecases.CommentRepoData;
import net.onewordstory.core.usecases.Response;

import java.util.List;

/**
 * Output data for Get Story Comments use case
 */
public class GscOutputData {

    private final List<CommentRepoData> comments;
    private final Response res;

    /**
     * Constructor for GscOutputData
     * @param comments all the comments from a specific story
     * @param res the response, describing what the response was when getStoryComments was called
     */
    public GscOutputData(@Nullable List<CommentRepoData> comments, @NotNull Response res) {

        this.comments = comments;
        this.res = res;
    }

    @NotNull
    public Response getRes() { return res; }

    /**
     * @return list of comment data, or null if the Response is a fail code
     */
    @Nullable
    public List<CommentRepoData> getComments() {
        return comments;
    }
}
