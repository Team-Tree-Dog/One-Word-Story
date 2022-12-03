package adapters.presenters;

import adapters.ViewModel;
import usecases.get_most_liked_stories.GmlsOutputBoundary;
import usecases.get_most_liked_stories.GmlsOutputData;

public class GmlsPresenter implements GmlsOutputBoundary {

    private final ViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public GmlsPresenter(ViewModel viewM) { this.viewM = viewM; }

    /**
     * Update the view model with these stories
     * @param data the output data with which to update the view model
     */
    @Override
    public void putStories(GmlsOutputData data) {

    }
}
