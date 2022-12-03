package adapters.controllers;

import usecases.like_story.LsInputBoundary;
import usecases.like_story.LsInputData;

/**
 * The controller for the "like-story" use case
 * */
public class LsController {

    private final LsInputBoundary inputBoundary;

    /**
     * This constructor takes and assigns the input boundary for the "like-story" use case
     * @param inputBoundary The like-story input boundary
     * */
    public LsController(LsInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    /**
     * This method takes in the information about the story and adds like to it
     * @param requestId The request's id
     * @param storyId The story's id
     * */
    public void likeStory(String requestId, int storyId) {
        LsInputData inputData = new LsInputData(requestId, storyId);
        inputBoundary.likeStory(inputData);
    }

}
