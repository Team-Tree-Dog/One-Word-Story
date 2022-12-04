package usecases.like_story;

import org.jetbrains.annotations.NotNull;
import usecases.Response;

public interface LsGatewayStory {

    /**
     * This method adds a like to the given story
     * @param storyId unique primary key ID of story to which to add a like
     * @return success of the operation or a fail code
     * */
    @NotNull
    Response likeStory(int storyId);

}
