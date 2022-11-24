package adapters.controllers;

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
     * Provide the number(otherwise null) latest stories one wants to get from the repo
     * @param numToget number of latest stories to get
     */

    public void getLatestStories(Integer numToget) {
        gls.getLatestStories( new GlsInputData(numToget));
    }
}
