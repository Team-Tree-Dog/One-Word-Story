package usecases.upvote_title;

import usecases.Response;

/**
 * Gateway for the use case that is implemented by the repository.
 */
public interface UtGatewayTitles {

    /**
     * Method to upvote a title in the repository
     * @param storyId       the ID of the story whose title is to be upvoted
     * @param titleToUpvote the title to be upvoted
     * @return              a Response object that records whether upvoting the title was successful
     */
    Response upvoteTitle(int storyId, String titleToUpvote);
}
