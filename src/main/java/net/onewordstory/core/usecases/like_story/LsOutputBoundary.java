package net.onewordstory.core.usecases.like_story;

import net.onewordstory.core.usecases.shutdown_server.SsOutputBoundary;

public interface LsOutputBoundary extends SsOutputBoundary {

    /**
     * This method notifies all the necessary actors that the story has been liked
    */
    void likeOutput(LsOutputData data);

}
