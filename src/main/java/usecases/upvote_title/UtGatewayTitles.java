package usecases.upvote_title;

import usecases.Response;

public interface UtGatewayTitles {

    Response upvoteTitle(int storyId, String titleToUpvote);
}
