package adapters.controllers;

import adapters.presenters.StoryListPresenter;
import adapters.view_models.StoryListViewModel;
import usecases.get_most_liked_stories.GmlsInputBoundary;
import usecases.get_most_liked_stories.GmlsInputData;

public class GmlsController {
    GmlsInputBoundary gmls;

    /**
     * Take in and set an instance of the input boundary that
     * is intended to be called by users from the view
     */
    public GmlsController(GmlsInputBoundary gmls) {
        this.gmls = gmls;
    }

    /**
     * 1. Build the input data for the Get Most Liked Stories use case
     * 2. Call the method gmls.getMostLikedStories() to begin the use case
     * @param lowerRangeInclusive the lower inclusive bound for the range of stories sorted
     *                            in descending order by likes
     * @param upperRangeExclusive the upper inclusive bound for the range of stories sorted
     *                            in descending order by likes
     * @return View model for this use case
     */
    public StoryListViewModel getMostLikedStories(int lowerRangeInclusive, int upperRangeExclusive){
        GmlsInputData inputData = new GmlsInputData(lowerRangeInclusive, upperRangeExclusive);
        StoryListViewModel viewM = new StoryListViewModel();
        StoryListPresenter pres = new StoryListPresenter(viewM);

        gmls.getMostLikedStories(inputData, pres);

        return viewM;
    }
}
