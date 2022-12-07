package adapters.presenters;

import adapters.view_models.GmlsViewModel;
import usecases.get_most_liked_stories.GmlsOutputBoundary;
import usecases.get_most_liked_stories.GmlsOutputData;

public class GmlsPresenter implements GmlsOutputBoundary {

    private final GmlsViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public GmlsPresenter(GmlsViewModel viewM) { this.viewM = viewM; }

    /**
     * Update the view model with these stories
     * @param data the output data with which to update the view model
     */
    @Override
    public void putStories(GmlsOutputData data) {

    }

    @Override
    public void outputShutdownServer() {

    }
}
