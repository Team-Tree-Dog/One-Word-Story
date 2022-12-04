package usecases.get_story_comments;

import usecases.CommentRepoData;
import usecases.RepoRes;

/**
 * Gateway for Get Story Comments use case, used by a repository to get all comments from a story
 */
public interface GscGatewayComments {

    /**
     * Gets all comments from the story matching the provided id
     * @param storyId the id for the story comments should be pulled from
     * @return a string array containing all the story comments, each one string
     */
    RepoRes<CommentRepoData> getAllComments(int storyId);
}
