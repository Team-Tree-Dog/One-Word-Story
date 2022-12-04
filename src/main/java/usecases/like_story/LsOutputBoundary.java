package usecases.like_story;

import usecases.shutdown_server.SsOutputBoundary;

public interface LsOutputBoundary extends SsOutputBoundary {

    /**
     * This method notifies all the necessary actors that the story has been liked
    */
    void likeOutput(LsOutputData data);

}
