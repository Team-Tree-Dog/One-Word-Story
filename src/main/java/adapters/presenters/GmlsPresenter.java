package adapters.presenters;

import usecases.get_most_liked_stories.GmlsOutputBoundary;
import usecases.get_most_liked_stories.GmlsOutputData;

public class GmlsPresenter implements GmlsOutputBoundary {
    /**
     * Update the view model with these stories
     * @param data the output data with which to update the view model
     */
    @Override
    public void putStories(GmlsOutputData data) {

    }
}
