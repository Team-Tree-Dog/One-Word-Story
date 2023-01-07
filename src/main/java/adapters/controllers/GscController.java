package adapters.controllers;

import adapters.presenters.GscPresenter;
import adapters.view_models.GscViewModel;
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
    public GscViewModel getStoryComments(int storyId) {

        GscInputData inputData = new GscInputData(storyId);
        GscViewModel viewM = new GscViewModel();
        GscPresenter pres = new GscPresenter(viewM);
        inputBoundary.getStoryComments(inputData, pres);
        return viewM;
    }
}
