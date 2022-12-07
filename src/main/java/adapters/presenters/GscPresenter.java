package adapters.presenters;

import adapters.ViewModel;
import usecases.get_story_comments.GscOutputBoundary;
import usecases.get_story_comments.GscOutputData;

public class GscPresenter implements GscOutputBoundary {

    private final ViewModel viewM;

    /**
     * Constructor for GscPresenter
     * @param viewM Instance of the view model to write to
     */
    public GscPresenter(ViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify the view model that getting comments from a story was successful/failed
     * If success, pass comments to be updated
     * @param data necessary comment data
     */
    @Override
    public void putStoryComments(GscOutputData data) {

    }

    @Override
    public void outputShutdownServer() {

    }
}
