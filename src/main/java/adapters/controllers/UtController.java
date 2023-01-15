package adapters.controllers;

import adapters.presenters.UtPresenter;
import adapters.view_models.UtViewModel;
import usecases.upvote_title.UtInputBoundary;
import usecases.upvote_title.UtInputData;

public class UtController {
    private UtInputBoundary ut;

    /**
     * Constructor for the upvote title use case controller. Takes in and sets the input boundary for this use case.
     * @param ut    the input boundary for this use case.
     */
    public UtController(UtInputBoundary ut) {this.ut = ut;}

    /**
     * Create the input data for this use case containing the title of the story to be upvoted, the ID of the story,
     * and the ID of this request, and upvote this title.
     * @param storyId       the ID of the story for which a particular title is to be upvoted
     * @param titleToUpvote the title for this story that is to be upvoted
     */
    public UtViewModel upvoteTitle(int storyId, String titleToUpvote){
        UtInputData inputData = new UtInputData(storyId, titleToUpvote);
        UtViewModel viewM = new UtViewModel();
        UtPresenter pres = new UtPresenter(viewM);
        ut.upvoteTitle(inputData, pres);
        return viewM;
    }
}
