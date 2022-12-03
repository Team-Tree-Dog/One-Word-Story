package adapters.presenters;

import adapters.ViewModel;
import usecases.get_latest_stories.GlsOutputBoundary;
import usecases.get_latest_stories.GlsOutputData;

public class GlsPresenter implements GlsOutputBoundary {

    private final ViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public GlsPresenter(ViewModel viewM) { this.viewM = viewM; }

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
