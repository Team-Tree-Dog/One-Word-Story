package usecases.like_story;

public interface LsGatewayStory {

    /**
     * @param storyId unique primary key ID of story to which to add a like
     * @return success of adding a like to the requested story
     */
    boolean likeStory(int storyId);

}
