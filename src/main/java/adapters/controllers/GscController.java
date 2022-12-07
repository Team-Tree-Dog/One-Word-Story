package adapters.controllers;

import usecases.get_story_comments.GscInputBoundary;
import usecases.get_story_comments.GscInputData;

public class GscController {

    private final GscInputBoundary inputBoundary;

    /**
     * Constructor for GscController
     * @param inputBoundary input boundary for Get Story comments use case
     */
    public GscController(GscInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    /**
     * Takes a story id and retrieves all comments made on the respective story
     * @param storyId id of the story go get comments from
     */
    public void getStoryComments(int storyId) {

        GscInputData inputData = new GscInputData(storyId);
        inputBoundary.getStoryComments(inputData);
    }
}
