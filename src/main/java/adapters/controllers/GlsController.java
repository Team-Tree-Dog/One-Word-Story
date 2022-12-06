package adapters.controllers;

import adapters.presenters.GlsPresenter;
import adapters.view_models.GlsViewModel;
import adapters.view_models.ViewModel;
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
     */

    public ViewModel getLatestStories(Integer numToget) {
        GlsInputData data = new GlsInputData(numToget);
        GlsViewModel viewM = new GlsViewModel();
        GlsPresenter pres = new GlsPresenter(viewM);

        gls.getLatestStories(data, pres);

        return viewM;
    }
}
