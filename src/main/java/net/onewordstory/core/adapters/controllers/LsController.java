package net.onewordstory.core.adapters.controllers;

import net.onewordstory.core.adapters.presenters.LsPresenter;
import net.onewordstory.core.adapters.view_models.LsViewModel;
import net.onewordstory.core.usecases.like_story.LsInputBoundary;
import net.onewordstory.core.usecases.like_story.LsInputData;

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
     * @param storyId The story's id
     * @return View model for this use case
     * */
    public LsViewModel likeStory(int storyId) {
        LsInputData inputData = new LsInputData(storyId);
        LsViewModel viewM = new LsViewModel();
        LsPresenter pres = new LsPresenter(viewM);
        inputBoundary.likeStory(inputData, pres);
        return viewM;
    }

}
