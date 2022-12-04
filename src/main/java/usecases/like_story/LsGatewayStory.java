package usecases.like_story;

public interface LsGatewayStory {

    /**
     * This method adds a like to the given story
     * @return success of the operation
     * */
    boolean likeStory(int StoryId);

}
