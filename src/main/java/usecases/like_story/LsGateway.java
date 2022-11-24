package usecases.like_story;

public interface LsGateway {

    /**
     * This method adds a like to the given story
     * */
    LsGatewayOutputData likeStory(LsGatewayInputData data);

}
