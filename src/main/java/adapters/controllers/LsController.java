package adapters.controllers;

import adapters.presenters.LsPresenter;
import adapters.view_models.LsViewModel;
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
     * @return View model for this use case
     * */
    public LsViewModel likeStory(String requestId, int storyId) {
        LsInputData inputData = new LsInputData(requestId, storyId);
        LsViewModel viewM = new LsViewModel();
        LsPresenter pres = new LsPresenter(viewM);
        inputBoundary.likeStory(inputData, pres);
        return viewM;
    }

}
