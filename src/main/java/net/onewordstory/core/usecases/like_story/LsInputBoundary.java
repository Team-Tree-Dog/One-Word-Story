package net.onewordstory.core.usecases.like_story;

public interface LsInputBoundary {

    /**
     * This is the main method used for adding likes to stories
     * */
    void likeStory(LsInputData data, LsOutputBoundary pres);

}
