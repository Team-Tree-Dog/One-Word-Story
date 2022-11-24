package usecases.like_story;

public interface LsOutputBoundary {

    /**
     * This method notifies all the necessary actors that the story has been liked
    */
    void likeOutput(LsOutputData data);

}
