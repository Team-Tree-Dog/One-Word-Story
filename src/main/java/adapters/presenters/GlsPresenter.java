package adapters.presenters;

import usecases.get_latest_stories.GlsOutputBoundary;
import usecases.get_latest_stories.GlsOutputData;

public class GlsPresenter implements GlsOutputBoundary {

    /**
     * Notify the view model with the retrieved
     * data.numToGet(nullable) stories
     */
    @Override
    public void putStories(GlsOutputData data) {

    }

    @Override
    public void outputShutdownServer() {

    }
}
