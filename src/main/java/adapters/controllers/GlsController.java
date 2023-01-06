package adapters.controllers;

import adapters.presenters.StoryListPresenter;
import adapters.view_models.StoryListViewModel;
import usecases.get_latest_stories.GlsInputBoundary;
import usecases.get_latest_stories.GlsInputData;

public class GlsController {

    private final GlsInputBoundary gls;

    /**
     * Take in and set an instance of the input boundary that
     * is intended to be called by users from the view
     */
    public GlsController(GlsInputBoundary gls) {
        this.gls = gls;
    }

    /**
     * Provide the number(otherwise null) the latest stories one wants to get from the repo
     * @param numToget number of the latest stories to get
     * @return View model for this use case
     */

    public StoryListViewModel getLatestStories(Integer numToget) {
        GlsInputData data = new GlsInputData(numToget);
        StoryListViewModel viewM = new StoryListViewModel();
        StoryListPresenter pres = new StoryListPresenter(viewM);

        gls.getLatestStories(data, pres);

        return viewM;
    }
}
