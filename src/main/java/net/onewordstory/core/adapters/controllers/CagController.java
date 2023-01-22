package net.onewordstory.core.adapters.controllers;

import net.onewordstory.core.adapters.presenters.CagPresenter;
import net.onewordstory.core.adapters.view_models.CagViewModel;
import net.onewordstory.core.usecases.comment_as_guest.CagInputBoundary;
import net.onewordstory.core.usecases.comment_as_guest.CagInputData;

public class CagController {

    private final CagInputBoundary inputBoundary;

    /**
     * Constructor for CagController
     * @param inputBoundary input boundary for the Comment As Guest use case
     */
    public CagController(CagInputBoundary inputBoundary) { this.inputBoundary = inputBoundary; }

    /**
     * Takes in information about a comment and adds it to a specified story
     * @param displayName chosen display name of the guest who wrote the comment
     * @param comment the comment the guest wrote
     * @param storyId the unique id of the story the comment is under
     */
    public CagViewModel commentAsGuest(String displayName, String comment, int storyId) {

        CagInputData inputData = new CagInputData(displayName, comment, storyId);
        CagViewModel viewM = new CagViewModel();
        CagPresenter pres = new CagPresenter(viewM);
        inputBoundary.commentAsGuest(inputData, pres);
        return viewM;
    }
}
