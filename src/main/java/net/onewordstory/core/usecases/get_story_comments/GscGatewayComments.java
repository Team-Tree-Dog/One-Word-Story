package net.onewordstory.core.usecases.get_story_comments;

import org.jetbrains.annotations.NotNull;
import net.onewordstory.core.usecases.CommentRepoData;
import net.onewordstory.core.usecases.RepoRes;

/**
 * Gateway for Get Story Comments use case, used by a repository to get all comments from a story
 */
public interface GscGatewayComments {

    /**
     * Gets all comments from the story matching the provided id
     * @param storyId the id for the story comments should be pulled from
     * @return a response containing rows of comment data, or a fail response if repo failed
     */
    @NotNull
    RepoRes<CommentRepoData> getAllComments(int storyId);
}
