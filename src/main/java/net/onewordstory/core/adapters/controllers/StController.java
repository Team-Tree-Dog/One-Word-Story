package net.onewordstory.core.adapters.controllers;

import net.onewordstory.core.adapters.presenters.StPresenter;
import net.onewordstory.core.adapters.view_models.StViewModel;
import net.onewordstory.core.usecases.suggest_title.StInputBoundary;
import net.onewordstory.core.usecases.suggest_title.StInputData;

/**
 * The controller for the Suggested Title use case. Builds input data from user input and uses it to call the method
 * to suggest title on the given input boundary.
 */
public class StController {
    private static StInputBoundary st;

    /**
     * Constructor for StController.
     * @param st the input boundary for the controller
     */
    public StController(StInputBoundary st) {
        this.st = st;
    }

    /**
     * Method for the controller to:
     * 1. Build the input data for the Suggest Title use case
     * 2. Call the method st.suggestTitle() to begin the use case
     * @param storyId           the ID for the story for which the title is suggested
     * @param suggestedTitle    the title that is suggested for the story
     * @return View model for this use case
     */
    public StViewModel suggestTitle(int storyId, String suggestedTitle){
        StViewModel viewM = new StViewModel();
        StPresenter pres = new StPresenter(viewM);
        StInputData inp = new StInputData(suggestedTitle, storyId);
        st.suggestTitle(inp, pres);
        return viewM;
    }
}
