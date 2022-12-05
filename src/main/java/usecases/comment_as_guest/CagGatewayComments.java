package usecases.comment_as_guest;

import org.jetbrains.annotations.NotNull;
import usecases.Response;

/**
 * Gateway for Comment As Guest use case, used by a repository storing guest comments
 */
public interface CagGatewayComments {

    /**
     * Method for implementing repository to add comment to the given story
     * @param storyId the id of the story the comment was made under
     * @param displayName the chosen display name of the guest who commented
     * @param comment the comment the guest wrote
     * @return true if adding the comment was successful
     */
    @NotNull
    Response commentAsGuest(int storyId, @NotNull String displayName, @NotNull String comment);
}
