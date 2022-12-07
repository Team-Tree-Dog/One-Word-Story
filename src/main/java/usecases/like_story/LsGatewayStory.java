package usecases.like_story;

import org.jetbrains.annotations.NotNull;
import usecases.Response;

public interface LsGatewayStory {

    /**
     * This method adds a like to the given story
     * @return success of the operation or a fail code
     * */
    @NotNull
    Response likeStory(int storyId);

}
